package com.nyalium.cttshieldindicator;

import java.util.Locale;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Mod Menu 里的设置界面。
 *
 * <p>数值项用滑块；直接改 {@link ShieldIndicatorConfig#get()} 里的字段，HUD 每帧读取，
 * 所以拖动即时生效；关闭界面时写回磁盘。</p>
 */
public final class ShieldIndicatorConfigScreen extends Screen {
    private static final Component TITLE = Component.translatable("ctt_shield_indicator.config.title");
    private static final int ROW_WIDTH = 220;
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_GAP = 26;
    private static final int TOP = 40;

    /** 缩放比例的可调范围。 */
    private static final double SCALE_MIN = 0.6D;
    private static final double SCALE_MAX = 2.5D;
    private static final double SCALE_STEP = 0.05D;

    /** 与 actionbar 间距的可调范围。 */
    private static final double GAP_MIN = 0D;
    private static final double GAP_MAX = 20D;
    private static final double GAP_STEP = 1D;

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

        // 缩放比例
        addRenderableWidget(new ValueSlider(left, y, ROW_WIDTH, ROW_HEIGHT,
                SCALE_MIN, SCALE_MAX, SCALE_STEP, config.scale,
                value -> Component.translatable("ctt_shield_indicator.config.scale", String.format(Locale.ROOT, "%.2f", value)),
                value -> config.scale = (float) value));
        y += ROW_GAP;

        // 与 actionbar 的间距
        addRenderableWidget(new ValueSlider(left, y, ROW_WIDTH, ROW_HEIGHT,
                GAP_MIN, GAP_MAX, GAP_STEP, config.gapAboveActionBar,
                value -> Component.translatable("ctt_shield_indicator.config.gap", String.format(Locale.ROOT, "%d", (int) Math.round(value))),
                value -> config.gapAboveActionBar = (int) Math.round(value)));
        y += ROW_GAP + 10;

        int half = (ROW_WIDTH - 8) / 2;

        // 恢复默认
        addRenderableWidget(Button.builder(Component.translatable("ctt_shield_indicator.config.reset"), button -> {
            ShieldIndicatorConfig.reset();
            rebuildWidgets();
        }).bounds(left, y, half, ROW_HEIGHT).build());

        // 完成
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(left + half + 8, y, half, ROW_HEIGHT).build());
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

    /** 带数值标签的滑块：内部用 0..1 的归一化值，applyValue 时量化到 step 并回调。 */
    private static final class ValueSlider extends AbstractSliderButton {
        private final double min;
        private final double max;
        private final double step;
        private final DoubleFunction<Component> label;
        private final DoubleConsumer onChange;

        ValueSlider(int x, int y, int width, int height, double min, double max, double step, double initial,
                    DoubleFunction<Component> label, DoubleConsumer onChange) {
            super(x, y, width, height, Component.empty(), (initial - min) / (max - min));
            this.min = min;
            this.max = max;
            this.step = step;
            this.label = label;
            this.onChange = onChange;
            updateMessage();
        }

        private double currentValue() {
            double raw = this.min + (this.max - this.min) * this.value;
            double snapped = Math.round(raw / this.step) * this.step;
            return Math.max(this.min, Math.min(this.max, snapped));
        }

        @Override
        protected void updateMessage() {
            // 父类构造期间可能就会调用 updateMessage()，此时字段还没赋值
            if (this.label == null) {
                return;
            }
            setMessage(this.label.apply(currentValue()));
        }

        @Override
        protected void applyValue() {
            if (this.onChange == null) {
                return;
            }
            double value = currentValue();
            this.value = (value - this.min) / (this.max - this.min);
            this.onChange.accept(value);
            updateMessage();
        }
    }
}
