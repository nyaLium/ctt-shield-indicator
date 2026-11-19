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
 *
 * <p>「缩放比例」与「水平偏移」改的是<b>当前位置</b>那一套参数。切换位置时不重建控件，
 * 而是把滑块重新指向新的那套数值（{@link #syncWidgets()}），
 * 因此两个位置各自的数值互不影响，来回切换不会丢。</p>
 */
public final class ShieldIndicatorConfigScreen extends Screen {
    private static final Component TITLE = Component.translatable("ctt_shield_indicator.config.title");
    private static final int ROW_WIDTH = 220;
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_GAP = 24;
    private static final int TOP = 40;

    /** 缩放比例的可调范围。 */
    private static final double SCALE_MIN = 0.6D;
    private static final double SCALE_MAX = 2.5D;
    private static final double SCALE_STEP = 0.05D;

    /** 水平偏移的可调范围（像素，正右负左）。 */
    private static final double OFFSET_MIN = -160D;
    private static final double OFFSET_MAX = 160D;
    private static final double OFFSET_STEP = 1D;

    /** 透明度的可调范围。 */
    private static final double OPACITY_MIN = 0.1D;
    private static final double OPACITY_MAX = 1.0D;
    private static final double OPACITY_STEP = 0.05D;

    /** 与 actionbar 间距的可调范围。 */
    private static final double GAP_MIN = 0D;
    private static final double GAP_MAX = 20D;
    private static final double GAP_STEP = 1D;

    private final Screen parent;

    private CycleButton<Boolean> enabledButton;
    private CycleButton<ShieldIndicatorConfig.Position> positionButton;
    private ValueSlider scaleSlider;
    private ValueSlider offsetSlider;
    private ValueSlider opacitySlider;
    private ValueSlider gapSlider;

    public ShieldIndicatorConfigScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        ShieldIndicatorConfig config = ShieldIndicatorConfig.get();
        ShieldIndicatorConfig.Profile profile = config.activeProfile();
        int left = this.width / 2 - ROW_WIDTH / 2;
        int y = TOP;

        // 启用
        this.enabledButton = addRenderableWidget(CycleButton.onOffBuilder(config.enabled)
                .create(left, y, ROW_WIDTH, ROW_HEIGHT,
                        Component.translatable("ctt_shield_indicator.config.enabled"),
                        (button, value) -> ShieldIndicatorConfig.get().enabled = value));
        y += ROW_GAP;

        // 位置：切到另一套参数，并同步下面的滑块
        this.positionButton = addRenderableWidget(CycleButton.<ShieldIndicatorConfig.Position>builder(
                        ShieldIndicatorConfigScreen::positionName, config.position)
                .withValues(ShieldIndicatorConfig.Position.OFFHAND, ShieldIndicatorConfig.Position.CENTER)
                .create(left, y, ROW_WIDTH, ROW_HEIGHT,
                        Component.translatable("ctt_shield_indicator.config.position"),
                        (button, value) -> {
                            ShieldIndicatorConfig.get().position = value;
                            syncWidgets();
                        }));
        y += ROW_GAP;

        // 缩放比例（当前位置）
        this.scaleSlider = addRenderableWidget(new ValueSlider(left, y, ROW_WIDTH, ROW_HEIGHT,
                SCALE_MIN, SCALE_MAX, SCALE_STEP, profile.scale,
                value -> Component.translatable("ctt_shield_indicator.config.scale", String.format(Locale.ROOT, "%.2f", value)),
                value -> ShieldIndicatorConfig.get().activeProfile().scale = (float) value));
        y += ROW_GAP;

        // 水平偏移（当前位置）
        this.offsetSlider = addRenderableWidget(new ValueSlider(left, y, ROW_WIDTH, ROW_HEIGHT,
                OFFSET_MIN, OFFSET_MAX, OFFSET_STEP, profile.offset,
                value -> Component.translatable("ctt_shield_indicator.config.offset",
                        signed((int) Math.round(value))),
                value -> ShieldIndicatorConfig.get().activeProfile().offset = (int) Math.round(value)));
        y += ROW_GAP;

        // 透明度
        this.opacitySlider = addRenderableWidget(new ValueSlider(left, y, ROW_WIDTH, ROW_HEIGHT,
                OPACITY_MIN, OPACITY_MAX, OPACITY_STEP, config.opacity,
                value -> Component.translatable("ctt_shield_indicator.config.opacity",
                        String.format(Locale.ROOT, "%d%%", Math.round(value * 100.0D))),
                value -> ShieldIndicatorConfig.get().opacity = (float) value));
        y += ROW_GAP;

        // 与 actionbar 的间距：只有副手槽模式用得上，屏幕中心模式下置灰
        this.gapSlider = addRenderableWidget(new ValueSlider(left, y, ROW_WIDTH, ROW_HEIGHT,
                GAP_MIN, GAP_MAX, GAP_STEP, config.gapAboveActionBar,
                value -> Component.translatable("ctt_shield_indicator.config.gap", String.format(Locale.ROOT, "%d", (int) Math.round(value))),
                value -> ShieldIndicatorConfig.get().gapAboveActionBar = (int) Math.round(value)));
        y += ROW_GAP;

        y += 10;
        int half = (ROW_WIDTH - 8) / 2;

        // 恢复默认
        addRenderableWidget(Button.builder(Component.translatable("ctt_shield_indicator.config.reset"), button -> {
            ShieldIndicatorConfig.reset();
            syncWidgets();
        }).bounds(left, y, half, ROW_HEIGHT).build());

        // 完成
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(left + half + 8, y, half, ROW_HEIGHT).build());

        syncWidgets();
    }

    /** 把控件显示同步到当前配置（切换位置、恢复默认后调用）。 */
    private void syncWidgets() {
        ShieldIndicatorConfig config = ShieldIndicatorConfig.get();
        ShieldIndicatorConfig.Profile profile = config.activeProfile();
        if (this.enabledButton != null) {
            this.enabledButton.setValue(config.enabled);
        }
        if (this.positionButton != null) {
            this.positionButton.setValue(config.position);
        }
        if (this.scaleSlider != null) {
            this.scaleSlider.setCurrent(profile.scale);
        }
        if (this.offsetSlider != null) {
            this.offsetSlider.setCurrent(profile.offset);
        }
        if (this.opacitySlider != null) {
            this.opacitySlider.setCurrent(config.opacity);
        }
        if (this.gapSlider != null) {
            this.gapSlider.setCurrent(config.gapAboveActionBar);
            this.gapSlider.active = config.position != ShieldIndicatorConfig.Position.CENTER;
        }
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

    /** 位置选项的显示名。 */
    private static Component positionName(ShieldIndicatorConfig.Position position) {
        String key = position == ShieldIndicatorConfig.Position.CENTER
                ? "ctt_shield_indicator.config.position.center"
                : "ctt_shield_indicator.config.position.offhand";
        return Component.translatable(key);
    }

    /** 偏移量带符号显示，0 时不带正号。 */
    private static String signed(int value) {
        return value > 0 ? "+" + value : Integer.toString(value);
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

        /** 外部换值（保存的数值变了），只刷新显示，不触发回调。 */
        void setCurrent(double newValue) {
            double clamped = Math.max(this.min, Math.min(this.max, newValue));
            this.value = (clamped - this.min) / (this.max - this.min);
            updateMessage();
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
