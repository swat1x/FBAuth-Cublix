package me.swat1x.fbauth.handlers;

import com.google.common.collect.Lists;
import me.swat1x.fbauth.AuthPlugin;
import me.swat1x.fbauth.commands.MinorCommands;
import me.swat1x.fbauth.management.AuthManager;
import me.swat1x.fbauth.utils.common.LobbyData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class GeneralHandler implements Listener {

    @EventHandler
    public void join(PreLoginEvent e) {
        if(!e.getConnection().getName().replaceAll("[A-Za-z0-9_]", "").equalsIgnoreCase("")){
            e.setCancelReason(new ComponentBuilder("Используйте ник в параметрах §e[A-Za-z0-9_]").create());
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void join(PostLoginEvent e) {
        AuthManager manager = AuthPlugin.getAuthManager();

        if (manager.isCorrectName(e.getPlayer())) {
            manager.loginManager().handleJoin(e.getPlayer());
        }
    }

    @EventHandler
    public void quit(PlayerDisconnectEvent e) {
        AuthPlugin.getAuthManager().loginManager().handleLeave(e.getPlayer());
    }

    private static List<String> allowCommands = Lists.newArrayList("/reg", "/register", "/рег", "/регистрация", "/l", "/login", "/л", "/логин");

    @EventHandler
    public void join(ChatEvent e) {
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        if (AuthPlugin.getAuthManager().loginManager().getStage(p.getName()) != null) {
            if (e.isCommand()) {
                String command = e.getMessage().split(" ")[0];
                if (!allowCommands.contains(command.toLowerCase())) {
                    e.setCancelled(true);
                }
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void kick(ServerKickEvent e) {
        if(e.getKickedFrom().getName().contains("lobby") || e.getKickedFrom().getName().contains("auth")){
            return;
        }
        ProxiedPlayer p = e.getPlayer();
        LobbyData lobby = MinorCommands.findLobbyServer();
        e.setCancelled(true);
        e.setCancelServer(lobby.getServer());
        p.sendMessage("§eВнимание §7| §fВы были отключены от сервера и перенаправлены в лобби §b№" + lobby.getLobbyNumber());
        p.sendMessage("§fПричина отключения: " + e.getKickReason());
    }

}
