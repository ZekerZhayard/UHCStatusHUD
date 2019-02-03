package io.github.zekerzhayard.uhcstatushud.config;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

import io.github.zekerzhayard.uhcstatushud.UHCStatusHUD;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Property;

public enum EnumConfig {
    SHOWTITLE("Show Title", true, I18n.format("config.panel.title"), EnumConfig.Type.BOOLEAN),
    DEBUGMODE("Debug Mode", false, I18n.format("config.debugmode"), EnumConfig.Type.BOOLEAN),
    KILLTRIGGER("Chat Trigger", new String[] {"[A-Za-z0-9_]{3,16}\\swas\\s.*\\sby\\s(?<killer>[A-Za-z0-9_]{3,16})", "[A-Za-z0-9_]{3,16}\\stried\\sto\\sswim\\sin\\slava\\sto\\sescape\\s(?<killer>[A-Za-z0-9_]{3,16})", "被(?<killer>[A-Za-z0-9_]{3,16})[一-龥]"}, I18n.format("config.trigger.kill"), EnumConfig.Type.MANUAL),
    PARTNERTRIGGER("Start Trigger", new String[] {"\\s+Your\\spartners\\sare\\s.*", "\\s+你的队友是\\s.*"}, I18n.format("config.trigger.start"), EnumConfig.Type.MANUAL),
    AUTOSEND("Message to Auto Send", "My Coords: X={X} ,Y={Y} ,Z={Z}", I18n.format("config.autosend"), EnumConfig.Type.MANUAL),
    MAXDISPLAY("Max display", Integer.MAX_VALUE, I18n.format("config.maxdisplay"), 0, Integer.MAX_VALUE, EnumConfig.Type.MANUAL),
    SOLOX("Solo Panel X", 0, I18n.format("config.x"), EnumConfig.Type.LOCATION),
    SOLOY("Solo Panel Y", 0, I18n.format("config.y"), EnumConfig.Type.LOCATION),
    TEAMX("Team Panel X", 0, I18n.format("config.x"), EnumConfig.Type.LOCATION),
    TEAMY("Team Panel Y", 100, I18n.format("config.y"), EnumConfig.Type.LOCATION),
    PLAYERSCOLORTYPE("Players Color Type", 0, "", 0, 2, EnumConfig.Type.COLORMANAGER),
    PLAYERSCOLOR("Players Color", EnumChatFormatting.RED.getColorIndex(), I18n.format("config.color.players"), 0, 15, EnumConfig.Type.COLOR),
    PLAYERSCOLORALPHA("Players Color Alpha", 0xFF, "", 0x01, 0xFF, EnumConfig.Type.RANGE),
    PLAYERSCOLORRED("Players Color Red", 0xFF, "", 0x01, 0xFF, EnumConfig.Type.RANGE),
    PLAYERSCOLORGREEN("Players Color Green", 0x55, "", 0x01, 0xFF, EnumConfig.Type.RANGE),
    PLAYERSCOLORBLUE("Players Color Blue", 0x55, "", 0x01, 0xFF, EnumConfig.Type.RANGE),
    TEAMSCOLORTYPE("Teams Color Type", 0, "", 0, 2, EnumConfig.Type.COLORMANAGER),
    TEAMSCOLOR("Teams Color", EnumChatFormatting.GREEN.getColorIndex(), I18n.format("config.color.teams"), 0, 15, EnumConfig.Type.COLOR),
    TEAMSCOLORALPHA("Teams Color Alpha", 0x50, "", 0x01, 0xFF, EnumConfig.Type.RANGE),
    TEAMSCOLORRED("Teams Color Red", 0x55, "", 0x01, 0xFF, EnumConfig.Type.RANGE),
    TEAMSCOLORGREEN("Teams Color Green", 0xFF, "", 0x01, 0xFF, EnumConfig.Type.RANGE),
    TEAMSCOLORBLUE("Teams Color Blue", 0x55, "", 0x01, 0xFF, EnumConfig.Type.RANGE),
    PANELCOLORTYPE("Panel Color Type", 2, "", 0, 2, EnumConfig.Type.COLORMANAGER),
    PANELCOLOR("Panel Color", EnumChatFormatting.BLACK.getColorIndex(), I18n.format("config.color.panel"), 0, 15, EnumConfig.Type.COLOR),
    PANELCOLORALPHA("Panel Color Alpha", 0x50, I18n.format("config.panel.alpha"), 0x01, 0xFF, EnumConfig.Type.RANGE),
    PANELCOLORRED("Panel Color Red", 0x00, "", 0x01, 0xFF, EnumConfig.Type.RANGE),
    PANELCOLORGREEN("Panel Color Green", 0x00, "", 0x01, 0xFF, EnumConfig.Type.RANGE),
    PANELCOLORBLUE("Panel Color Blue", 0x00, "", 0x01, 0xFF, EnumConfig.Type.RANGE);

    private EnumConfig.Type type;
    private Object[] args;
    private Property property;

    private EnumConfig(String name, Object defaultValue, String comment, EnumConfig.Type type) {
        this.type = type;
        this.args = new Object[] {UHCStatusHUD.MODID, name, defaultValue, comment};
    }

    private EnumConfig(String name, String[] defaultValues, String comment, EnumConfig.Type type) {
        this.type = type;
        this.args = new Object[] {UHCStatusHUD.MODID, name, defaultValues, comment};
    }

    private EnumConfig(String name, int defaultValue, String comment, int minValue, int maxValue, EnumConfig.Type type) {
        this.type = type;
        this.args = new Object[] {UHCStatusHUD.MODID, name, defaultValue, comment, minValue, maxValue};
    }

    public EnumConfig.Type getType() {
        return this.type;
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

    public enum Type {
        BOOLEAN(ImmutableMap.of(true, EnumChatFormatting.GREEN + "true", false, EnumChatFormatting.RED + "false")),
        COLORMANAGER(ImmutableMap.of(0, "Classic", 1, "Chroma", 2, "Rainbow")),
        COLOR(new ImmutableMap.Builder<Object, String>().putAll(Arrays.stream(EnumChatFormatting.values()).filter(EnumChatFormatting::isColor).collect(Collectors.toMap(EnumChatFormatting::getColorIndex, ecf -> ecf.toString() + ecf.name()))).build()),
        RANGE(ImmutableMap.of()),
        LOCATION(ImmutableMap.of()),
        MANUAL(ImmutableMap.of());

        private ImmutableMap<Object, String> map;

        private Type(ImmutableMap<Object, String> map) {
            this.map = map;
        }

        public ImmutableMap<Object, String> getMap() {
            return this.map;
        }
    }
}