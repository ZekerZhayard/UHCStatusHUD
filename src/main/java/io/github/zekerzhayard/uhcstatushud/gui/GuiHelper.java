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
                    buttonList.add(new GuiButton(i, x, y, EnumConfig.Type.BOOLEAN.getMap().get(configs.get(i).getProperty().getBoolean())) {
                        @Override()
                        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                            if (super.mousePressed(mc, mouseX, mouseY)) {
                                configs.get(this.id).getProperty().set(!configs.get(this.id).getProperty().getBoolean());
                                this.displayString = EnumConfig.Type.BOOLEAN.getMap().get(configs.get(this.id).getProperty().getBoolean());
                            }
                            return super.mousePressed(mc, mouseX, mouseY);
                        }
                    });
                    break;
                } case COLORMANAGER: {
                    buttonList.add(new GuiButton(i, x, y, EnumConfig.Type.COLORMANAGER.getMap().get(configs.get(i).getProperty().getInt())) {
                        @Override()
                        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                            if (super.mousePressed(mc, mouseX, mouseY)) {
                                configs.get(this.id).getProperty().set((configs.get(this.id).getProperty().getInt() + 1) % 3);
                                this.displayString = EnumConfig.Type.COLORMANAGER.getMap().get(configs.get(this.id).getProperty().getInt());
                                buttonList.get(buttonList.indexOf(this) + 1).enabled = configs.get(buttonList.indexOf(this)).getProperty().getInt() == 0;
                                IntStream.rangeClosed(3, 5).forEach(i -> buttonList.get(buttonList.indexOf(this) + i).enabled = configs.get(buttonList.indexOf(this)).getProperty().getInt() == 1);
                            }
                            return super.mousePressed(mc, mouseX, mouseY);
                        }
                    });
                    break;
                } case COLOR: {
                    buttonList.add(new GuiButton(i, x, y, EnumConfig.Type.COLOR.getMap().get(configs.get(i).getProperty().getInt())) {
                        @Override()
                        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                            if (super.mousePressed(mc, mouseX, mouseY)) {
                                configs.get(this.id).getProperty().set((configs.get(this.id).getProperty().getInt() + 1) % 16);
                                this.displayString = EnumConfig.Type.COLOR.getMap().get(configs.get(this.id).getProperty().getInt());
                            }
                            return super.mousePressed(mc, mouseX, mouseY);
                        }
                    });
                    break;
                } case RANGE: {
                    buttonList.add(new GuiSlider(i, x, y, 200, 20, "", "", Double.valueOf(configs.get(i).getProperty().getMinValue()), Double.valueOf(configs.get(i).getProperty().getMaxValue()), configs.get(i).getProperty().getInt(), true, true) {
                        @Override()
                        protected void mouseDragged(Minecraft mc, int x, int y) {
                            super.mouseDragged(mc, x, y);
                            configs.get(this.id).getProperty().set(this.getValueInt());
                        }

                        @Override()
                        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                            if (super.mousePressed(mc, mouseX, mouseY)) {
                                configs.get(this.id).getProperty().set(this.getValueInt());
                            }
                            return super.mousePressed(mc, mouseX, mouseY);
                        }
                    });
                    break;
                } default: {
                    // ignore
                }
            }
        });
        buttonList.stream().filter(button -> configs.get(buttonList.indexOf(button)).getType().equals(EnumConfig.Type.COLORMANAGER)).forEach(button -> {
            buttonList.get(buttonList.indexOf(button) + 1).enabled = configs.get(buttonList.indexOf(button)).getProperty().getInt() == 0;
            IntStream.rangeClosed(3, 5).forEach(i -> buttonList.get(buttonList.indexOf(button) + i).enabled = configs.get(buttonList.indexOf(button)).getProperty().getInt() == 1);
        });
    }
}
