package com.nyalium.cttshieldindicator;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

/**
 * 纯客户端 Mod 入口。
 *
 * <p>整个功能只依赖客户端渲染：读取副手物品的组件与耐久，把“盾牌 + 计量条”
 * 画在 HUD 上，完全不需要服务端安装任何数据包。挂载点放在原版 HOTBAR 之后，
 * 因此绘制顺序紧接快捷栏，不会被物品栏内容覆盖。</p>
 */
public final class CttShieldIndicatorClient implements ClientModInitializer {
    public static final String MOD_ID = "ctt_shield_indicator";

    @Override
    public void onInitializeClient() {
        ShieldIndicatorConfig.load();
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.HOTBAR,
                Identifier.fromNamespaceAndPath(MOD_ID, "shield_indicator"),
                new ShieldIndicatorHud());
    }
}
