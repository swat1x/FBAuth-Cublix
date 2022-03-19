package me.swat1x.fbauth.management;

import lombok.Getter;
import me.swat1x.fbauth.AuthPlugin;
import me.swat1x.fbauth.commands.MinorCommands;
import me.swat1x.fbauth.management.config.ConfigManager;
import me.swat1x.fbauth.utils.PlayerUtils;
import me.swat1x.fbauth.utils.common.LobbyData;
import me.swat1x.fbauth.values.LoginSession;
import me.swat1x.fbauth.values.LoginStage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import ru.cublix.bungee.BungeeCore;
import ru.cublix.connection.protocol.packets.Packet2PlayerConnect;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Getter
public class LoginManager {

    private final HashMap<String, Long> joinMap = new HashMap<>();

    private final HashMap<String, LoginStage> stageMap = new HashMap<>();

    public HashMap<String, LoginStage> getStageMap() {
        return stageMap;
    }

    private final AuthManager authManager;

    public LoginManager(AuthManager authManager) {
        this.authManager = authManager;
    }

    public void handleJoin(ProxiedPlayer player) {
        LoginSession ls = authManager.getOrLoadSession(player.getName());
        joinMap.remove(player.getName());
        joinMap.put(player.getName(), System.currentTimeMillis());
        if (ls != null) {
            if (authManager.getOrLoadUserData(player.getName()).getIp().equals(player.getAddress().getAddress().getHostAddress())) {
                if (ls.isValid()) {
                    bypassPlayer(player, false);
                    PlayerUtils.sendMessage(player, ConfigManager.lang().HAS_SESSION);
                    return;
                }
            }
            PlayerUtils.sendMessage(player, ConfigManager.lang().LOGIN);
            setStage(player.getName(), LoginStage.LOGIN);
        } else {
            PlayerUtils.sendMessage(player, ConfigManager.lang().REGISTER);
            setStage(player.getName(), LoginStage.REGISTER);
        }
    }

    public void handleLeave(ProxiedPlayer player) {
        joinMap.remove(player.getName());
        stageMap.remove(player.getName());
    }

    public LoginStage getStage(String player) {
        return stageMap.getOrDefault(player, null);
    }

    public boolean isStage(String player, LoginStage stage) {
        LoginStage playerStage = getStage(player);
        if (playerStage != null) {
            return playerStage == stage;
        }
        return false;
    }

    public void setStage(String player, LoginStage stage) {
        stageMap.remove(player);
        if (stage != null) {
            stageMap.put(player, stage);
        }
    }

    private final Title authTitle = ProxyServer.getInstance().createTitle().title(new ComponentBuilder("§aУспешно!").create()).subTitle(new ComponentBuilder("§fВы авторизировались. Удачной игры!").create()).fadeIn(0).stay(40);
    private final Title linkTitle = ProxyServer.getInstance().createTitle().title(new ComponentBuilder("§eВнимание!").create()).subTitle(new ComponentBuilder("§fОбезопасьте свой аккаунт!").create()).fadeIn(0).stay(60);

    public void bypassPlayer(ProxiedPlayer player, boolean updateSession) {
        joinMap.remove(player.getName());
        stageMap.remove(player.getName());
        PlayerUtils.sendMessage(ChatMessageType.ACTION_BAR, player, ConfigManager.lang().AB_AUTH);
        LobbyData lobby = MinorCommands.findLobbyServer();
        if(lobby == null){
            player.sendMessage("§cСейчас небольшие проблемы с лобби и на данный момент нет доступных");
        }
        else{
            ProxyServer.getInstance().getScheduler().schedule(AuthPlugin.getPlugin(), () ->
                    player.connect(MinorCommands.findLobbyServer().getServer()), 100, TimeUnit.MILLISECONDS);
        }
        authManager.handleLogin(player, !updateSession);
        if (updateSession) {
            authManager.updateSession(player.getName());
        }

        if(authManager.getTfaManager().hasLink(player.getName())){
            player.sendTitle(authTitle);
        } else {
            player.sendTitle(linkTitle);
            player.sendMessage(authManager.getLinkMessage());

        }

        BungeeCore.getClient().sendPacket(new Packet2PlayerConnect(player.getName(),player.getAddress().getHostName()));
    }

    public void login(ProxiedPlayer player) {
        if (authManager.tfaManager().hasLink(player.getName())) {
            setStage(player.getName(), LoginStage.WAIT_2FA);
            authManager.tfaManager().sendConfirmRequest(player);
        } else {
            bypassPlayer(player, true);
        }
    }

    public void handle2FAConfirm(int id, ProxiedPlayer player) {
        PlayerUtils.sendMessage(player, ConfigManager.lang().BOT_GAME_LOGIN_CONFIRM);
        bypassPlayer(player, true);
        TFAManager.vkManager.sendMessage(id, "\uD83D\uDED2 Вы подтвердили вход в аккаунт!");
    }

    public static class NotifyTask implements Runnable {

        private final AuthManager manager;

        public NotifyTask(Plugin plugin, AuthManager manager) {
            this.manager = manager;
            ProxyServer.getInstance().getScheduler().schedule(plugin, this, 0, 1, TimeUnit.SECONDS);
        }

        private int getTimeToKick(String player) {
            long joinTime = manager.loginManager().joinMap.getOrDefault(player, -1L);
            long time = joinTime + ConfigManager.settings().AUTH_TIME - System.currentTimeMillis();
            return (int) (time / 1000);
        }

        private int i = 1;

        @Override
        public void run() {
            LoginManager loginManager = manager.loginManager();
            for (String s : loginManager.getStageMap().keySet()) {
                int timeLeft = getTimeToKick(s);
                if (timeLeft <= 0) {
                    loginManager.stageMap.remove(s);
                    loginManager.joinMap.remove(s);
                    PlayerUtils.kick(s, ConfigManager.lang().NO_TIME_TO_AUTH);
                    return;
                }
                boolean send = false;
                if (i >= 3) {
                    i = 1;
                    send = true;
                }
                PlayerUtils.sendMessage(ChatMessageType.ACTION_BAR, s, ConfigManager.lang().AB_TIME.replace("%time", timeLeft + ""));
                Title loginTitle = ProxyServer.getInstance().createTitle().title(new ComponentBuilder("").create()).subTitle(new ComponentBuilder("§fАвторизуйтесь используя команду §e/l <пароль>").create()).fadeIn(0).stay(20 * 5).fadeOut(0);
                Title authTitle = ProxyServer.getInstance().createTitle().title(new ComponentBuilder("").create()).subTitle(new ComponentBuilder("§fЗарегистрируйтесь используя команду §b/reg <пароль>").create()).fadeIn(0).stay(20 * 5).fadeOut(0);

                Title tfaTitle = ProxyServer.getInstance().createTitle().title(
                        new ComponentBuilder("§c2FA").create()
                ).subTitle(new ComponentBuilder("§fПодтвердите вход в §aсоциальной сети").create()).fadeIn(0).stay(20 * 5).fadeOut(0);
                if (send) {
                    LoginStage stage = loginManager.getStageMap().get(s);
                    if (stage == LoginStage.LOGIN) {
                        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(s);
                        if (player != null) {
                            loginTitle.send(player);
                        }
                        PlayerUtils.sendMessage(s, ConfigManager.lang().LOGIN);
                    } else {
                        if (stage == LoginStage.REGISTER) {
                            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(s);
                            if (player != null) {
                                authTitle.send(player);
                            }
                            PlayerUtils.sendMessage(s, ConfigManager.lang().REGISTER);
                        } else {
                            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(s);
                            if (player != null) {
                                tfaTitle.send(player);
                            }
                            PlayerUtils.sendMessage(s, "Подтвердите вход через социальную сеть!");
                        }
                    }
                }
                i++;
            }
        }
    }


}
