package me.yura.magebanet.commands;

import me.yura.magebanet.config.ConfigManager;
import me.yura.magebanet.data.DataManager;
import me.yura.magebanet.datatypes.DataPlayer;
import me.yura.magebanet.datatypes.Punishment;
import me.yura.magebanet.datatypes.PunishmentType;
import me.yura.magebanet.utils.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class PunishmentsListCommand extends AbstractCommand{

    public PunishmentsListCommand(String command, String permission, String unPermission) {
        super(command, permission, unPermission);
    }

    @Override
    void execute(CommandSender sender, String[] args, boolean unCommand) {
        if(args.length == 0) {
            sender.sendMessage("§c/puns §7<Тип> <Страница> §f- §eОтображает все активные наказания");
            sender.sendMessage("§eДля отображения всех типов используйте: §call");
            return;
        }

        if(args.length == 1) {
            PunishmentType type = PunishmentType.getPunishmentType(args[0]);

            if(!args[0].equalsIgnoreCase("all") && type == null) {
                sender.sendMessage("§eОшибка: §cАргументы указаны неправильно!");
                return;
            }

            ArrayList<Punishment> punishments = getPunishments(type);

            if(punishments.isEmpty()) {
                sender.sendMessage("§eОшибка: §cНи одного активного наказания!");
                return;
            }

            HashMap<Integer, List<Punishment>> sortPages = StringUtils.sortPages(punishments, 10);

            StringBuilder builder = new StringBuilder("§eСписок наказаний: \n");

            int index = 1;

            for(Punishment punishment : sortPages.get(1)) {
                builder.append(StringUtils.formatPunishment(punishment, index, ConfigManager.CONFIG.getMessage("punishemnt-format")))
                        .append("\n");

                index++;
            }
            
            sender.sendMessage(builder.toString());

        }else if(args.length == 2) {
            PunishmentType type = PunishmentType.getPunishmentType(args[0]);

            if(!args[0].equalsIgnoreCase("all") && type == null) {
                sender.sendMessage("§eОшибка: §cАргументы указаны неправильно!");
                return;
            }

            int page = StringUtils.parseInt(args[1]);

            ArrayList<Punishment> punishments = getPunishments(type);

            if(punishments.isEmpty()) {
                sender.sendMessage("§eОшибка: §cНи одного активного наказания!");
                return;
            }

            HashMap<Integer, List<Punishment>> sortPages = StringUtils.sortPages(punishments, 10);

            if(!sortPages.containsKey(page)) {
                sender.sendMessage("§eОшибка: §cУказанная страница не найдена!");
                return;
            }

            StringBuilder builder = new StringBuilder("§eСписок наказаний: \n");

            int index = 1;

            for(Punishment punishment : sortPages.get(page)) {
                builder.append(StringUtils.formatPunishment(punishment, index, ConfigManager.CONFIG.getMessage("punishemnt-format")))
                        .append("\n");

                index++;
            }

            sender.sendMessage(builder.toString());
        }else {
            sender.sendMessage("§eОшибка: §cАргументы указаны неправильно!");
            return;
        }


    }

    public ArrayList<Punishment> getPunishments(PunishmentType filter) {
        ArrayList<Punishment> temp = new ArrayList<>();

        for(DataPlayer player : DataManager.DATA.getDataPlayers()) {
            if(filter == null) temp.addAll(player.getActivePunishments());
            else temp.addAll(player.getActivePunishments(filter));
        }

        //Sorting
        Comparator<Punishment> comparator = Punishment::compareToDate;
        temp.sort(comparator);

        return temp;
    }
}
