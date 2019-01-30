package io.github.zekerzhayard.uhcstatushud.gui;

import java.io.IOException;
import java.util.List;

import io.github.zekerzhayard.uhcstatushud.UHCStatusHUD;
import io.github.zekerzhayard.uhcstatushud.config.ModConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ModGuiConfig extends GuiConfig {
    private static List<IConfigElement> elements;

    static {
        ModGuiConfig.elements = new ConfigElement(ModConfig.instance.getConfig().getCategory(UHCStatusHUD.MODID)).getChildElements();
        ModGuiConfig.elements.removeIf(el -> el.getType().compareTo(ConfigGuiType.STRING) != 0);
    }

    public ModGuiConfig(GuiScreen parent) {
        super(parent, ModGuiConfig.elements, UHCStatusHUD.MODID, false, false, UHCStatusHUD.NAME);
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