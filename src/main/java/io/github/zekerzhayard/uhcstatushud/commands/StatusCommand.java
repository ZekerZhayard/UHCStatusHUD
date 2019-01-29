package io.github.zekerzhayard.uhcstatushud.commands;

import java.util.List;

import io.github.zekerzhayard.uhcstatushud.feature.BoardRenderer;
import io.github.zekerzhayard.uhcstatushud.utils.DebugUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

public class StatusCommand extends CommandBase {
    @Override()
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return BoardRenderer.instance.isInUHC;
    }

    @Override()
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap().stream().map(n -> n.getGameProfile().getName()).toArray(String[]::new));
        }
        return null;
    }

    @Override()
    public String getCommandName() {
        return "uhcstatus";
    }

    @Override()
    public String getCommandUsage(ICommandSender sender) {
        return "/uhcstatus <name>";
    }

    private int count = 0;
    @Override()
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1) {
            this.count = 0;
            BoardRenderer.instance.killerList.stream().filter(entry -> args[0].equalsIgnoreCase(entry.getKey())).forEachOrdered(entry -> {
                DebugUtils.info(entry.getKey() + " : " + entry.getValue());
                this.count = 1;
            });
            BoardRenderer.instance.teamkillerList.stream().filter(entry -> entry.getKey().toLowerCase().contains(args[0].toLowerCase())).forEachOrdered(entry -> DebugUtils.info(entry.getKey() + " : " + entry.getValue()));
            if (this.count == 0) {
                DebugUtils.info(I18n.format("command.query"));
            }
        } else {
            DebugUtils.info(this.getCommandUsage(sender));
        }
    }
}