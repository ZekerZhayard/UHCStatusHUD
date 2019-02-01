package io.github.zekerzhayard.uhcstatushud.feature;

import java.util.function.Consumer;

import com.google.common.collect.ImmutableMap;

import org.lwjgl.input.Keyboard;

import io.github.zekerzhayard.uhcstatushud.UHCStatusHUD;
import io.github.zekerzhayard.uhcstatushud.config.EnumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyListener {
    public static KeyListener instance = new KeyListener();
    /** 0=Show All, 1=Show max lines which set in config, 2=Hide */
    public int showPanelMode = 0;
    private ImmutableMap<KeyBinding, Consumer<KeyBinding>> keyMap = new ImmutableMap.Builder<KeyBinding, Consumer<KeyBinding>>().put(new KeyBinding("key.panel.show", Keyboard.KEY_RSHIFT, UHCStatusHUD.NAME), k -> this.showPanelMode = (this.showPanelMode + 1) % 3).put(new KeyBinding("key.autosend", Keyboard.KEY_H, UHCStatusHUD.NAME), k -> {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        p.sendChatMessage(EnumConfig.AUTOSEND.getProperty().getString().replace("{X}", String.valueOf((long) p.posX)).replace("{Y}", String.valueOf((long) p.posY)).replace("{Z}", String.valueOf((long) p.posZ)));
    }).build();

    private KeyListener() {
        this.keyMap.keySet().forEach(ClientRegistry::registerKeyBinding);
    }

    @SubscribeEvent()
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        this.keyMap.entrySet().stream().filter(e -> e.getKey().isPressed()).forEach(e -> e.getValue().accept(e.getKey()));
    }
}