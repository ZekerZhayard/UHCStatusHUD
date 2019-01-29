package io.github.zekerzhayard.uhcstatushud.config;

import io.github.zekerzhayard.uhcstatushud.UHCStatusHUD;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Property;

public enum EnumConfig {
    SHOWTITLE("Show Title", true, I18n.format("config.panel.title")),
    DEBUGMODE("Debug Mode", false, I18n.format("config.debugmode")),
    KILLTRIGGER("Chat Trigger", new String[] {"[A-Za-z0-9_]{3,16}\\swas\\s.*\\sby\\s(?<killer>[A-Za-z0-9_]{3,16})", "被(?<killer>[A-Za-z0-9_]{3,16})[一-龥]"}, I18n.format("config.trigger.kill")),
    PARTNERTRIGGER("Start Trigger", new String[] {"\\s+Your\\spartners\\sare\\s.*", "\\s+你的队友是\\s.*"}, I18n.format("config.trigger.start")),
    SOLOX("Solo Panel X", 0, I18n.format("config.x")),
    SOLOY("Solo Panel Y", 0, I18n.format("config.y")),
    TEAMX("Team Panel X", 0, I18n.format("config.x")),
    TEAMY("Team Panel Y", 100, I18n.format("config.y")),
    PLAYERSCOLOR("Players Color", EnumChatFormatting.RED.getColorIndex(), I18n.format("config.color.players"), 0, 15),
    TEAMSCOLOR("Teams Color", EnumChatFormatting.GREEN.getColorIndex(), I18n.format("config.color.teams"), 0, 15);

    private Property property;
    private Object[] args;

    private EnumConfig(String name, Object defaultValue, String comment) {
        this.args = new Object[] {UHCStatusHUD.MODID, name, defaultValue, comment};
    }

    private EnumConfig(String name, String[] defaultValues, String comment) {
        this.args = new Object[] {UHCStatusHUD.MODID, name, defaultValues, comment};
    }

    private EnumConfig(String name, int defaultValue, String comment, int minValue, int maxValue) {
        this.args = new Object[] {UHCStatusHUD.MODID, name, defaultValue, comment, minValue, maxValue};
    }

    public Property getProperty() {
        return this.property;
    }

    public Object[] getArgs() {
        return this.args;
    }

    public void setProperty(Property property) {
        this.property = property;
    }
}