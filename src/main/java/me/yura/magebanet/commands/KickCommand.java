package me.yura.magebanet.commands;

import me.yura.magebanet.config.ConfigManager;
import me.yura.magebanet.data.DataManager;
import me.yura.magebanet.datatypes.Punishment;
import me.yura.magebanet.datatypes.PunishmentType;
import me.yura.magebanet.event.PunishmentAddEvent;
import me.yura.magebanet.utils.CommandUtils;
import me.yura.magebanet.utils.PermissionUtils;
import me.yura.magebanet.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class KickCommand extends AbstractCommand{

    public KickCommand(String command, String permission, String unPermission) {
        super(command, permission, unPermission);
    }

    @Override
    void execute(CommandSender sender, String[] args, boolean unCommand) {
        //Выполнение команды

        //Проверка аргументов
        if(args.length < 2){
            sender.sendMessage("§c/kick <Ник> <Причина> §7- §eКикнуть игрока с сервера");
            return;
        }

        String name = args[0];

        //Проверка имени игрока
        if(!CommandUtils.validName(sender, name, PunishmentType.KICK)) return;

        Player punishable = Bukkit.getPlayer(name);

        String senderName = sender instanceof ConsoleCommandSender ? ConfigManager.CONFIG.getMessage("consoleName") : sender.getName();

        //Проверка защиты игрока
        if(!PermissionUtils.checkPunishablePerms(sender, punishable)) return;

        //Получение причины
        String reason = CommandUtils.buildReason(args, 1);

        //Кик игрока
        punishable.kickPlayer(StringUtils.formatMessage(name, senderName, reason, ConfigManager.CONFIG.getMessage("kick-gui")));

        //Отправка уведомления
        StringUtils.sendInfoMessage(StringUtils.formatMessage(name, senderName, reason, ConfigManager.CONFIG.getMessage("kick-message")));

        //Вызываем ивент
        Punishment punishment = new Punishment(PunishmentType.KICK, System.currentTimeMillis(), 0L, name, sender.getName(), reason);
        PunishmentAddEvent event = new PunishmentAddEvent(punishment, System.currentTimeMillis(), DataManager.DATA.getPlayer(name));
        Bukkit.getPluginManager().callEvent(event);
    }
}
