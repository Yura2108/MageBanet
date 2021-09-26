package me.yura.magebanet;

import me.yura.magebanet.config.ConfigManager;
import me.yura.magebanet.data.DataManager;
import me.yura.magebanet.datatypes.DataPlayer;
import me.yura.magebanet.datatypes.Punishment;
import me.yura.magebanet.datatypes.PunishmentType;
import me.yura.magebanet.utils.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.ArrayList;

public class ListenerClass implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event){
        DataPlayer dataPlayer = DataManager.DATA.getPlayer(event.getName());
        dataPlayer.updatePunishments();

        ArrayList<Punishment> puns = dataPlayer.getActivePunishments(PunishmentType.BAN, PunishmentType.ADMIN_BAN);

        if(puns.isEmpty()) return;

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, puns.get(0).getGuiMessage());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatEvent(AsyncPlayerChatEvent event){
        DataPlayer dataPlayer = DataManager.DATA.getPlayer(event.getPlayer().getName());
        dataPlayer.updatePunishments();

        ArrayList<Punishment> puns = dataPlayer.getActivePunishments(PunishmentType.MUTE, PunishmentType.ADMIN_MUTE);

        if(puns.isEmpty()) return;

        Punishment pun = puns.get(0);

        event.setCancelled(true);

        event.getPlayer().sendMessage(StringUtils.formatMessage(pun.getPunishable(), pun.getPunisher(), pun.getReason(),
                ConfigManager.CONFIG.getMessage("chat-cancel")));
    }


}
