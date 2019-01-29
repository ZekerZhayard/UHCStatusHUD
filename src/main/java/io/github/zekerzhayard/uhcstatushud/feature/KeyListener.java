package io.github.zekerzhayard.uhcstatushud.feature;

import org.lwjgl.input.Keyboard;

import io.github.zekerzhayard.uhcstatushud.UHCStatusHUD;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyListener {
    public static KeyListener instance = new KeyListener();
    public boolean showPanel = true;
    public KeyBinding showPanelKey = new KeyBinding(UHCStatusHUD.NAME, Keyboard.KEY_RSHIFT, "panel.show");

    @SubscribeEvent()
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (this.showPanelKey.isPressed()) {
            this.showPanel = !this.showPanel;
        }
    }
}