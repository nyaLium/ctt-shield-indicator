package com.nyalium.cttshieldindicator;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

/**
 * 纯客户端 Mod 入口（Minecraft 1.21.4 / Fabric）。
 *
 * <p>整个功能只依赖客户端渲染：读取副手物品的组件与耐久，把计量条画在 HUD 上，
 * 完全不需要服务端安装任何数据包。1.21.4 还没有 {@code HudElementRegistry}，
 * 因此用 Fabric API 的 {@link HudRenderCallback} 在 HUD 绘制阶段挂一个回调。</p>
 */
public final class CttShieldIndicatorClient implements ClientModInitializer {
    public static final String MOD_ID = "ctt_shield_indicator";

    @Override
    public void onInitializeClient() {
        ShieldIndicatorConfig.load();
        HudRenderCallback.EVENT.register(new ShieldIndicatorHud());
    }
}
