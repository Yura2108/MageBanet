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

public class TempBanCommand extends AbstractCommand{

    public TempBanCommand(String command, String permission, String unPermission) {
        super(command, permission, unPermission);
    }

    @Override
    void execute(CommandSender sender, String[] args, boolean unCommand) {
        //Проверка аргументов
        if(args.length < 3){
            sender.sendMessage("§c/tempban §7<Ник> <Время> <Причина> §f- §eЗабанить игрока на указанный срок §7(Время: 2h1m = 2 часа и 1 минута)");
            return;
        }

        String name = args[0];

        //Проверка имени игрока
        if(!CommandUtils.validName(sender, name, PunishmentType.BAN)) return;

        //Проверка защиты игрока
        if(!PermissionUtils.checkPunishablePerms(sender, name)) return;

        String senderName = sender instanceof ConsoleCommandSender ? ConfigManager.CONFIG.getMessage("consoleName") : sender.getName();

        DataPlayer player = DataManager.DATA.getPlayer(name);

        //Обновляем данные о наказаниях
        player.updatePunishments();

        //Получение времени из строки аргумента
        long time = TimeUtils.parseTime(args[1]);

        //Проверяем, как прошёл парс времени
        if(!CommandUtils.validTime(sender, time, PunishmentType.BAN)) return;

        //Получение причины
        String reason = CommandUtils.buildReason(args, 2);

        //Проверяем на текущие наказания
        if(player.containsPunishment(PunishmentType.BAN)){
            sender.sendMessage("§cИгрок уже имеет данное наказание!");
            return;
        }

        //Формируем GUI сообщение
        String guiMessage = StringUtils.formatMessage(name, senderName, reason, time, ConfigManager.CONFIG.getMessage("ban-gui"));

        //Формируем наказание и добавляем в список активных
        Punishment punishment = new Punishment(PunishmentType.BAN, System.currentTimeMillis(), time, name,
                senderName, reason);
        punishment.setGuiMessage(guiMessage);
        player.addPunishment(punishment);

        //Кик игрока с сервера
        if(Bukkit.getPlayer(name) != null) Bukkit.getPlayer(name).kickPlayer(guiMessage);

        //Отправка сообщения остальным в чат
        StringUtils.sendInfoMessage(StringUtils.formatMessage(name, senderName, reason, time,  ConfigManager.CONFIG.getMessage("ban-message")));

        //Вызываем ивент
        PunishmentAddEvent event = new PunishmentAddEvent(punishment, System.currentTimeMillis(), player);
        Bukkit.getPluginManager().callEvent(event);
    }
}
