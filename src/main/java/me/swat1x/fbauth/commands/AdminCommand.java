package me.swat1x.fbauth.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.swat1x.fbauth.AuthPlugin;
import me.swat1x.fbauth.management.AuthManager;
import me.swat1x.fbauth.management.config.ConfigManager;
import me.swat1x.fbauth.utils.PlayerUtils;
import me.swat1x.fbauth.utils.TextUtils;
import me.swat1x.fbauth.values.PasswordData;
import me.swat1x.fbauth.values.TFAData;
import me.swat1x.fbauth.values.UserData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("auth")
public class AdminCommand extends BaseCommand {

    @Default
    public void def(CommandSender sender) {
        if (sender instanceof ProxiedPlayer) {
            if (!ConfigManager.settings().ADMINS.contains(sender.getName())) {
                sender.sendMessage("§fПлагин §bFBAuth§f. Автор §eswat1x§7 vk.com/swat1x");
                return;
            }
        }
        sendInfo(sender);
    }

    @Subcommand("reload")
    private void reload(CommandSender sender) {
        AuthPlugin.reloadConfig();
        sender.sendMessage("Конфиг успешно перезагружен!");
    }

    @Subcommand("setpw")
    @CommandCompletion("@players Новый_Пароль")
    private void setPw(CommandSender sender, String[] args) {
        AuthManager manager = AuthPlugin.getAuthManager();
        if (args.length < 2) {
            sender.sendMessage("§fСменить пароль игроку §7- §e/auth setpw <игрок> <новый пароль>");
            return;
        }
        String player = args[0];
        PasswordData pd = manager.getOrLoadPassword(player);
        if (pd == null) {
            sender.sendMessage("§cТакой игрок не зарегистрирован");
            return;
        }
        String newPassword = args[1];
        manager.changePassword(player, newPassword);
        sender.sendMessage("Пароль игрока §e" + player + "§f изменён на §b" + newPassword);
    }

    @Subcommand("info")
    @CommandCompletion("@players")
    private void info(CommandSender sender, String[] args) {
        AuthManager manager = AuthPlugin.getAuthManager();
        if (args.length < 1) {
            sender.sendMessage("§fИнформация об игроке §7- §e/auth info <игрок>");
            return;
        }
        String player = args[0];
        PasswordData pd = manager.getOrLoadPassword(player);
        if (pd == null) {
            sender.sendMessage("§cТакой игрок не зарегистрирован");
            return;
        }
        UserData userData = manager.getOrLoadUserData(player);
        boolean hasSession = manager.hasSession(player);
        sender.sendMessage("Информация о пользователе §b" + player);
        sender.sendMessage("");
        sender.sendMessage("Последний айпи §e" + userData.getIp());
        sender.sendMessage("Айпи регистрации §e" + userData.getRegIp());
        sender.sendMessage("Последний вход в аккаунт §e" + TextUtils.getTimeLabel(userData.getLastJoin(), System.currentTimeMillis()) + " назад");
        sender.sendMessage("Сессия: "+(hasSession ? "§eещё "+TextUtils.getTimeLabel(System.currentTimeMillis(), manager.getOrLoadSession(player).getEnd()) : "§cнету"));
        sender.sendMessage("Зарегистрировался §e" + TextUtils.getTimeLabel(userData.getRegData(), System.currentTimeMillis()) + " назад");
        boolean hasLink = manager.tfaManager().hasLink(player);
        sender.sendMessage("Привязка в VK: " + (hasLink ? "§aесть" : "§cнету"));
        if (hasLink) {
            TFAData data = manager.tfaManager().getData(player);
            sender.sendMessage("Привязан: §e" + TextUtils.getTimeLabel(data.getDate(), System.currentTimeMillis())+" назад");
            TextComponent line = new TextComponent("Аккаунт: §7*наведите*");
            String link = "https://vk.com/id" + data.getAccount();
            line.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§fОткрыть ссылку §e" + link).create()));
            line.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
            sender.sendMessage(line);
        }
    }

    private void sendInfo(CommandSender sender) {
        sender.sendMessage("§7-----(§a§lF§e§lB§f§lAuth§7)-----");
        sender.sendMessage(" ");

        sender.sendMessage("§fСменить пароль игроку §7- §e/auth setpw <игрок> <новый пароль>");
        sender.sendMessage("§fИнформация об игроке §7- §e/auth info <игрок>");
        sender.sendMessage("§fПерезагрузка конфигов §7- §e/auth reload §7(Лучше будет перезапуск BungeeCord)");

        sender.sendMessage(" ");
        sender.sendMessage("§7-----(§a§lF§e§lB§f§lAuth§7)-----");
    }

}
