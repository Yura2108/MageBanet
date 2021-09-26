package me.yura.magebanet.datatypes;

import lombok.Data;
import me.yura.magebanet.config.ConfigManager;
import me.yura.magebanet.utils.StringUtils;

import java.io.Serializable;

@Data
public class Punishment implements Serializable, Comparable<Punishment> {

    //Тип наказания
    private final PunishmentType type;

    //Время выдачи наказания
    private final long currentTime;

    //Длительность наказания
    private final long durationTime;

    //Наказуемый
    private final String punishable;

    //Каратель
    private final String punisher;

    //Причина
    private final String reason;

    //GUI сообщение
    private String guiMessage;


    public void updateGUI(){
        if(getDurationTime() == -1L) return;
        if(getType().equals(PunishmentType.MUTE)) return;

        this.guiMessage = StringUtils.formatMessage(punishable, punisher, reason, ((durationTime + currentTime) - System.currentTimeMillis()), ConfigManager.CONFIG.getMessage("ban-gui"));
    }


    @Override
    public int compareTo(Punishment o) {
        return o.getType().getWeight() - getType().getWeight();
    }

    public int compareToDate(Punishment o) {
        return (int) (o.getCurrentTime() - getCurrentTime());
    }
}
