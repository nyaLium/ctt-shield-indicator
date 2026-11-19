package com.namylium.cttshieldindicator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.joml.Matrix3x2fStack;

/**
 * 副手槽位上方的耐久计量条（盾牌指示器）。
 *
 * <p>判定规则（全部只在客户端读取，不需要服务端配合）：</p>
 * <ol>
 *   <li>副手物品非空，且 {@code minecraft:custom_data} 中 {@code shield:1b}
 *       或 {@code shieldAbility:1b} 为真；</li>
 *   <li>物品可损坏（{@code max_damage > 0}）且耐久未满（{@code damage > 0}）；</li>
 *   <li>物品只在副手时显示，主手/背包内不显示。</li>
 * </ol>
 *
 * <p>尺寸按<b>纹理的可见内容</b>算，而不是把 136x136 的画布塞进某个方框：
 * {@code meter_N.png} 里真正有像素的区域是 {@code x 42..93 / y 4..131}，即 52x128。
 * 所以「可见宽度」= {@code 20px（原版一格） × widthSlots}，可见高度默认按 52:128 的比例自动得出；
 * 想要压扁一点可以显式给 {@code heightSlots}。</p>
 *
 * <p>水平对齐的是副手<b>物品格</b>的中心（原版把 16x16 物品画在 {@code center-117}，中心为
 * {@code center-109}），不是 29 宽的外框中心（{@code center-105.5}）。</p>
 *
 * <p>档位与资源包 {@code items/shield_indicator/enable/*.json} 中的 {@code range_dispatch} 对应
 * （{@code RangeSelectItemModel} 取“阈值 &le; 值”的最后一项）。由于不再绘制盾牌图标，
 * 资源包里 {@code 0 < r < 0.2} 那档“只有盾牌、没有计量条”的状态并入 {@code meter_3}：</p>
 * <pre>
 *   damage / max_damage &gt;= 0.8  -&gt; meter_0（0 格）
 *   damage / max_damage &ge; 0.6  -&gt; meter_1（1 格）
 *   damage / max_damage &ge; 0.4  -&gt; meter_2（2 格）
 *   damage / max_damage &lt; 0.4   -&gt; meter_3（3 格，含 0~0.2 段）
 * </pre>
 */
public final class ShieldIndicatorHud implements HudElement {
    /** 纹理模式的计量条：与资源包 {@code textures/item/shields/meter_N.png} 同一份。 */
    private static final Identifier[] METER_TEXTURES = new Identifier[]{
            Identifier.fromNamespaceAndPath(CttShieldIndicatorClient.MOD_ID, "textures/gui/meter_0.png"),
            Identifier.fromNamespaceAndPath(CttShieldIndicatorClient.MOD_ID, "textures/gui/meter_1.png"),
            Identifier.fromNamespaceAndPath(CttShieldIndicatorClient.MOD_ID, "textures/gui/meter_2.png"),
            Identifier.fromNamespaceAndPath(CttShieldIndicatorClient.MOD_ID, "textures/gui/meter_3.png")
    };

    /** 纹理画布 136x136。 */
    private static final int CANVAS = 136;
    /** 可见内容在画布中的包围盒（左上含、右下不含）：x 42..93 / y 4..131。 */
    private static final int CONTENT_LEFT = 42;
    private static final int CONTENT_TOP = 4;
    private static final int CONTENT_RIGHT = 94;
    private static final int CONTENT_BOTTOM = 132;
    private static final int CONTENT_W = CONTENT_RIGHT - CONTENT_LEFT;
    private static final int CONTENT_H = CONTENT_BOTTOM - CONTENT_TOP;

    /** 原版快捷栏一格的显示尺寸：格距 20px，物品 16px 居中、四周各内缩 2px。 */
    private static final int SLOT_CELL_SIZE = 20;
    /** 模型模式下 meter_N 模型按 16x16 模型空间设计。 */
    private static final int MODEL_BASE = 16;

    private static final Map<String, ItemStack> MODEL_STACKS = new HashMap<>();
    private static final Set<String> MISSING_DEFINITIONS = new HashSet<>();

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        ShieldIndicatorConfig config = ShieldIndicatorConfig.get();
        if (!config.enabled || !config.showMeter) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        ItemStack offhand = player.getOffhandItem();
        if (offhand.isEmpty() || !hasShieldFlag(offhand)) {
            return;
        }
        int maxDamage = offhand.getMaxDamage();
        if (maxDamage <= 0) {
            return;
        }
        int damage = offhand.getDamageValue();
        if (damage <= 0) {
            // 耐久满时不显示指示器
            return;
        }
        float ratio = (float) damage / (float) maxDamage;

        int guiWidth = graphics.guiWidth();
        int guiHeight = graphics.guiHeight();
        int center = guiWidth / 2;

        // 副手“物品格”中心：物品 16x16 画在 center-117 / center+101，中心即 center∓109。
        HumanoidArm offhandArm = player.getMainArm().getOpposite();
        float slotCenterX = offhandArm == HumanoidArm.LEFT ? center - 109.0f : center + 109.0f;

        // actionbar 文本画在 translate(0, guiHeight-68) 之后、本地 y=-4 处，文本顶边约 guiHeight-72。
        float bottomY = guiHeight - 72 - config.gapAboveActionBar;

        // 可见内容的宽度 = 一格 × widthSlots；高度默认按 52:128 自动。
        float slotsW = config.widthSlots > 0.0f ? config.widthSlots : 1.0f;
        float width = Math.max(1.0f, Math.min(400.0f, SLOT_CELL_SIZE * slotsW));
        float height = config.heightSlots > 0.0f
                ? Math.max(1.0f, Math.min(600.0f, SLOT_CELL_SIZE * config.heightSlots))
                : width * ((float) CONTENT_H / (float) CONTENT_W);

        float scaleX = width / (float) CONTENT_W;
        float scaleY = height / (float) CONTENT_H;
        int meterIndex = meterIndex(ratio);

        if ("model".equalsIgnoreCase(config.meterMode)) {
            ItemStack meterModel = stackFor("meter/" + meterIndex);
            if (meterModel != null) {
                // 模型模式保持等比，用宽度比例作为统一缩放
                drawModel(graphics, meterModel, slotCenterX, bottomY, scaleX);
                return;
            }
        }
        drawTexture(graphics, METER_TEXTURES[meterIndex], slotCenterX, bottomY, scaleX, scaleY);
    }

    /**
     * 以「可见内容」为基准绘制纹理：内容底边中点对齐 {@code (centerX, bottomY)}。
     *
     * <p>画布比内容大一圈（左 42、上 4、右 5、下 4 像素），所以按画布坐标反推绘制位置：
     * 本地左上角 = {@code (centerX - 68, bottomY - 132)}，再靠 pose 缩放把内容拉到目标尺寸。</p>
     */
    private static void drawTexture(GuiGraphicsExtractor graphics, Identifier texture,
                                    float centerX, float bottomY, float scaleX, float scaleY) {
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(centerX, bottomY);
        pose.scale(scaleX, scaleY);
        pose.translate(-centerX, -bottomY);
        int x = Math.round(centerX - CANVAS / 2.0f);
        int y = Math.round(bottomY - CONTENT_BOTTOM);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                x, y,
                0.0f, 0.0f,
                CANVAS, CANVAS,
                CANVAS, CANVAS,
                CANVAS, CANVAS);
        pose.popMatrix();
    }

    private static void drawModel(GuiGraphicsExtractor graphics, ItemStack stack,
                                  float centerX, float bottomY, float scale) {
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(centerX, bottomY);
        pose.scale(scale, scale);
        pose.translate(-centerX, -bottomY);
        graphics.item(stack, Math.round(centerX - MODEL_BASE / 2.0f), Math.round(bottomY - MODEL_BASE));
        pose.popMatrix();
    }

    /** 与 range_dispatch 的阈值一一对应。 */
    private static int meterIndex(float ratio) {
        if (ratio >= 0.8f) {
            return 0;
        }
        if (ratio >= 0.6f) {
            return 1;
        }
        if (ratio >= 0.4f) {
            return 2;
        }
        return 3;
    }

    private static boolean hasShieldFlag(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null || customData.isEmpty()) {
            return false;
        }
        CompoundTag tag = customData.copyTag();
        return tag.getBooleanOr("shield", false) || tag.getBooleanOr("shieldAbility", false);
    }

    private static ItemStack stackFor(String path) {
        ItemStack cached = MODEL_STACKS.get(path);
        if (cached != null) {
            return cached;
        }
        if (MISSING_DEFINITIONS.contains(path)) {
            return null;
        }
        Identifier definition = Identifier.fromNamespaceAndPath(CttShieldIndicatorClient.MOD_ID,
                "items/" + path + ".json");
        if (Minecraft.getInstance().getResourceManager().getResource(definition).isEmpty()) {
            MISSING_DEFINITIONS.add(path);
            return null;
        }
        ItemStack stack = new ItemStack(Items.PAPER);
        stack.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(CttShieldIndicatorClient.MOD_ID, path));
        MODEL_STACKS.put(path, stack);
        return stack;
    }
}
