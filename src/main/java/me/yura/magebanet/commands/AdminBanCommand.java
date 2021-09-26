package me.yura.magebanet.commands;

import me.yura.magebanet.config.ConfigManager;
import me.yura.magebanet.data.DataManager;
import me.yura.magebanet.datatypes.DataPlayer;
import me.yura.magebanet.datatypes.Punishment;
import me.yura.magebanet.datatypes.PunishmentType;
import me.yura.magebanet.event.PunishmentAddEvent;
import me.yura.magebanet.utils.CommandUtils;
import me.yura.magebanet.utils.PermissionUtils;
import me.yura.magebanet.utils.StringUtils;
import me.yura.magebanet.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class AdminBanCommand extends AbstractCommand{

    public AdminBanCommand(String command, String permission, String unPermission) {
        super(command, permission, unPermission);
    }

    @Override
    void execute(CommandSender sender, String[] args, boolean unCommand) {
        //Проверка аргументов
        if(unCommand && args.length < 2){
            sender.sendMessage("§c/aunban §7<Ник> <Причина> §f- §eСнять все приоритетные баны");
            return;
        }

        if(!unCommand && args.length < 3){
            sender.sendMessage("§c/aban §7<Ник> <Время> <Причина> §f- §eПриоритетно забанить игрока на указанный срок");
            return;
        }

        String name = args[0];

        //Проверка имени игрока
        if(!CommandUtils.validName(sender, name, PunishmentType.ADMIN_BAN)) return;

        String senderName = sender instanceof ConsoleCommandSender ? ConfigManager.CONFIG.getMessage("consoleName") : sender.getName();

        DataPlayer player = DataManager.DATA.getPlayer(name);

        //Обновляем данные о наказаниях
        player.updatePunishments();

        if(!unCommand){
            //Проверка защиты игрока
            if(!PermissionUtils.checkPunishablePerms(sender, name)) return;

            //Получение времени из строки аргумента
            long time = TimeUtils.parseTime(args[1]);

            //Проверяем, как прошёл парс времени
            if(!CommandUtils.validTime(sender, time, PunishmentType.ADMIN_BAN)) return;

            //Получение причины
            String reason = CommandUtils.buildReason(args, 2);

            //Проверяем на текущие наказания
            if(player.containsPunishment(PunishmentType.ADMIN_BAN)){
                sender.sendMessage("§cИгрок уже имеет данное наказание!");
                return;
            }

            //Формируем GUI сообщение
            String guiMessage = StringUtils.formatMessage(name, senderName, reason, time, ConfigManager.CONFIG.getMessage("admin-ban-gui"));

            //Формируем наказание и добавляем в список активных
            Punishment punishment = new Punishment(PunishmentType.ADMIN_BAN, System.currentTimeMillis(), time, name,
                    senderName, reason);
            punishment.setGuiMessage(guiMessage);
            player.addPunishment(punishment);

            //Кик игрока с сервера
            if(Bukkit.getPlayer(name) != null) Bukkit.getPlayer(name).kickPlayer(guiMessage);

            //Отправка сообщения остальным в чат
            StringUtils.sendInfoMessage(StringUtils.formatMessage(name, senderName, reason, time,  ConfigManager.CONFIG.getMessage("admin-ban-message")));

            //Вызываем ивент
            PunishmentAddEvent event = new PunishmentAddEvent(punishment, System.currentTimeMillis(), player);
            Bukkit.getPluginManager().callEvent(event);
        }else{
            //Получение причины
            String reason = CommandUtils.buildReason(args, 1);

            int cleared;

            cleared = player.clearPuns(sender.getName(), reason, PunishmentType.ADMIN_BAN);

            if(cleared <= 0){
                sender.sendMessage("§eИгрок не имеет приоритетных банов");
                return;
            }

            //Сообщение о разбане
            StringUtils.sendInfoMessage(StringUtils.formatMessage(name, senderName, reason, ConfigManager.CONFIG.getMessage("admin-unban-message")));
        }
    }
}
