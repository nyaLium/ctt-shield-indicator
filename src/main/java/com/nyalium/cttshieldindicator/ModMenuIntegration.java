package com.nyalium.cttshieldindicator;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Mod Menu 集成入口。
 *
 * <p>这个类只在安装了 Mod Menu 时才会被加载（入口点由 Mod Menu 自己实例化），
 * 因此 Mod Menu 是可选依赖：没装也能正常玩，只是没有设置界面。</p>
 */
public final class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ShieldIndicatorConfigScreen::new;
    }
}
