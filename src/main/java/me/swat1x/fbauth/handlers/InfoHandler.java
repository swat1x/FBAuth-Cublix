package me.swat1x.fbauth.handlers;

import me.swat1x.fbauth.utils.PlayerUtils;
import me.swat1x.fbauth.utils.TextUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class InfoHandler implements Listener {

    @EventHandler
    public void protocolChecker(PostLoginEvent event){
        ProxiedPlayer player = event.getPlayer();
        if(!PlayerUtils.isNewVersion(player)) {
            player.sendMessage("");
            player.sendMessage(TextUtils.getCenter("§cВнимание, Ваша версия устарела!"));
            player.sendMessage("");
            player.sendMessage(TextUtils.getCenter("Основная версия сервера это §e1.16.5"));
            player.sendMessage(TextUtils.getCenter("Играя на версиях ниже этой, Вы берёте на себя"));
            player.sendMessage(TextUtils.getCenter("все вытекающие баги/лаги в процессе игры"));
            player.sendMessage("");
        }
    }

}
