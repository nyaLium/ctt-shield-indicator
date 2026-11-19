package com.nyalium.cttshieldindicator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
 *
 * <p>两种位置各自保留一套「缩放比例 + 水平偏移」，切换位置时互不干扰：
 * 副手槽模式出厂值 {@code 1.10 / 0}，屏幕中心模式 {@code 0.85 / +25}（略小并向右让开准星）。</p>
 */
public final class ShieldIndicatorConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ShieldIndicatorConfig INSTANCE = new ShieldIndicatorConfig();

    /** 副手槽模式的出厂值。 */
    private static final float OFFHAND_SCALE = 1.1f;
    private static final int OFFHAND_OFFSET = 0;
    /** 屏幕中心模式的出厂值：略小一点，并向右让开准星。 */
    private static final float CENTER_SCALE = 0.85f;
    private static final int CENTER_OFFSET = 25;

    /** 总开关。关掉后指示器完全不显示。 */
    public boolean enabled = true;

    /** 指示器的位置锚点。 */
    public enum Position {
        /** 对齐副手槽的物品格（1.1.0 之前的行为）。 */
        @SerializedName("offhand") OFFHAND,
        /** 放在整个界面的正中，再按水平偏移左右移动。 */
        @SerializedName("center") CENTER
    }

    /** 水平锚点，默认跟随副手槽。 */
    public Position position = Position.OFFHAND;

    /** 不透明度：0.10–1.00，1.00 为完全不透明。 */
    public float opacity = 1.0f;

    /** 仅「副手槽上方」模式使用：底端与 actionbar 文字顶部的间距（像素）。 */
    public int gapAboveActionBar = 2;

    /** 「副手槽上方」模式的一套显示参数。 */
    public Profile offhand = new Profile(OFFHAND_SCALE, OFFHAND_OFFSET);

    /** 「屏幕中心」模式的一套显示参数。 */
    public Profile center = new Profile(CENTER_SCALE, CENTER_OFFSET);

    /** 一种位置对应的一套显示参数。 */
    public static final class Profile {
        /** 缩放比例：可见内容宽度 = 一格（20px）× 本值，高度按纹理 52:128 自动。 */
        public float scale = OFFHAND_SCALE;

        /** 水平偏移（像素，正值向右，负值向左）。 */
        public int offset = 0;

        public Profile() {
        }

        Profile(float scale, int offset) {
            this.scale = scale;
            this.offset = offset;
        }
    }

    /** 当前选中的位置对应的那套参数。 */
    public Profile activeProfile() {
        return position == Position.CENTER ? center : offhand;
    }

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
                INSTANCE = parse(Files.readString(file, StandardCharsets.UTF_8));
            } else {
                save();
            }
        } catch (IOException | RuntimeException e) {
            INSTANCE = new ShieldIndicatorConfig();
        }
    }

    /** 解析 JSON 文本并补齐缺省 / 迁移旧字段；解析失败由调用方兜底。 */
    static ShieldIndicatorConfig parse(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        ShieldIndicatorConfig parsed = GSON.fromJson(root, ShieldIndicatorConfig.class);
        if (parsed == null) {
            return new ShieldIndicatorConfig();
        }
        parsed.migrateLegacy(root);
        parsed.normalize();
        return parsed;
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

    /** 1.2.0 及更早的扁平字段（{@code scale} / {@code horizontalOffset}）迁移到副手槽那套。 */
    private void migrateLegacy(JsonObject root) {
        if (offhand == null) {
            offhand = new Profile(OFFHAND_SCALE, OFFHAND_OFFSET);
        }
        if (root.has("scale") && !root.get("scale").isJsonNull()) {
            offhand.scale = root.get("scale").getAsFloat();
        }
        if (root.has("horizontalOffset") && !root.get("horizontalOffset").isJsonNull()) {
            offhand.offset = root.get("horizontalOffset").getAsInt();
        }
    }

    /** 兜底：手改配置时可能缺字段或写出越界值。 */
    private void normalize() {
        if (offhand == null) {
            offhand = new Profile(OFFHAND_SCALE, OFFHAND_OFFSET);
        }
        if (center == null) {
            center = new Profile(CENTER_SCALE, CENTER_OFFSET);
        }
        if (position == null) {
            position = Position.OFFHAND;
        }
        if (Float.isNaN(opacity) || Float.isInfinite(opacity)) {
            opacity = 1.0f;
        }
        opacity = Math.max(0.1f, Math.min(1.0f, opacity));
    }
}
