package io.github.zekerzhayard.uhcstatushud;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.zekerzhayard.uhcstatushud.commands.StatusCommand;
import io.github.zekerzhayard.uhcstatushud.config.ModConfig;
import io.github.zekerzhayard.uhcstatushud.feature.BoardRenderer;
import io.github.zekerzhayard.uhcstatushud.feature.HypixelAPIHandler;
import io.github.zekerzhayard.uhcstatushud.feature.KeyListener;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

@Mod(modid = UHCStatusHUD.MODID, name = UHCStatusHUD.NAME, version = UHCStatusHUD.VERSION, clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]", guiFactory = "io.github.zekerzhayard.uhcstatushud.gui.ModGuiFactory", updateJSON = "https://raw.githubusercontent.com/ZekerZhayard/UHCStatusHUD/master/update.json")
public class UHCStatusHUD {
    public final static String MODID = "uhcstatushud";
    public final static String NAME = "UHCStatusHUD";
    public final static String VERSION = "@VERSION@";
    public final static Logger LOGGER = LogManager.getLogger(UHCStatusHUD.NAME);

    private ArrayList<Object> features = Lists.newArrayList(BoardRenderer.instance, new HypixelAPIHandler(), KeyListener.instance);

    @Mod.EventHandler()
    public void preInit(FMLPreInitializationEvent event) {
        ModConfig.instance.setConfig(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler()
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new StatusCommand());
        ClientRegistry.registerKeyBinding(KeyListener.instance.showPanelKey);
    }

    @SubscribeEvent()
    public void onConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (!Minecraft.getMinecraft().isSingleplayer() && Minecraft.getMinecraft().getCurrentServerData().serverIP.contains("hypixel.net")) {
            this.features.forEach(MinecraftForge.EVENT_BUS::register);
        }
    }

    @SubscribeEvent()
    public void onDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        this.features.forEach(MinecraftForge.EVENT_BUS::unregister);
    }
}