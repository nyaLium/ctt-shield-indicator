package com.nyalium.cttshieldindicator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/**
 * 配置模型 + 读写，放在 {@code config/ctt_shield_indicator.json}。
 * 首次启动生成默认值；解析失败退回默认值；字段缺失保留默认值。
 * 游戏内设置界面（Mod Menu）改的就是这个对象，改完立刻生效。
 */
public final class ShieldIndicatorConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ShieldIndicatorConfig INSTANCE = new ShieldIndicatorConfig();

    /** 总开关。关掉后指示器完全不显示。 */
    public boolean enabled = true;

    /** 指示器的水平锚点：跟着副手槽，还是钉在屏幕中心。 */
    public enum Position {
        /** 对齐副手槽的物品格（1.1.0 之前的行为）。 */
        @SerializedName("offhand") OFFHAND,
        /** 对齐屏幕中心，再按 {@link #horizontalOffset} 左右平移。 */
        @SerializedName("center") CENTER
    }

    /** 水平锚点，默认跟随副手槽。 */
    public Position position = Position.OFFHAND;

    /**
     * 在锚点基础上的水平偏移（像素，正值向右，负值向左）。
     * 无论哪种锚点都会叠加；绘制时还会被限制在屏幕内。
     */
    public int horizontalOffset = 0;

    /**
     * 缩放比例：可见内容宽度 = 一格（20px）× 本值，高度按纹理 52:128 自动。
     * {@code 1.0} = 与槽位同宽；{@code 1.1} = 略宽一点（默认）。
     */
    public float scale = 1.1f;

    /** 指示器底端距离 actionbar 文字顶部的额外间距（像素）。 */
    public int gapAboveActionBar = 2;

    public static ShieldIndicatorConfig get() {
        return INSTANCE;
    }

    /** 恢复默认值（设置界面的「恢复默认」按钮）。 */
    public static void reset() {
        INSTANCE = new ShieldIndicatorConfig();
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve(CttShieldIndicatorClient.MOD_ID + ".json");
    }

    public static void load() {
        Path file = file();
        try {
            if (Files.exists(file)) {
                String json = Files.readString(file, StandardCharsets.UTF_8);
                ShieldIndicatorConfig parsed = GSON.fromJson(json, ShieldIndicatorConfig.class);
                if (parsed != null) {
                    INSTANCE = parsed;
                }
            } else {
                save();
            }
        } catch (IOException | RuntimeException e) {
            INSTANCE = new ShieldIndicatorConfig();
        }
    }

    /** 写回磁盘（游戏内改完设置时调用）。 */
    public static void save() {
        Path file = file();
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(INSTANCE), StandardCharsets.UTF_8);
        } catch (IOException | RuntimeException ignored) {
            // 写失败不影响游戏内临时生效
        }
    }
}
