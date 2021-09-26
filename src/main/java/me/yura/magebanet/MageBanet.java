package me.yura.magebanet;

import me.yura.magebanet.commands.*;
import me.yura.magebanet.config.ConfigManager;
import me.yura.magebanet.data.DataManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class MageBanet extends JavaPlugin {

    private static Permission perms;
    
    private static MageBanet instance;

    private static final Logger logger = Logger.getLogger("Minecraft");

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        //Config load
        new ConfigManager(getInstance());

        //Load DataManager
        new DataManager(getInstance());

        //Register events
        Bukkit.getPluginManager().registerEvents(new ListenerClass(), this);

        //Register commands
        new KickCommand("kick", "MageBanet.kick", "");
        new TempMuteCommand("tempmute", "MageBanet.tempmute", "");
        new MuteCommand("mute", "MageBanet.mute", "MageBanet.unmute");
        new TempBanCommand("tempban", "MageBanet.tempban", "");
        new BanCommand("ban", "MageBanet.ban", "MageBanet.unban");

        //Register admins commands
        new AdminBanCommand("aban", "MageBanet.aban", "MageBanet.aunban");
        new AdminMuteCommand("amute", "MageBanet.amute", "MageBanet.aunmute");

        new PunishmentsListCommand("puns", "MageBanet.list", "");

        if(!setupPermissions()) {
            logger.warning("Vault не обнаружен!");
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        DataManager.DATA.saveData(this);
    }


    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static MageBanet getInstance() {
        return instance;
    }
}
