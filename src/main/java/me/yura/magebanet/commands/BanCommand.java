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
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class BanCommand extends AbstractCommand{

    public BanCommand(String command, String permission, String unPermission) {
        super(command, permission, unPermission);
    }

    @Override
    void execute(CommandSender sender, String[] args, boolean unCommand) {
        //Проверка аргументов
        if(args.length < 2){
            sender.sendMessage("§c/ban <Ник> <Причина> §7- §eЗабанить игрока навсегда");
            sender.sendMessage("§e/unban §7<Ник> <Причина> §f- §eСнять все баны");
            return;
        }

        String name = args[0];

        //Проверка имени игрока
        if(!CommandUtils.validName(sender, name, PunishmentType.BAN)) return;

        String senderName = sender instanceof ConsoleCommandSender ? ConfigManager.CONFIG.getMessage("consoleName") : sender.getName();

        //Получение причины
        String reason = CommandUtils.buildReason(args, 1);

        DataPlayer player = DataManager.DATA.getPlayer(name);

        //Обновляем данные о наказаниях
        player.updatePunishments();

        if(!unCommand){
            //Проверка защиты игрока
            if(!PermissionUtils.checkPunishablePerms(sender, name)) return;

            if(player.containsPunishment(PunishmentType.BAN)){
                sender.sendMessage("§cИгрок уже имеет данное наказание!");
                return;
            }

            //Формируем GUI сообщение
            String guiMessage = StringUtils.formatMessage(name, senderName, reason, -1L, ConfigManager.CONFIG.getMessage("ban-gui"));

            //Формируем наказание и добавляем в список активных
            Punishment punishment = new Punishment(PunishmentType.BAN, System.currentTimeMillis(), -1L, name,
                    senderName, reason);
            punishment.setGuiMessage(guiMessage);
            player.addPunishment(punishment);

            //Кик игрока с сервера
            if(Bukkit.getPlayer(name) != null) Bukkit.getPlayer(name).kickPlayer(guiMessage);

            //Отправка сообщения остальным в чат
            StringUtils.sendInfoMessage(StringUtils.formatMessage(name, senderName, reason, -1L, ConfigManager.CONFIG.getMessage("ban-message")));

            //Вызываем ивент
            PunishmentAddEvent event = new PunishmentAddEvent(punishment, System.currentTimeMillis(), player);
            Bukkit.getPluginManager().callEvent(event);
        }else{
            int cleared;

            cleared = player.clearPuns(sender.getName(), reason, PunishmentType.BAN);

            if(cleared <= 0){
                sender.sendMessage("§eИгрок не имеет банов");
                return;
            }

            //Сообщение о разбане
            StringUtils.sendInfoMessage(StringUtils.formatMessage(name, senderName, reason, ConfigManager.CONFIG.getMessage("unban-message")));
        }
    }
}
