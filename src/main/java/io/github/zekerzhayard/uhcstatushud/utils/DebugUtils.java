package io.github.zekerzhayard.uhcstatushud.utils;

import io.github.zekerzhayard.uhcstatushud.UHCStatusHUD;
import io.github.zekerzhayard.uhcstatushud.config.EnumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;

public class DebugUtils {
    public static void debug(String message, Event... event) {
        UHCStatusHUD.LOGGER.debug(message);
        if (EnumConfig.DEBUGMODE.getProperty().getBoolean()) {
            if (Minecraft.getMinecraft().thePlayer != null) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(String.format("%s[%s] %s%s", EnumChatFormatting.YELLOW, UHCStatusHUD.NAME, EnumChatFormatting.GREEN, message)));
            }
        } else if (event != null && event.length == 1) {
            event[0].setCanceled(true);
        }
    }

    public static void info(String message) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(String.format("%s[%s] %s%s", EnumChatFormatting.YELLOW, UHCStatusHUD.NAME, EnumChatFormatting.GREEN, message)));
        }
    }
}