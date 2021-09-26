package me.yura.magebanet.utils;

import me.yura.magebanet.config.ConfigManager;
import me.yura.magebanet.datatypes.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandUtils {

    public static boolean validName(CommandSender sender, String name, PunishmentType type){
        if(sender.getName().equals(name)) {
            sender.sendMessage("§eОшибка: §cВы не можете применить это к самому себе!");
            return false;
        }
        if(type.equals(PunishmentType.KICK) || type.equals(PunishmentType.MUTE) || type.equals(PunishmentType.ADMIN_MUTE)){
            if(Bukkit.getPlayer(name) == null) {
                sender.sendMessage("§eОшибка: §cИгрок не найден!");
                return false;
            }
        }else if(Bukkit.getOfflinePlayer(name) == null || !Bukkit.getOfflinePlayer(name).hasPlayedBefore()){
            sender.sendMessage("§eОшибка: §cИгрок не найден!");
            return false;
        }

        return true;
    }

    public static String buildReason(String[] args, int startIndex){
        StringBuilder builder = new StringBuilder();
        for(int i = startIndex; i < args.length; i++){
            builder.append(args[i]).append(" ");
        }
        return builder.substring(0, builder.length() - 1).replaceAll("&", "§");

    }

    public static boolean validTime(CommandSender sender, long time, PunishmentType type) {
        if(time == 0){
            sender.sendMessage("§eОшибка: §cВремя указано неправильно!");
            return false;
        }

        if(sender instanceof ConsoleCommandSender) return true;

        if(type.equals(PunishmentType.MUTE)) {
            if (time > ConfigManager.CONFIG.getMuteLimits(PermissionUtils.getPlayerGroup((Player) sender))) {
                sender.sendMessage("§eОшибка: §сВы указали время больше вашего лимита!");
                return false;
            }
        }else if(type.equals(PunishmentType.BAN)) {
            if (time > ConfigManager.CONFIG.getBanLimit(PermissionUtils.getPlayerGroup((Player) sender))) {
                sender.sendMessage("§eОшибка: §сВы указали время больше вашего лимита!");
                return false;
            }
        }

        return true;
    }

}
