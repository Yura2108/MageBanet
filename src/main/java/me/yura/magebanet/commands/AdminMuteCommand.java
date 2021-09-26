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

public class AdminMuteCommand extends AbstractCommand{

    public AdminMuteCommand(String command, String permission, String unPermission) {
        super(command, permission, unPermission);
    }

    @Override
    void execute(CommandSender sender, String[] args, boolean unCommand) {
        //Проверка аргументов
        if(unCommand && args.length < 2){
            sender.sendMessage("§c/aunmute §7<Ник> <Причина> §f- §eСнять все приоритетные муты");
            return;
        }

        if(!unCommand && args.length < 3){
            sender.sendMessage("§c/amute §7<Ник> <Время> <Причина> §f- §eПриоритетно замутить игрока на указанный срок");
            return;
        }

        String name = args[0];

        //Проверка имени игрока
        if(!CommandUtils.validName(sender, name, PunishmentType.ADMIN_MUTE)) return;

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
            if(!CommandUtils.validTime(sender, time, PunishmentType.ADMIN_MUTE)) return;

            //Получение причины
            String reason = CommandUtils.buildReason(args, 2);

            //Проверяем на текущие наказания
            if(player.containsPunishment(PunishmentType.ADMIN_MUTE)){
                sender.sendMessage("§cИгрок уже имеет данное наказание!");
                return;
            }

            //Формируем наказание и добавляем в список активных
            Punishment punishment = new Punishment(PunishmentType.ADMIN_MUTE, System.currentTimeMillis(), time, name,
                    senderName, reason);
            player.addPunishment(punishment);

            //Отправка сообщения о муте (Лично игроку)
            if (Bukkit.getPlayer(name) != null) Bukkit.getPlayer(name).sendMessage(
                    StringUtils.formatMessage(name, senderName, reason, ConfigManager.CONFIG.getMessage("admin-mute-local"))
            );

            //Отправка сообщения остальным в чат
            StringUtils.sendInfoMessage(StringUtils.formatMessage(name, senderName, reason, time,  ConfigManager.CONFIG.getMessage("admin-mute-message")));

            //Вызываем ивент
            PunishmentAddEvent event = new PunishmentAddEvent(punishment, System.currentTimeMillis(), player);
            Bukkit.getPluginManager().callEvent(event);
        }else{
            //Получение причины
            String reason = CommandUtils.buildReason(args, 1);

            int cleared;

            cleared = player.clearPuns(sender.getName(), reason, PunishmentType.ADMIN_MUTE);

            if(cleared <= 0){
                sender.sendMessage("§eИгрок не имеет приоритетных мутов");
                return;
            }

            //Сообщение о разбане
            StringUtils.sendInfoMessage(StringUtils.formatMessage(name, senderName, reason, ConfigManager.CONFIG.getMessage("admin-unmute-message")));
        }

    }
}
