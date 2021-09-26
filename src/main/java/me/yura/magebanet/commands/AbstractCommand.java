package me.yura.magebanet.commands;

import me.yura.magebanet.MageBanet;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class AbstractCommand implements CommandExecutor {

    private final String permission;
    private final String unPermission;

    public AbstractCommand(String command, String permission, String unPermission) {
        this.permission = permission;
        this.unPermission = unPermission;
        Bukkit.getPluginCommand(command).setExecutor(this);
        if(!unPermission.isEmpty()) {
            String unCommand;
            if(command.startsWith("a")) unCommand = "aun" + command.replaceFirst("a", "");
            else unCommand = "un" + command;
            Bukkit.getPluginCommand(unCommand).setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.startsWith("un") || label.startsWith("aun")) {
            if(unPermission.isEmpty()) {
                System.err.println("[MageBanet] unPermission not set to " + label);
                return true;
            }

            if(checkPermission(sender, unPermission)) execute(sender, args, true);

        }else if(checkPermission(sender, permission)){
            execute(sender, args, false);
        }


        return true;
    }

    private boolean checkPermission(CommandSender sender, String perm){
        if(sender.hasPermission(perm)){
            return true;
        }else{
            sender.sendMessage("§eОшибка: §cНедостаточно прав!");
            return false;
        }
    }

    abstract void execute(CommandSender sender, String[] args, boolean unCommand);
}
