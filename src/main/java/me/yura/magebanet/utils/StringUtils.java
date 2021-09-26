package me.yura.magebanet.utils;

import me.yura.magebanet.datatypes.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.text.SimpleDateFormat;
import java.util.*;

import static me.yura.magebanet.MageBanet.getInstance;

public class StringUtils {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");

    public static void sendInfoMessage(String msg){
        Bukkit.broadcastMessage(msg);

        //Сообщение в консоль
        //getInstance().getLogger().info(ChatColor.stripColor(msg));
    }

    /**
     * Метод для замены собственных переменных в строке (Без времени)
     * @param name - ник игрока (Наказуемого)
     * @param sender - кто наказывает (Администратор)
     * @param reason - причина наказания
     * @param toFormat - исходное сообщение
     * @return строка, с заменёнными переменными
     */
    public static String formatMessage(String name, String sender, String reason, String toFormat){
        return toFormat.replaceAll("\\{player}", name).replaceAll("\\{admin}", sender).replaceAll("\\{reason}", reason);
    }

    /**
     * Метод для замены собственных переменных в строке (+время)
     * @param name - ник игрока (Наказуемого)
     * @param sender - кто наказывает (Администратор)
     * @param reason - причина наказания
     * @param time - время бана в MS
     * @param toFormat - исходное сообщение
     * @return строка, с заменёнными переменными
     */
    public static String formatMessage(String name, String sender, String reason, long time, String toFormat){
        return toFormat.replaceAll("\\{player}", name).replaceAll("\\{admin}", sender).replaceAll("\\{reason}", reason).replaceAll("\\{time}", TimeUtils.formatTimeDiff(time));
    }



    public static <T> HashMap<Integer, List<T>> sortPages(ArrayList<T> input, int maxPageObj){
        HashMap<Integer, List<T>> lists = new HashMap<>();

        int currentPage = 1;
        int localCount = 0;
        for(T object : input){
            if(lists.get(currentPage) != null) lists.get(currentPage).add(object);
            else lists.put(currentPage, new ArrayList<>(Collections.singletonList(object)));

            localCount++;
            if(localCount >= maxPageObj){
                currentPage++;
                localCount = 0;
            }
        }

        return lists;
    }

    public static String formatPunishment(Punishment punishment, int index, String format){
        return format.replaceAll("\\{index}", Integer.toString(index))
                .replaceAll("\\{player}", punishment.getPunishable())
                .replaceAll("\\{admin}", punishment.getPunisher())
                .replaceAll("\\{type}", punishment.getType().getLocalization())
                .replaceAll("\\{reason}", punishment.getReason())
                .replaceAll("\\{time}", formatDate(punishment.getCurrentTime()));
    }

    public static String formatDate(long date){
        return dateFormat.format(new Date(date)).replaceAll("/", ".");
    }

    public static int parseInt(String input) {
        try{
            return Integer.parseInt(input);
        }catch (NumberFormatException exception) {
            return 1;
        }
    }

}
