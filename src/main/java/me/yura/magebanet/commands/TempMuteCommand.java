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

public class TempMuteCommand extends AbstractCommand{

    public TempMuteCommand(String command, String permission, String unPermission) {
        super(command, permission, unPermission);
    }

    @Override
    void execute(CommandSender sender, String[] args, boolean unCommand) {

        //Проверка аргументов
        if(args.length < 3){
            sender.sendMessage("§c/tempmute <Ник> <Время> <Причина> §7- §eЗамутить игрока на указанный срок");
            return;
        }

        String name = args[0];

        //Проверка имени игрока
        if(!CommandUtils.validName(sender, name, PunishmentType.MUTE)) return;

        //Проверка защиты игрока
        if(!PermissionUtils.checkPunishablePerms(sender, name)) return;

        String senderName = sender instanceof ConsoleCommandSender ? ConfigManager.CONFIG.getMessage("consoleName") : sender.getName();

        DataPlayer player = DataManager.DATA.getPlayer(name);

        //Обновляем данные о наказаниях
        player.updatePunishments();


        long time = TimeUtils.parseTime(args[1]);

        //Проверяем, как прошёл парс времени
        if(!CommandUtils.validTime(sender, time, PunishmentType.MUTE)) return;

        //Получение причины
        String reason = CommandUtils.buildReason(args, 2);

        if (player.containsPunishment(PunishmentType.MUTE)) {
            sender.sendMessage("§cИгрок уже имеет данное наказание!");
            return;
        }

        //Формируем наказание и добавляем в список активных
        Punishment punishment = new Punishment(PunishmentType.MUTE, System.currentTimeMillis(), time, name,
                senderName, reason);
        player.addPunishment(punishment);

        //Отправка сообщения о муте (Лично игроку)
        if (Bukkit.getPlayer(name) != null) Bukkit.getPlayer(name).sendMessage(
                StringUtils.formatMessage(name, senderName, reason, ConfigManager.CONFIG.getMessage("mute-local"))
        );

        //Отправка сообщения остальным в чат (Игнорируя список миров из CubeChat)
        StringUtils.sendInfoMessage(StringUtils.formatMessage(name, senderName, reason, time, ConfigManager.CONFIG.getMessage("mute-message")));

        //Вызываем ивент
        PunishmentAddEvent event = new PunishmentAddEvent(punishment, System.currentTimeMillis(), player);
        Bukkit.getPluginManager().callEvent(event);
    }
}
