package io.github.zekerzhayard.uhcstatushud.gui;

import java.util.List;
import java.util.stream.IntStream;

import io.github.zekerzhayard.uhcstatushud.UHCStatusHUD;
import io.github.zekerzhayard.uhcstatushud.config.EnumConfig;
import io.github.zekerzhayard.uhcstatushud.feature.BoardRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

class GuiHelper {
    static String examplePlayer = "e-x-a-m-p-l-e";
    static String exampleTeams = " \u2022 (e-x-a-m-p-l-e, e-x-a-m-p-l-e, e-x-a-m-p-l-e)";

    static void clickButton(GuiButton button, List<EnumConfig> configs) {
        switch (configs.get(button.id).getType()) {
            case BOOLEAN: {
                configs.get(button.id).getProperty().set(!configs.get(button.id).getProperty().getBoolean());
                button.displayString = EnumConfig.Type.BOOLEAN.getMap().get(configs.get(button.id).getProperty().getBoolean());
                break;
            } case COLOR: {
                configs.get(button.id).getProperty().set((configs.get(button.id).getProperty().getInt() + 2) % 17 - 1);
                button.displayString = EnumConfig.Type.COLOR.getMap().get(configs.get(button.id).getProperty().getInt());
                break;
            } case RANGE: {
                configs.get(button.id).getProperty().set(((GuiSlider) button).getValueInt());
                break;
            } default: {
                // ignore
            }
        }
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

    static void addButton(GuiScreen guiScreen, List<GuiButton> buttonList, List<EnumConfig> configs, int base) {
        IntStream.range(0, configs.size()).forEachOrdered(i -> {
            int x = guiScreen.width / 2 - 60;
            int y = guiScreen.height / 2 - (configs.size() - 1) / 2 * 22 - 6 + i * 22 + base;
            switch (configs.get(i).getType()) {
                case BOOLEAN: {
                    buttonList.add(new GuiButton(i, x, y, EnumConfig.Type.BOOLEAN.getMap().get(configs.get(i).getProperty().getBoolean())));
                    break;
                } case COLOR: {
                    buttonList.add(new GuiButton(i, x, y, EnumConfig.Type.COLOR.getMap().get(configs.get(i).getProperty().getInt())));
                    break;
                } case RANGE: {
                    buttonList.add(new GuiSlider(i, x, y, 200, 20, "", "", Double.valueOf(configs.get(i).getProperty().getMinValue()), Double.valueOf(configs.get(i).getProperty().getMaxValue()), configs.get(i).getProperty().getInt(), true, false) {
                        @Override()
                        protected void mouseDragged(Minecraft mc, int x, int y) {
                            super.mouseDragged(mc, x, y);
                            configs.get(i).getProperty().set(this.getValueInt());
                        }
                    });
                    break;
                } default: {
                    // ignore
                }
            }
        });
    }
}
