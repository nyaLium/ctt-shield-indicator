package com.nyalium.cttshieldindicator;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

/**
 * 副手槽位上方（或屏幕正中）的盾牌计量条。
 *
 * <p>判定规则（全部只在客户端读取，不需要服务端配合）：</p>
 * <ol>
 *   <li>副手物品非空，且 {@code minecraft:custom_data} 中 {@code shield:1b}
 *       或 {@code shieldAbility:1b} 为真；</li>
 *   <li>物品可损坏（{@code max_damage > 0}）且耐久未满（{@code damage > 0}）；</li>
 *   <li>物品只在副手时显示，主手/背包内不显示。</li>
 * </ol>
 *
 * <p>尺寸按纹理的<b>可见内容</b>算：{@code meter_N.png} 里真正有像素的区域是
 * {@code x 42..93 / y 4..131}（52x128），所以可见宽度 = {@code 20px（一格）× scale}，
 * 高度按 52:128 自动得出。</p>
 *
 * <p>位置由 {@link ShieldIndicatorConfig#position} 决定：默认对齐副手<b>物品格</b>的中心
 * （原版把 16x16 物品画在 {@code center-117}，中心为 {@code center-109}）；垂直方向贴在
 * actionbar 上方，间距由 {@link ShieldIndicatorConfig#gapAboveActionBar} 控制。设为
 * {@link ShieldIndicatorConfig.Position#CENTER} 时改放在整个界面的正中（水平、垂直都居中），
 * 此时间距配置不生效。两种模式都会再叠加 {@link ShieldIndicatorConfig.Profile#offset} 的像素偏移，
 * 并限制在屏幕范围内。透明度通过 {@code drawTexture} 的 ARGB 顶点色实现。</p>
 */
public final class ShieldIndicatorHud implements HudRenderCallback {
    /** 与资源包 {@code textures/item/shields/meter_N.png} 同一份纹理。 */
    private static final Identifier[] METER_TEXTURES = new Identifier[]{
            Identifier.of(CttShieldIndicatorClient.MOD_ID, "textures/gui/meter_0.png"),
            Identifier.of(CttShieldIndicatorClient.MOD_ID, "textures/gui/meter_1.png"),
            Identifier.of(CttShieldIndicatorClient.MOD_ID, "textures/gui/meter_2.png"),
            Identifier.of(CttShieldIndicatorClient.MOD_ID, "textures/gui/meter_3.png")
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

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        ShieldIndicatorConfig config = ShieldIndicatorConfig.get();
        if (!config.enabled) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        ItemStack offhand = player.getOffHandStack();
        if (offhand.isEmpty() || !hasShieldFlag(offhand)) {
            return;
        }
        int maxDamage = offhand.getMaxDamage();
        if (maxDamage <= 0) {
            return;
        }
        int damage = offhand.getDamage();
        if (damage <= 0) {
            // 耐久满时不显示指示器
            return;
        }
        float ratio = (float) damage / (float) maxDamage;

        int guiWidth = context.getScaledWindowWidth();
        int guiHeight = context.getScaledWindowHeight();
        int center = guiWidth / 2;

        // 当前位置各自的一套参数：缩放比例 + 水平偏移。
        ShieldIndicatorConfig.Profile profile = config.activeProfile();

        // 可见内容宽度 = 一格 × 缩放比例；高度按 52:128 自动。
        float slots = profile.scale > 0.0f ? profile.scale : 1.0f;
        float width = Math.max(1.0f, Math.min(400.0f, SLOT_CELL_SIZE * slots));
        float height = width * ((float) CONTENT_H / (float) CONTENT_W);
        float scaleX = width / (float) CONTENT_W;
        float scaleY = height / (float) CONTENT_H;

        // 锚点（内容底边中点）：
        //   副手槽模式 —— 水平对齐副手“物品格”中心（center∓109），垂直贴在 actionbar 上方的固定高度；
        //   屏幕中心模式 —— 水平、垂直都对齐整个界面的中心（不是 actionbar 上方）。
        float anchorX;
        float bottomY;
        if (config.position == ShieldIndicatorConfig.Position.CENTER) {
            anchorX = center;
            bottomY = guiHeight / 2.0f + height / 2.0f;
        } else {
            Arm offhandArm = player.getMainArm().getOpposite();
            anchorX = offhandArm == Arm.LEFT ? center - 109.0f : center + 109.0f;
            bottomY = guiHeight - 72 - config.gapAboveActionBar;
        }

        // 用户偏移 + 边界保护：整条指示器始终留在屏幕内（四周各留 2px）。
        anchorX += profile.offset;
        float halfWidth = width / 2.0f;
        anchorX = Math.max(halfWidth + 2.0f, Math.min(guiWidth - halfWidth - 2.0f, anchorX));
        bottomY = Math.max(height + 2.0f, Math.min(guiHeight - 2.0f, bottomY));

        // ARGB 颜色里的 alpha 实现整体透明度。
        int alpha = Math.round(255.0f * Math.max(0.0f, Math.min(1.0f, config.opacity)));
        int color = (alpha << 24) | 0x00FFFFFF;

        drawTexture(context, METER_TEXTURES[meterIndex(ratio)], anchorX, bottomY, scaleX, scaleY, color);
    }

    /**
     * 以「可见内容」为基准绘制：内容底边中点对齐 {@code (centerX, bottomY)}。
     *
     * <p>画布比内容大一圈（左 42、上 4、右 5、下 4 像素），所以按画布坐标反推绘制位置：
     * 本地左上角 = {@code (centerX - 68, bottomY - 132)}，再靠矩阵缩放把内容拉到目标尺寸。</p>
     */
    private static void drawTexture(DrawContext context, Identifier texture,
                                    float centerX, float bottomY, float scaleX, float scaleY, int color) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(centerX, bottomY, 0.0f);
        matrices.scale(scaleX, scaleY, 1.0f);
        matrices.translate(-centerX, -bottomY, 0.0f);
        int x = Math.round(centerX - CANVAS / 2.0f);
        int y = Math.round(bottomY - CONTENT_BOTTOM);
        context.drawTexture(
                RenderLayer::getGuiTextured,
                texture,
                x, y,
                0.0f, 0.0f,
                CANVAS, CANVAS,
                CANVAS, CANVAS,
                color);
        matrices.pop();
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
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null) {
            return false;
        }
        NbtCompound tag = customData.copyNbt();
        return tag.getBoolean("shield") || tag.getBoolean("shieldAbility");
    }
}
