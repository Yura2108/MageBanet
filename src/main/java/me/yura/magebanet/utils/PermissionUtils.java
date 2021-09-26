package me.yura.magebanet.utils;

import me.yura.magebanet.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.yura.magebanet.MageBanet.getPermissions;

public class PermissionUtils {

    public static boolean checkPunishablePerms(CommandSender sender, Player punishable){
        if(sender instanceof ConsoleCommandSender) return true;

        if(ConfigManager.CONFIG.getGroupWeight(getPlayerGroup((Player) sender)) <
                ConfigManager.CONFIG.getGroupWeight(getPlayerGroup(punishable))){


            sender.sendMessage("§eОшибка: §cВы пытаетесь наказать игрока с приоритетом §c§lвыше §cвашего!");
            return false;

        }

        return true;

    }

    public static boolean checkPunishablePerms(CommandSender sender, String punishable){
        if(sender instanceof ConsoleCommandSender) return true;

        String playerGroup, senderGroup;

        if(Bukkit.getPlayer(punishable) != null) {
            Player player = Bukkit.getPlayer(punishable);

            senderGroup = getPlayerGroup((Player) sender);
            playerGroup = getPlayerGroup(player);
        }else{
            OfflinePlayer player = Bukkit.getOfflinePlayer(punishable);

            senderGroup = getPlayerGroup((Player) sender);
            playerGroup = getPlayerGroup(player);
        }

        if(ConfigManager.CONFIG.getGroupWeight(senderGroup) <
                ConfigManager.CONFIG.getGroupWeight(playerGroup)){


            sender.sendMessage("§eОшибка: §cВы пытаетесь наказать игрока с приоритетом §c§lвыше §cвашего!");
            return false;

        }

        return true;
    }


    public static String getPlayerGroup(Player player) {
        return getPermissions().getPrimaryGroup(player);
    }

    public static String getPlayerGroup(OfflinePlayer player) {
        //Check this
        return getPermissions().getPrimaryGroup(Bukkit.getWorlds().get(0).getName(), player);
    }

}
