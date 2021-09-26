package me.yura.magebanet.event;

import lombok.Data;
import me.yura.magebanet.datatypes.DataPlayer;
import me.yura.magebanet.datatypes.Punishment;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Data
public class PunishmentAddEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Punishment punishment;
    private final long currentTime;
    private final DataPlayer player;


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
