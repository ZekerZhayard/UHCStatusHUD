package io.github.zekerzhayard.uhcstatushud.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.Maps;

import io.github.zekerzhayard.uhcstatushud.config.EnumConfig;
import io.github.zekerzhayard.uhcstatushud.config.ModConfig;
import io.github.zekerzhayard.uhcstatushud.feature.BoardRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;

public class ModGuiSettings extends GuiScreen {
    private List<EnumConfig> configs = Arrays.stream(EnumConfig.values()).filter(ec -> !ec.getType().equals(EnumConfig.Type.LOCATION) && !ec.getType().equals(EnumConfig.Type.MANUAL)).collect(Collectors.toList());
    private GuiScreen parentScreen;

    public ModGuiSettings(GuiScreen parent) {
        this.parentScreen = parent;
    }

    @Override()
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiHelper.drawString(this, this.fontRendererObj, this.configs.stream().map(c -> c.getProperty().getName()).collect(Collectors.toList()), "Normal", 11);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override()
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        GuiHelper.mouseClickMove(mouseX, mouseY, clickedMouseButton);
    }

    @Override()
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 200) {
            Minecraft.getMinecraft().displayGuiScreen(this.parentScreen);
        } else if (button.id == 201) {
            Minecraft.getMinecraft().displayGuiScreen(new ModGuiConfig(this));
        } else {
            GuiHelper.clickButton(button, this.configs);
        }
    }

    @Override()
    public void initGui() {
        MinecraftForge.EVENT_BUS.register(BoardRenderer.instance);
        if (BoardRenderer.instance.killerList.isEmpty()) {
            IntStream.range(0, 5).forEachOrdered(i -> BoardRenderer.instance.killerList.add(Maps.immutableEntry(GuiHelper.examplePlayer, "")));
        }
        if (BoardRenderer.instance.teamkillerList.isEmpty()) {
            IntStream.range(0, 5).forEachOrdered(i -> BoardRenderer.instance.teamkillerList.add(Maps.immutableEntry(GuiHelper.exampleTeams, "")));
        }
        BoardRenderer.instance.playerWidth = Math.max(BoardRenderer.instance.playerWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(GuiHelper.examplePlayer));
        BoardRenderer.instance.teamsWidth = Math.max(BoardRenderer.instance.teamsWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(GuiHelper.exampleTeams));
        GuiHelper.addButton(this, this.buttonList, this.configs, 0);
        this.buttonList.add(new GuiButton(200, this.width / 2 - 155, this.height - 35, 150, 20, I18n.format("gui.done")));
        this.buttonList.add(new GuiButton(201, this.width / 2 + 5, this.height - 35, 150, 20, I18n.format("gui.othersettings")));
    }

    @Override()
    public void onGuiClosed() {
        ModConfig.instance.saveNormalConfig();
        BoardRenderer.instance.playerWidth = BoardRenderer.instance.teamsWidth = 0;
        BoardRenderer.instance.killerList.removeIf(e -> e.getKey().equals(GuiHelper.examplePlayer));
        BoardRenderer.instance.teamkillerList.removeIf(e -> e.getKey().equals(GuiHelper.exampleTeams));
        BoardRenderer.instance.killerList.forEach(e -> BoardRenderer.instance.playerWidth = Math.max(BoardRenderer.instance.playerWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(e.getKey())));
        BoardRenderer.instance.teamkillerList.forEach(e -> BoardRenderer.instance.teamsWidth = Math.max(BoardRenderer.instance.teamsWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(e.getKey())));
        if (Minecraft.getMinecraft().getCurrentServerData() == null || Minecraft.getMinecraft().isSingleplayer() || !Minecraft.getMinecraft().getCurrentServerData().serverIP.contains("hypixel.net")) {
            MinecraftForge.EVENT_BUS.unregister(BoardRenderer.instance);
        }
    }
}