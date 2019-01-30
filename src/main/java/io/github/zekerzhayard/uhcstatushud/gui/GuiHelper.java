package io.github.zekerzhayard.uhcstatushud.gui;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

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
    static String getColorName(int color) {
        return EnumChatFormatting.func_175744_a(color).name();
    }

    private static int changeColor(GuiButton button, EnumConfig config) {
        Integer[] myColourCodes = Arrays.stream(EnumChatFormatting.values()).filter(EnumChatFormatting::isColor).map(EnumChatFormatting::getColorIndex).toArray(Integer[]::new);
        int index = ArrayUtils.indexOf(myColourCodes, config.getProperty().getInt()) + 1;
        if (index >= ArrayUtils.getLength(myColourCodes)) {
            index = 0;
        }
        config.getProperty().set(myColourCodes[index]);
        button.displayString = EnumChatFormatting.func_175744_a(index).toString() + GuiHelper.getColorName(myColourCodes[index]);
        return 0;
    }

    private static int changeBoolean(GuiButton button, EnumConfig config) {
        config.getProperty().set(!config.getProperty().getBoolean());
        button.displayString = config.getProperty().getBoolean() ? EnumChatFormatting.GREEN + "true" : EnumChatFormatting.RED + "false";
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
            if (mouseX >= EnumConfig.SOLOX.getProperty().getInt() && mouseX <= EnumConfig.SOLOX.getProperty().getInt() + Math.max(BoardRenderer.instance.playerWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth("e-x-a-m-p-l-e")) && mouseY >= EnumConfig.SOLOY.getProperty().getInt() && mouseY <= EnumConfig.SOLOY.getProperty().getInt() + (BoardRenderer.instance.killerList.size() + 1) * 9) {
                EnumConfig.SOLOX.getProperty().set(mouseX - Math.max(BoardRenderer.instance.playerWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth("e-x-a-m-p-l-e")) / 2);
                EnumConfig.SOLOY.getProperty().set(mouseY - (BoardRenderer.instance.killerList.size() + 1) * 9 / 2);
            } else if (mouseX >= EnumConfig.TEAMX.getProperty().getInt() && mouseX <= EnumConfig.TEAMX.getProperty().getInt() + Math.max(BoardRenderer.instance.teamsWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(" • (e-x-a-m-p-l-e, e-x-a-m-p-l-e, e-x-a-m-p-l-e)")) && mouseY >= EnumConfig.TEAMY.getProperty().getInt() && mouseY <= EnumConfig.TEAMY.getProperty().getInt() + (BoardRenderer.instance.teamkillerList.size() + 1) * 9) {
                EnumConfig.TEAMX.getProperty().set(mouseX - Math.max(BoardRenderer.instance.teamsWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(" • (e-x-a-m-p-l-e, e-x-a-m-p-l-e, e-x-a-m-p-l-e)")) / 2);
                EnumConfig.TEAMY.getProperty().set(mouseY - (BoardRenderer.instance.teamkillerList.size() + 1) * 9 / 2);
            }
        }
    }

    private static GuiButton initBooleanButton(int index, GuiScreen guiScreen, List<EnumConfig> configs, int base) {
        return new GuiButton(index, guiScreen.width / 2 - 60, guiScreen.height / 2 - (configs.size() - 1) / 2 * 22 - 6 + index * 22 + base, 200, 20, configs.get(index).getProperty().getBoolean() ? EnumChatFormatting.GREEN + "true" : EnumChatFormatting.RED + "false");
    }

    private static GuiButton initColorButton(int index, GuiScreen guiScreen, List<EnumConfig> configs, int base) {
        int color = configs.get(index).getProperty().getInt();
        GuiButton button = new GuiButton(index, guiScreen.width / 2 - 60, guiScreen.height / 2 - (configs.size() - 1) / 2 * 22 - 6 + index * 22 + base, 200, 20, GuiHelper.getColorName(color));
        button.displayString = EnumChatFormatting.func_175744_a(color).toString() + GuiHelper.getColorName(color);
        return button;
    }

    static GuiButton initButton(boolean isBoolean, int index, GuiScreen guiScreen, List<EnumConfig> configs, int base) {
        return isBoolean ? GuiHelper.initBooleanButton(index, guiScreen, configs, base) : GuiHelper.initColorButton(index, guiScreen, configs, base);
    }
}
