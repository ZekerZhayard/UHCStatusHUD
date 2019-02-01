package io.github.zekerzhayard.uhcstatushud.gui;

import java.util.List;

import io.github.zekerzhayard.uhcstatushud.UHCStatusHUD;
import io.github.zekerzhayard.uhcstatushud.config.EnumConfig;
import io.github.zekerzhayard.uhcstatushud.feature.BoardRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

class GuiHelper {
    static String examplePlayer = "e-x-a-m-p-l-e";
    static String exampleTeams = " \u2022 (e-x-a-m-p-l-e, e-x-a-m-p-l-e, e-x-a-m-p-l-e)";

    private static int changeColor(GuiButton button, EnumConfig config) {
        config.getProperty().set((config.getProperty().getInt() + 2) % 17 - 1);
        button.displayString = EnumConfig.Type.COLOR.getMap().get(config.getProperty().getInt());
        return 0;
    }

    private static int changeBoolean(GuiButton button, EnumConfig config) {
        config.getProperty().set(!config.getProperty().getBoolean());
        button.displayString = EnumConfig.Type.BOOLEAN.getMap().get(config.getProperty().getBoolean());
        return -1;
    }

    static int changeButton(boolean isBoolean, GuiButton button, EnumConfig config) {
        return isBoolean ? GuiHelper.changeBoolean(button, config) : GuiHelper.changeColor(button, config);
    }

    private static int j;
    static void drawString(GuiScreen guiScreen, FontRenderer fontRenderer, List<String> texts, String title, int base) {
        GuiHelper.j = (texts.size() - 1) * 11 + 22 - base;
        guiScreen.drawCenteredString(fontRenderer, EnumChatFormatting.BOLD + UHCStatusHUD.NAME, guiScreen.width / 2 + 30, guiScreen.height / 2 - GuiHelper.j - 44, 0x00FFFF55);
        guiScreen.drawCenteredString(fontRenderer, EnumChatFormatting.BOLD + title + " Settings", guiScreen.width / 2 + 30, guiScreen.height / 2 - GuiHelper.j - 31, 0x00FFFFFF);
        guiScreen.drawString(fontRenderer, EnumChatFormatting.DARK_AQUA + "UHCStatusHUD v" + UHCStatusHUD.VERSION + " by ZekerZhayard", 0, guiScreen.height - 10, 0x00FFFFFF);
        texts.forEach(str -> guiScreen.drawString(fontRenderer, I18n.format(str), guiScreen.width / 2 - 70 - fontRenderer.getStringWidth(I18n.format(str)), guiScreen.height / 2 - (GuiHelper.j -= 22), 0x00FFFFFF));
    }

    static void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton) {
        if (clickedMouseButton == 0) {
            if (mouseX >= EnumConfig.SOLOX.getProperty().getInt() && mouseX <= EnumConfig.SOLOX.getProperty().getInt() + Math.max(BoardRenderer.instance.playerWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(GuiHelper.examplePlayer)) && mouseY >= EnumConfig.SOLOY.getProperty().getInt() && mouseY <= EnumConfig.SOLOY.getProperty().getInt() + (BoardRenderer.instance.killerList.size() + 1) * 9) {
                EnumConfig.SOLOX.getProperty().set(mouseX - Math.max(BoardRenderer.instance.playerWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(GuiHelper.examplePlayer)) / 2);
                EnumConfig.SOLOY.getProperty().set(mouseY - (BoardRenderer.instance.killerList.size() + 1) * 9 / 2);
            } else if (mouseX >= EnumConfig.TEAMX.getProperty().getInt() && mouseX <= EnumConfig.TEAMX.getProperty().getInt() + Math.max(BoardRenderer.instance.teamsWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(GuiHelper.exampleTeams)) && mouseY >= EnumConfig.TEAMY.getProperty().getInt() && mouseY <= EnumConfig.TEAMY.getProperty().getInt() + (BoardRenderer.instance.teamkillerList.size() + 1) * 9) {
                EnumConfig.TEAMX.getProperty().set(mouseX - Math.max(BoardRenderer.instance.teamsWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(GuiHelper.exampleTeams)) / 2);
                EnumConfig.TEAMY.getProperty().set(mouseY - (BoardRenderer.instance.teamkillerList.size() + 1) * 9 / 2);
            }
        }
    }

    private static GuiButton initBooleanButton(int index, GuiScreen guiScreen, List<EnumConfig> configs, int base) {
        return new GuiButton(index, guiScreen.width / 2 - 60, guiScreen.height / 2 - (configs.size() - 1) / 2 * 22 - 6 + index * 22 + base, 200, 20, EnumConfig.Type.BOOLEAN.getMap().get(configs.get(index).getProperty().getBoolean()));
    }

    private static GuiButton initColorButton(int index, GuiScreen guiScreen, List<EnumConfig> configs, int base) {
        GuiButton button = new GuiButton(index, guiScreen.width / 2 - 60, guiScreen.height / 2 - (configs.size() - 1) / 2 * 22 - 6 + index * 22 + base, 200, 20, EnumConfig.Type.COLOR.getMap().get(configs.get(index).getProperty().getInt()));
        return button;
    }

    static GuiButton initButton(boolean isBoolean, int index, GuiScreen guiScreen, List<EnumConfig> configs, int base) {
        return isBoolean ? GuiHelper.initBooleanButton(index, guiScreen, configs, base) : GuiHelper.initColorButton(index, guiScreen, configs, base);
    }
}
