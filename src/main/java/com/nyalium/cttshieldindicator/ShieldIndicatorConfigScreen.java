package com.nyalium.cttshieldindicator;

import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Mod Menu 里的设置界面。
 *
 * <p>直接改 {@link ShieldIndicatorConfig#get()} 里的字段，HUD 每帧读取，所以改完立刻生效；
 * 关闭界面时写回磁盘。</p>
 */
public final class ShieldIndicatorConfigScreen extends Screen {
    private static final Component TITLE = Component.translatable("ctt_shield_indicator.config.title");
    private static final int ROW_WIDTH = 220;
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_GAP = 24;
    private static final int TOP = 36;

    private static final List<Float> WIDTH_VALUES = List.of(0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.4f, 1.6f, 2.0f);
    private static final List<Float> HEIGHT_VALUES = List.of(0.0f, 1.0f, 1.2f, 1.5f, 1.8f, 2.0f, 2.5f, 3.0f);
    private static final List<Integer> GAP_VALUES = List.of(0, 2, 4, 6, 8, 10, 14, 20);
    private static final List<String> MODE_VALUES = List.of("texture", "model");

    private final Screen parent;

    public ShieldIndicatorConfigScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        ShieldIndicatorConfig config = ShieldIndicatorConfig.get();
        int left = this.width / 2 - ROW_WIDTH / 2;
        int y = TOP;

        // 启用
        addRenderableWidget(CycleButton.onOffBuilder(config.enabled)
                .create(left, y, ROW_WIDTH, ROW_HEIGHT,
                        Component.translatable("ctt_shield_indicator.config.enabled"),
                        (button, value) -> config.enabled = value));
        y += ROW_GAP;

        // 显示计量条
        addRenderableWidget(CycleButton.onOffBuilder(config.showMeter)
                .create(left, y, ROW_WIDTH, ROW_HEIGHT,
                        Component.translatable("ctt_shield_indicator.config.show_meter"),
                        (button, value) -> config.showMeter = value));
        y += ROW_GAP;

        // 宽度（以槽位格为单位）
        addRenderableWidget(CycleButton.<Float>builder(ShieldIndicatorConfigScreen::slotsLabel, config.widthSlots)
                .withValues(WIDTH_VALUES)
                .create(left, y, ROW_WIDTH, ROW_HEIGHT,
                        Component.translatable("ctt_shield_indicator.config.width"),
                        (button, value) -> config.widthSlots = value));
        y += ROW_GAP;

        // 高度（0 = 自动按比例）
        addRenderableWidget(CycleButton.<Float>builder(ShieldIndicatorConfigScreen::slotsLabel, config.heightSlots)
                .withValues(HEIGHT_VALUES)
                .create(left, y, ROW_WIDTH, ROW_HEIGHT,
                        Component.translatable("ctt_shield_indicator.config.height"),
                        (button, value) -> config.heightSlots = value));
        y += ROW_GAP;

        // 与 actionbar 的间距
        addRenderableWidget(CycleButton.<Integer>builder(v -> Component.literal(v + "px"), config.gapAboveActionBar)
                .withValues(GAP_VALUES)
                .create(left, y, ROW_WIDTH, ROW_HEIGHT,
                        Component.translatable("ctt_shield_indicator.config.gap"),
                        (button, value) -> config.gapAboveActionBar = value));
        y += ROW_GAP;

        // 绘制方式
        addRenderableWidget(CycleButton.<String>builder(v -> Component.translatable("ctt_shield_indicator.mode." + v), config.meterMode)
                .withValues(MODE_VALUES)
                .create(left, y, ROW_WIDTH, ROW_HEIGHT,
                        Component.translatable("ctt_shield_indicator.config.meter_mode"),
                        (button, value) -> config.meterMode = value));
        y += ROW_GAP + 8;

        // 恢复默认
        addRenderableWidget(Button.builder(Component.translatable("ctt_shield_indicator.config.reset"), button -> {
            ShieldIndicatorConfig.reset();
            rebuildWidgets();
        }).bounds(left, y, (ROW_WIDTH - 8) / 2, ROW_HEIGHT).build());

        // 完成
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(left + (ROW_WIDTH - 8) / 2 + 8, y, (ROW_WIDTH - 8) / 2, ROW_HEIGHT).build());
    }

    private static Component slotsLabel(float value) {
        if (value <= 0.0f) {
            return Component.translatable("ctt_shield_indicator.value.auto");
        }
        return Component.literal(String.format(Locale.ROOT, "%.2f", value));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, TITLE, this.width / 2, 16, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        ShieldIndicatorConfig.save();
        // 26.3 里屏幕由 Gui 管理（Screen#onClose 默认实现同样是调 gui.setScreen(null)）
        this.minecraft.gui.setScreen(this.parent);
    }
}
