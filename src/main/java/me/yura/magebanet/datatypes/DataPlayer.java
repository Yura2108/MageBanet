package me.yura.magebanet.datatypes;


import lombok.AllArgsConstructor;
import lombok.Getter;
import me.yura.magebanet.event.PunishmentRemoveEvent;
import org.bukkit.Bukkit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

@AllArgsConstructor
public class DataPlayer implements Serializable {

    @Getter private final String playerName;

    //Список активных наказаний игрока
    private final ArrayList<Punishment> activePunishments = new ArrayList<>();

    /**
     Метод добавления наказания
     **/
    public void addPunishment(Punishment punishment){
        activePunishments.add(punishment);
    }

    /**
     * @param who Кто снимает наказание
     * @param reason Причина, для ивента
     * @param types Типы наказаний, которые он снимает
     Метод очистки наказаний определённого типа

     **/
    public int clearPuns(String who, String reason, PunishmentType... types){
        int before = activePunishments.size();
        ArrayList<Punishment> tempList = new ArrayList<>();

        for(PunishmentType type : types){
            for (Punishment pun : activePunishments){
                if(pun.getType().equals(type)) tempList.add(pun);

                //Вызываем ивент
                PunishmentRemoveEvent event = new PunishmentRemoveEvent(pun, who, System.currentTimeMillis(), reason);
                Bukkit.getPluginManager().callEvent(event);
            }
        }

        //Удаление всех объектов итерации
        activePunishments.removeAll(tempList);

        return before - activePunishments.size();
    }

    /**
     Метод для проверки на текущие наказания
     */
    public boolean containsPunishment(PunishmentType... type){
        for(Punishment pun : activePunishments){
            if(Arrays.asList(type).contains(pun.getType())) return true;
        }

        return false;
    }

    /**
     Метод обновления текущих наказаний. Иначе: проверка на истечение времени
     **/
    @SuppressWarnings("unchecked")
    public void updatePunishments(){
        ArrayList<Punishment> temp = (ArrayList<Punishment>) activePunishments.clone();
        for(Punishment punishment : temp){
            if(punishment.getDurationTime() == -1L) continue;

            if(punishment.getDurationTime() + punishment.getCurrentTime() < System.currentTimeMillis()){
                activePunishments.remove(punishment);

                //Вызываем ивент
                PunishmentRemoveEvent event = new PunishmentRemoveEvent(punishment, "AUTO", System.currentTimeMillis(), "");
                Bukkit.getPluginManager().callEvent(event);
            }else {
                punishment.updateGUI();
            }
        }
    }



    /**
     * Метод получения списка активных, отсортированных наказаний
     * @param types - список нужных типов наказаний
     **/
    public ArrayList<Punishment> getActivePunishments(PunishmentType... types){
        ArrayList<Punishment> temp = new ArrayList<>();

        for(Punishment punishment : activePunishments){
            if(Arrays.asList(types).contains(punishment.getType())) temp.add(punishment);
        }

        Comparator<Punishment> comparator = Punishment::compareTo;
        temp.sort(comparator);

        return temp;
    }

    /**
     * Метод получения списка активных, отсортированных наказаний
     * Без указания типа - вернёт все активные наказания
     **/
    @SuppressWarnings("unchecked")
    public ArrayList<Punishment> getActivePunishments(){
        ArrayList<Punishment> temp = (ArrayList<Punishment>) activePunishments.clone();

        Comparator<Punishment> comparator = Punishment::compareTo;
        temp.sort(comparator);

        return temp;
    }

}
