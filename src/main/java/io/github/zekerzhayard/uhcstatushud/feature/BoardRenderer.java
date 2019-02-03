package io.github.zekerzhayard.uhcstatushud.feature;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.github.zekerzhayard.uhcstatushud.UHCStatusHUD;
import io.github.zekerzhayard.uhcstatushud.config.EnumConfig;
import io.github.zekerzhayard.uhcstatushud.gui.ModGuiSettings;
import io.github.zekerzhayard.uhcstatushud.utils.DebugUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BoardRenderer {
    public static BoardRenderer instance = new BoardRenderer();
    public int playerWidth = 0;
    public int playerkillWidth = 0;
    public int teamsWidth = 0;
    public int teamskillWidth = 0;
    public CopyOnWriteArrayList<Map.Entry<String, String>> killerList = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<String> playerList = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<Map.Entry<String, String>> teamkillerList = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<ArrayList<String>> teamList = new CopyOnWriteArrayList<>();
    
    @SubscribeEvent()
    public void onRender(TickEvent.RenderTickEvent event) {
        if ((KeyListener.instance.showPanelMode != 2 && (Minecraft.getMinecraft().currentScreen == null || Minecraft.getMinecraft().currentScreen instanceof GuiChat)) || Minecraft.getMinecraft().currentScreen instanceof ModGuiSettings) {
            int x = EnumConfig.SOLOX.getProperty().getInt();
            int y = EnumConfig.SOLOY.getProperty().getInt();
            int baseWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth("=");
            if (!this.killerList.isEmpty() && (HypixelAPIHandler.isInUHC || Minecraft.getMinecraft().currentScreen instanceof ModGuiSettings)) {
                Gui.drawRect(x - 4, y - 2, x + baseWidth * 4 + this.playerWidth + this.playerkillWidth + 4, y + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * (Math.min(this.killerList.size(), KeyListener.instance.showPanelMode == 1 ? EnumConfig.MAXDISPLAY.getProperty().getInt() : 111) + 1) + 2, this.getColorHex(EnumConfig.PANELCOLORTYPE));
            }
            if (EnumConfig.SHOWTITLE.getProperty().getBoolean()) {
                Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(EnumChatFormatting.YELLOW.toString() + (HypixelAPIHandler.isInUHC ? EnumChatFormatting.BOLD.toString() : "") + UHCStatusHUD.NAME, x, y, 0xFFFFFF);
            }
            if (HypixelAPIHandler.isInUHC || Minecraft.getMinecraft().currentScreen instanceof ModGuiSettings) {
                for (Map.Entry<String, String> killer : this.killerList) {
                    if (KeyListener.instance.showPanelMode == 1 && this.killerList.indexOf(killer) + 1 > EnumConfig.MAXDISPLAY.getProperty().getInt()) {
                        break;
                    }
                    Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(killer.getKey(), x + baseWidth * 2, y += Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT, this.getColorHex(EnumConfig.PLAYERSCOLORTYPE));
                    Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(killer.getValue(), x + baseWidth * 4 + this.playerWidth, y, this.getColorHex(EnumConfig.PLAYERSCOLORTYPE));
                }
                x = EnumConfig.TEAMX.getProperty().getInt();
                y = EnumConfig.TEAMY.getProperty().getInt();
                if (!this.teamkillerList.isEmpty()) {
                    Gui.drawRect(x - 4, y - 2, x + baseWidth * 4 + this.teamsWidth + this.teamskillWidth + 4, y + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT * (Math.min(this.teamkillerList.size(), KeyListener.instance.showPanelMode == 1 ? EnumConfig.MAXDISPLAY.getProperty().getInt() : 111) + 1) + 2, this.getColorHex(EnumConfig.PANELCOLORTYPE));
                    Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(EnumChatFormatting.BOLD.toString() + "TEAMS:", x, y, this.getColorHex(EnumConfig.TEAMSCOLORTYPE));
                }
                for (Map.Entry<String, String> team : this.teamkillerList) {
                    if (KeyListener.instance.showPanelMode == 1 && this.teamkillerList.indexOf(team) + 1 > EnumConfig.MAXDISPLAY.getProperty().getInt()) {
                        break;
                    }
                    Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(team.getKey(), x, y += Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT, this.getColorHex(EnumConfig.TEAMSCOLORTYPE));
                    Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(team.getValue(), x + baseWidth * 2 + this.teamsWidth, y, this.getColorHex(EnumConfig.TEAMSCOLORTYPE));
                }
            }
        }
    }

    private boolean isSendingWho = false;
    private long sendTime;
    @SubscribeEvent()
    public void onChatReceived(ClientChatReceivedEvent event) {
        String text = event.message.getUnformattedText();
        Arrays.stream(EnumConfig.PARTNERTRIGGER.getProperty().getStringList()).map(str -> Pattern.compile(str).matcher(text)).filter(m -> m.find() && m.group(0).equals(text)).findAny().ifPresent(m -> {
            DebugUtils.debug("Caught the message: " + m.group(0));
            this.playerWidth = this.teamsWidth = 0;
            new Thread(this::sendWhoCommand).start();
        });
        this.isSendingWho = System.currentTimeMillis() - sendTime < 3000L;
        if (this.isSendingWho) {
            if (text.equals("TEAMS: ")) {
                DebugUtils.debug("Start to caught team messages.", event);
            }
            Matcher m = Pattern.compile("\\s\u2022\\s\\((?<name1>[A-Za-z0-9_]{3,16})(,\\s(?<name2>[A-Za-z0-9_]{3,16})){0,1}(,\\s(?<name3>[A-Za-z0-9_]{3,16})){0,1}(,\\s(?<name4>[A-Za-z0-9_]{3,16})){0,1}\\)").matcher(text);
            if (m.find() && m.group(0).equals(text)) {
                DebugUtils.debug("Caught the message: " + m.group(0), event);
                this.teamList.add(Lists.newArrayList(m.group(0), Strings.nullToEmpty(m.group("name1")), Strings.nullToEmpty(m.group("name2")), Strings.nullToEmpty(m.group("name3")), Strings.nullToEmpty(m.group("name4"))));
            }
        }
        if (HypixelAPIHandler.isInUHC) {
            Arrays.stream(EnumConfig.KILLTRIGGER.getProperty().getStringList()).map(str -> Pattern.compile(str).matcher(text)).filter(m -> m.find() && !m.group(0).contains(":")).findAny().ifPresent(m -> {
                DebugUtils.debug("Caught the killer name: " + m.group("killer"));
                this.playerList.add(m.group("killer"));
                this.killerList.clear();
                this.playerList.stream().collect(Collectors.groupingBy(str -> str, Collectors.counting())).entrySet().stream().sorted(Map.Entry.comparingByValue()).forEachOrdered(e -> {
                    Map.Entry<String, String> entry = Maps.immutableEntry(e.getKey(), e.getValue().toString() + " kill" + (e.getValue() == 1 ? "" : "s"));
                    this.killerList.add(0, entry);
                    this.playerWidth = Math.max(this.playerWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(entry.getKey()));
                    this.playerkillWidth = Math.max(this.playerkillWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(entry.getValue()));
                });
                this.teamList.stream().filter(list -> list.contains(m.group("killer"))).findAny().ifPresent(list -> this.teamList.add(list));
                this.teamkillerList.clear();
                this.teamList.stream().collect(Collectors.groupingBy(list -> list, Collectors.counting())).entrySet().stream().sorted(Map.Entry.comparingByValue()).filter(e -> e.getValue() > 1L).forEachOrdered(e -> {
                    Map.Entry<String, String> entry = Maps.immutableEntry(e.getKey().get(0), String.valueOf(e.getValue() - 1L) + " kill" + (e.getValue() == 2 ? "" : "s"));
                    this.teamkillerList.add(0, entry);
                    this.teamsWidth = Math.max(this.teamsWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(entry.getKey()));
                    this.teamskillWidth = Math.max(this.teamskillWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(entry.getValue()));
                });
            });
        }
    }

    private void sendWhoCommand() {
        try {
            this.isSendingWho = true;
            this.sendTime = System.currentTimeMillis();
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/who");
            Thread.sleep(5000);
            if (this.isSendingWho && this.teamList.isEmpty()) {
                this.sendWhoCommand();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int getColorHex(EnumConfig colorManager) {
        int index = Arrays.binarySearch(EnumConfig.values(), colorManager);
        int alpha = EnumConfig.values()[index + 2].getProperty().getInt() << 24;
        switch (colorManager.getProperty().getInt()) {
            case 0: {
                return alpha + Minecraft.getMinecraft().fontRendererObj.getColorCode(Integer.toHexString(EnumConfig.values()[index + 1].getProperty().getInt()).toCharArray()[0]);
            } case 1: {
                return alpha + (EnumConfig.values()[index + 3].getProperty().getInt() << 16) + (EnumConfig.values()[index + 4].getProperty().getInt() << 8) + EnumConfig.values()[index + 5].getProperty().getInt();
            } case 2: {
                return alpha + Color.HSBtoRGB(System.currentTimeMillis() % 1000L / 1000.0F, 0.8F, 0.8F);
            } default: {
                return 0;
            }
        }
    }
}