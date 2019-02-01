package io.github.zekerzhayard.uhcstatushud.gui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.github.zekerzhayard.uhcstatushud.UHCStatusHUD;
import io.github.zekerzhayard.uhcstatushud.config.EnumConfig;
import io.github.zekerzhayard.uhcstatushud.config.ModConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ModGuiConfig extends GuiConfig {
    private static List<IConfigElement> elements;

    static {
        ModGuiConfig.elements = new ConfigElement(ModConfig.instance.getConfig().getCategory(UHCStatusHUD.MODID)).getChildElements();
        List<String> specialConfig = Arrays.stream(EnumConfig.values()).filter(c -> c.getType().equals(EnumConfig.Type.MANUAL)).map(c -> c.getProperty().getName()).collect(Collectors.toList());
        ModGuiConfig.elements.removeIf(el -> !specialConfig.contains(el.getQualifiedName()));
    }

    public ModGuiConfig(GuiScreen parent) {
        super(parent, ModGuiConfig.elements, UHCStatusHUD.MODID, false, false, UHCStatusHUD.NAME);
    }

    @Override()
    public void onGuiClosed() {
        super.onGuiClosed();
        ModConfig.instance.saveConfig();
    }
}