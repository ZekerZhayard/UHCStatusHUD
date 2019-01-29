package io.github.zekerzhayard.uhcstatushud.feature;

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
import io.github.zekerzhayard.uhcstatushud.gui.ModGuiConfig;
import io.github.zekerzhayard.uhcstatushud.utils.DebugUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BoardRenderer {
    public static BoardRenderer instance = new BoardRenderer();
    public boolean isInUHC = false;
    public int playerWidth = 0;
    public int teamsWidth = 0;
    public CopyOnWriteArrayList<Map.Entry<String, String>> killerList = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<String> playerList = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<Map.Entry<String, String>> teamkillerList = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<ArrayList<String>> teamList = new CopyOnWriteArrayList<>();
    
    @SubscribeEvent()
    public void onRender(TickEvent.RenderTickEvent event) {
        if (KeyListener.instance.showPanel && (Minecraft.getMinecraft().currentScreen == null || Minecraft.getMinecraft().currentScreen instanceof ModGuiConfig || Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
            int x = EnumConfig.SOLOX.getProperty().getInt();
            int y = EnumConfig.SOLOY.getProperty().getInt();
            int baseWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth("=");
            if (EnumConfig.SHOWTITLE.getProperty().getBoolean()) {
                Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(EnumChatFormatting.YELLOW.toString() + (this.isInUHC ? EnumChatFormatting.BOLD.toString() : "") + UHCStatusHUD.NAME, x, y, 0xFFFFFF);
            }
            if (this.isInUHC) {
                String color = String.valueOf(EnumChatFormatting.func_175744_a(EnumConfig.PLAYERSCOLOR.getProperty().getInt())).replace("null", "");
                for (Map.Entry<String, String> killer : this.killerList) {
                    Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(color + killer.getKey(), x + baseWidth * 2, y += Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT, 0xFFFFFF);
                    Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(color + killer.getValue(), x + baseWidth * 4 + this.playerWidth, y, 0xFFFFFF);
                }
                x = EnumConfig.TEAMX.getProperty().getInt();
                y = EnumConfig.TEAMY.getProperty().getInt();
                color = String.valueOf(EnumChatFormatting.func_175744_a(EnumConfig.TEAMSCOLOR.getProperty().getInt())).replace("null", "");
                if (!this.teamkillerList.isEmpty()) {
                    Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(color + EnumChatFormatting.BOLD.toString() + "TEAMS:", x, y += Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT, 0xFFFFFF);
                }
                for (Map.Entry<String, String> team : this.teamkillerList) {
                    Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(color + team.getKey(), x, y += Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT, 0xFFFFFF);
                    Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(color + team.getValue(), x + baseWidth * 2 + this.teamsWidth, y, 0xFFFFFF);
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
            Matcher m = Pattern.compile("\\s•\\s\\((?<name1>[A-Za-z0-9_]{3,16})(,\\s(?<name2>[A-Za-z0-9_]{3,16})){0,1}(,\\s(?<name3>[A-Za-z0-9_]{3,16})){0,1}(,\\s(?<name4>[A-Za-z0-9_]{3,16})){0,1}\\)").matcher(text);
            if (m.find() && m.group(0).equals(text)) {
                DebugUtils.debug("Caught the message: " + m.group(0), event);
                this.teamList.add(Lists.newArrayList(m.group(0), Strings.nullToEmpty(m.group("name1")), Strings.nullToEmpty(m.group("name2")), Strings.nullToEmpty(m.group("name3")), Strings.nullToEmpty(m.group("name4"))));
            }
        }
        if (this.isInUHC) {
            Arrays.stream(EnumConfig.KILLTRIGGER.getProperty().getStringList()).map(str -> Pattern.compile(str).matcher(text)).filter(m -> m.find() && !m.group(0).contains(":")).findAny().ifPresent(m -> {
                DebugUtils.debug("Caught the killer name: " + m.group("killer"));
                this.playerList.add(m.group("killer"));
                this.killerList.clear();
                this.playerList.stream().collect(Collectors.groupingBy(str -> str, Collectors.counting())).entrySet().stream().sorted(Map.Entry.comparingByValue()).forEachOrdered(e -> {
                    this.killerList.add(0, Maps.immutableEntry(e.getKey(), e.getValue().toString() + " kill" + (e.getValue() == 1 ? "" : "s")));
                    this.playerWidth = Math.max(this.playerWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(e.getKey()));
                });
                this.teamList.stream().filter(list -> list.contains(m.group("killer"))).findAny().ifPresent(list -> this.teamList.add(list));
                this.teamkillerList.clear();
                this.teamList.stream().collect(Collectors.groupingBy(list -> list, Collectors.counting())).entrySet().stream().sorted(Map.Entry.comparingByValue()).filter(e -> e.getValue() > 1L).forEachOrdered(e -> {
                    this.teamkillerList.add(0, Maps.immutableEntry(e.getKey().get(0), String.valueOf(e.getValue() - 1L) + " kill" + (e.getValue() == 2 ? "" : "s")));
                    this.teamsWidth = Math.max(this.teamsWidth, Minecraft.getMinecraft().fontRendererObj.getStringWidth(e.getKey().get(0)));
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
}