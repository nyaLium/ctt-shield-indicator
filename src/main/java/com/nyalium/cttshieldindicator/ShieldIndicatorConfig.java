package com.nyalium.cttshieldindicator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/**
 * 简单 JSON 配置，放在 {@code config/ctt_shield_indicator.json}。
 * 首次启动自动生成默认值；解析失败时退回默认值，不会导致游戏崩溃。
 * 字段缺失时使用这里的默认值（Gson 只覆盖 JSON 中出现的字段）。
 */
public final class ShieldIndicatorConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ShieldIndicatorConfig INSTANCE = new ShieldIndicatorConfig();

    /** 总开关。 */
    public boolean enabled = true;

    /** 是否显示计量条；关掉后整个指示器不出现。 */
    public boolean showMeter = true;

    /**
     * 可见内容的宽度，单位是「原版槽位格」（20px）。
     * {@code 1.0} = 与槽位同宽；{@code 1.1} = 略宽一点（默认）。
     * 因为最终是 GUI 单位，所以调 GUI 缩放/分辨率时会自动等比跟随。
     */
    public float widthSlots = 1.1f;

    /**
     * 可见内容的高度，单位同上；{@code 0} = 按纹理 52:128 的比例自动
     * （例如宽 1.1 格 → 高 ≈ 2.7 格）。想压扁一点就显式给个值，如 {@code 1.5}。
     */
    public float heightSlots = 0.0f;

    /** 指示器底端距离 actionbar 文字顶部的额外间距（像素）。 */
    public int gapAboveActionBar = 2;

    /**
     * 计量条绘制方式：
     * {@code texture} = 把 meter_N.png 按上面算出的尺寸绘制（默认）；
     * {@code model}   = 直接渲染资源包的 meter_N 模型。
     */
    public String meterMode = "texture";

    public static ShieldIndicatorConfig get() {
        return INSTANCE;
    }

    public static void load() {
        Path file = FabricLoader.getInstance().getConfigDir().resolve(CttShieldIndicatorClient.MOD_ID + ".json");
        try {
            if (Files.exists(file)) {
                String json = Files.readString(file, StandardCharsets.UTF_8);
                ShieldIndicatorConfig parsed = GSON.fromJson(json, ShieldIndicatorConfig.class);
                if (parsed != null) {
                    INSTANCE = parsed;
                }
            } else {
                Files.createDirectories(file.getParent());
                Files.writeString(file, GSON.toJson(INSTANCE), StandardCharsets.UTF_8);
            }
        } catch (IOException | RuntimeException e) {
            INSTANCE = new ShieldIndicatorConfig();
        }
    }
}
