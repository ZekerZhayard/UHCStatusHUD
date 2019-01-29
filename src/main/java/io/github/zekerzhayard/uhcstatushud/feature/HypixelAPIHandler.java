package io.github.zekerzhayard.uhcstatushud.feature;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;

import io.github.zekerzhayard.uhcstatushud.utils.DebugUtils;
import net.hypixel.api.HypixelAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HypixelAPIHandler {
    private boolean isGettingApiKey = false;
    private boolean isGettingRoomName = false;
    private String apikey = "00000000-0000-0000-0000-000000000000";
    private String recentRoomName = "";

    @SubscribeEvent()
    public void onGuiRendered(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.gui instanceof GuiDownloadTerrain && !this.isGettingRoomName) {
            new Thread(this::sendWhereAmICommand).start();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatReceived(ClientChatReceivedEvent event) {
        String text = event.message.getUnformattedText();
        if (this.isGettingRoomName) {
            Matcher matcher = Pattern.compile("You\\sare\\scurrently\\s(connected\\sto\\sserver\\s(?<room>[A-Za-z0-9]{4,10}))|(in\\slimbo)").matcher(text);
            if (matcher.find() && matcher.group(0).equals(text)) {
                this.isGettingRoomName = false;
                DebugUtils.debug("Caught current room name: " + matcher.group("room"), event);
                if (matcher.group("room").contains("mega")) {
                    new Thread(() -> {
                        if (BoardRenderer.instance.isInUHC = this.getPlayerbyUUID(Minecraft.getMinecraft().thePlayer.getUniqueID()).get("mostRecentGameType").getAsString().equals("UHC")) {
                            if (!this.recentRoomName.equals(matcher.group("room"))) {
                                BoardRenderer.instance = new BoardRenderer();
                                BoardRenderer.instance.playerWidth = BoardRenderer.instance.teamsWidth = 0;
                                BoardRenderer.instance.playerList.clear();
                                BoardRenderer.instance.killerList.clear();
                                BoardRenderer.instance.teamList.clear();
                                BoardRenderer.instance.teamkillerList.clear();
                            }
                        }
                        this.recentRoomName = matcher.group("room");
                    }).start();
                } else {
                    BoardRenderer.instance.isInUHC = false;
                }
            }
        } else if (this.isGettingApiKey) {
            Matcher matcher = Pattern.compile("Your\\snew\\sAPI\\skey\\sis\\s(?<key>[0-9a-f]{4}([0-9a-f]{4}-){4}[0-9a-f]{12})").matcher(text);
            if (matcher.find() && matcher.group(0).equals(text)) {
                this.isGettingApiKey = false;
                DebugUtils.debug("Caught the api key: " + matcher.group("key"), event);
                this.apikey = matcher.group("key");
            }
        }
    }

    /**
     * <br>Send the command "/whereami" to get the room name.</br>
     * <br>If the command failed to be sent, it will be resent after 10 seconds.</br>
     */
    private void sendWhereAmICommand() {
        try {
            this.isGettingRoomName = true;
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/whereami");
            Thread.sleep(10000);
            if (this.isGettingRoomName) {
                this.sendWhereAmICommand();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendApiNewCommand() {
        try {
            this.isGettingApiKey = true;
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/api new");
            Thread.sleep(10000);
            if (this.isGettingApiKey) {
                this.sendApiNewCommand();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private JsonObject getPlayerbyUUID(UUID uuid) {
        HypixelAPI api = new HypixelAPI(UUID.fromString(this.apikey));
        try {
            return api.getPlayerByUuid(uuid).get().getPlayer();
        } catch (Exception e) {
            if (e.getMessage().contains("Invalid API key!")) {
                this.sendApiNewCommand();
                return this.getPlayerbyUUID(uuid);
            }
            return new JsonObject();
        }
    }
}