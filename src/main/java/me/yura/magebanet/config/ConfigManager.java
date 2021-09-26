package me.yura.magebanet.config;

import me.yura.magebanet.MageBanet;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.TreeMap;

public class ConfigManager {

    public static ConfigManager CONFIG;

    private final TreeMap<String, Integer> groupsWeight = new TreeMap<>();

    private final TreeMap<String, Integer> banLimits = new TreeMap<>();
    private final TreeMap<String, Integer> muteLimits = new TreeMap<>();

    private final TreeMap<String, String> messages = new TreeMap<>();

    public ConfigManager(MageBanet instance) {
        CONFIG = this;

        instance.saveDefaultConfig();

        FileConfiguration config = instance.getConfig();

        ConfigurationSection groupsSection = config.getConfigurationSection("groups");

        for(String key : groupsSection.getKeys(false)) {
            groupsWeight.put(key, groupsSection.getInt(key + ".weight", 999));

            if(groupsSection.contains(key + ".limits")){
                if(groupsSection.contains(key + ".limits.ban")) banLimits.put(key, groupsSection.getInt(key + ".limits.ban"));
                if(groupsSection.contains(key + ".limits.mute")) muteLimits.put(key, groupsSection.getInt(key + ".limits.mute"));
            }
        }

        //Загружаем сообщения
        for(String key : config.getConfigurationSection("messages").getKeys(false)) {
            messages.put(key, ChatColor.translateAlternateColorCodes('&', config.getString("messages." + key)));
        }
    }


    public int getGroupWeight(String group){
        return groupsWeight.getOrDefault(group, Integer.MAX_VALUE);
    }

    public int getBanLimit(String group){
        return banLimits.getOrDefault(group, Integer.MAX_VALUE);
    }

    public int getMuteLimits(String group){
        return muteLimits.getOrDefault(group, Integer.MAX_VALUE);
    }

    public String getMessage(String path){
        try{
            return messages.get(path);
        }catch (NullPointerException exception) {
            System.err.println("[MageBanet] В конфиге сообщений не установлена настройка для пути: " + path);
            return "";
        }
    }

}
