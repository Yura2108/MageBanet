package me.yura.magebanet.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {

    private static final Pattern TIMES = Pattern.compile("(\\d+)([a-zа-я])");

    /**
     * Возвращает вместо полученной строки типа 2h12s время в MS
     **/
    public static long parseTime(String timeStr) {
        Matcher matcher = TIMES.matcher(timeStr);

        long totalTime = 0;

        while (matcher.find()) {
            long digits = Long.parseLong(matcher.group(1));
            switch (matcher.group(2)) {
                case "y":
                case "years":
                case "г":
                case "года":
                case "л":
                case "лет":
                    digits *= 365;
                case "d":
                case "days":
                case "д":
                case "дней":
                    digits *= 24;
                case "h":
                case "hours":
                case "ч":
                case "часов":
                    digits *= 60;
                case "m":
                case "minute":
                case "м":
                case "минут":
                    digits *= 60;
                case "s":
                case "seconds":
                case "c":
                case "с":
                case "секунд":
                    digits *= 1000;

            }
            totalTime += digits;
        }

        return totalTime;
    }

    /**
     * Метод для получения нормализованного времени
     * @param input - входящее количество времени в MS
     * @return Строка, типа 1 день 2 часа. В зависимости от указанноого времени
     */
    public static String formatTimeDiff(long input){
        if(input == -1L) return "Навсегда";
        if(input < 1000) return "сейчас";

        final int[] times = new int[] {86400000, 3600000, 60000, 1000};

        final String[] names = new String[] {"день", "дня", "дней", "час", "часа", "часов",
                "минута", "минуты", "минут", "секунда", "секунды", "секунд"};

        final StringBuilder builder = new StringBuilder();
        for(int i = 0; i < times.length; i++){
            int diff = (int) (input / times[i]);

            input -= (long) diff * times[i];
            if (diff > 0) {
                int cef;

                if(diff % 10 == 1 && diff % 100 != 11) cef = 0;

                else if(2 <= diff % 10 && diff % 10 <= 4 && (diff % 100 < 10 || diff % 100 >= 20)) cef = 1;

                else cef = 2;

                builder.append(diff).append(" ").append(names[i * 3 + cef]).append(" ");
            }
        }

        return builder.substring(0, builder.length() - 1);
    }

}
