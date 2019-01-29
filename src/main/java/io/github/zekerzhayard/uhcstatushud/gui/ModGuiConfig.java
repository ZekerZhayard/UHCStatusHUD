package io.github.zekerzhayard.uhcstatushud.gui;

import java.io.IOException;

import io.github.zekerzhayard.uhcstatushud.UHCStatusHUD;
import io.github.zekerzhayard.uhcstatushud.config.ModConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ModGuiConfig extends GuiConfig {
    public ModGuiConfig(GuiScreen parent) {
        super(parent, new ConfigElement(ModConfig.instance.getConfig().getCategory(UHCStatusHUD.MODID)).getChildElements(), UHCStatusHUD.MODID, false, false, UHCStatusHUD.NAME);
    }

    @Override()
    public void handleInput() throws IOException {
        super.handleInput();
        this.entryList.saveConfigElements();
        ModConfig.instance.saveConfig();
    }

    @Override()
    public void onGuiClosed() {
        super.onGuiClosed();
        ModConfig.instance.saveConfig();
    }
}