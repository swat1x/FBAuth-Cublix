package me.swat1x.fbauth.utils.common;

import lombok.Value;
import net.md_5.bungee.api.config.ServerInfo;

@Value
public class LobbyData {

    ServerInfo server;
    int lobbyNumber;

}
