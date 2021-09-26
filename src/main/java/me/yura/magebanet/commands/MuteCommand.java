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

public class MuteCommand extends AbstractCommand{

    public MuteCommand(String command, String permission, String unPermission) {
        super(command, permission, unPermission);
    }

    @Override
    void execute(CommandSender sender, String[] args, boolean unCommand) {
        //Проверка аргументов
        if(args.length < 2){
            sender.sendMessage("§c/mute <Ник> <Причина> §7- §eЗамутить игрока навсегда");
            sender.sendMessage("§e/unmute §7<Ник> <Причина> §f- §eСнять все муты");
            return;
        }

        String name = args[0];

        //Проверка имени игрока
        if(!CommandUtils.validName(sender, name, PunishmentType.MUTE)) return;

        String senderName = sender instanceof ConsoleCommandSender ? ConfigManager.CONFIG.getMessage("consoleName") : sender.getName();

        //Получение причины
        String reason = CommandUtils.buildReason(args, 1);

        DataPlayer player = DataManager.DATA.getPlayer(name);

        //Обновляем данные о наказаниях
        player.updatePunishments();

        if(!unCommand){
            //Проверка защиты игрока
            if(!PermissionUtils.checkPunishablePerms(sender, name)) return;

            if(player.containsPunishment(PunishmentType.MUTE)){
                sender.sendMessage("§cИгрок уже имеет данное наказание!");
                return;
            }

            //Формируем наказание и добавляем в список активных
            Punishment punishment = new Punishment(PunishmentType.MUTE, System.currentTimeMillis(), -1L, name,
                    senderName, reason);
            player.addPunishment(punishment);

            //Отправка сообщения о муте (Лично игроку)
            if (Bukkit.getPlayer(name) != null) Bukkit.getPlayer(name).sendMessage(
                    StringUtils.formatMessage(name, senderName, reason, ConfigManager.CONFIG.getMessage("mute-local"))
            );

            //Отправка сообщения остальным в чат
            StringUtils.sendInfoMessage(StringUtils.formatMessage(name, senderName, reason, -1L, ConfigManager.CONFIG.getMessage("mute-message")));

            //Вызываем ивент
            PunishmentAddEvent event = new PunishmentAddEvent(punishment, System.currentTimeMillis(), player);
            Bukkit.getPluginManager().callEvent(event);
        }else{
            int cleared;

            cleared = player.clearPuns(sender.getName(), reason, PunishmentType.MUTE);

            if(cleared <= 0){
                sender.sendMessage("§eИгрок не имеет мутов");
                return;
            }

            //Сообщение о размуте
            StringUtils.sendInfoMessage(StringUtils.formatMessage(name, senderName, reason, ConfigManager.CONFIG.getMessage("unmute-message")));
        }
    }
}
