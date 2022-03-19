package me.swat1x.fbauth.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.google.common.collect.Lists;
import me.swat1x.fbauth.utils.common.LobbyData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public class MinorCommands extends BaseCommand {

    private static final List<String> lobbyServers = Lists.newArrayList("lobby", "lobby-2", "lobby-3");

    public static LobbyData findLobbyServer() {
        ServerInfo bestServer = null;
        int bestServerNumber = -1;
        int minimalOnline = -1;
        int serverNumber = 0;
        for (String s : lobbyServers) {
            serverNumber++;
            ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(s);
            if (serverInfo != null) {
                if (minimalOnline == -1) {
                    minimalOnline = serverInfo.getPlayers().size();
                    bestServer = serverInfo;
                    bestServerNumber = serverNumber;
                } else {
                    if (minimalOnline > serverInfo.getPlayers().size()) {
                        minimalOnline = serverInfo.getPlayers().size();
                        bestServer = serverInfo;
                        bestServerNumber = serverNumber;
                    }
                }
            }
        }
        if (bestServer == null) {
            return null;
        }
        return new LobbyData(bestServer, bestServerNumber);
    }

    @CommandAlias("hub|lobby|рги|дщиин|лобби|хаб")
    public void hubCommand(ProxiedPlayer player, String[] args) {
        if (args.length != 0) {
            int lobbyNumber;
            try {
                lobbyNumber = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage("§cОшибка §7| §fНекорректное число!");
                return;
            }
            if (lobbyNumber > 3 || lobbyNumber < 1) {
                player.sendMessage("§cОшибка §7| §fНомер лобби должен быть от §a1§f до §a3");
                return;
            }
            ServerInfo targetLobby = ProxyServer.getInstance().getServerInfo(lobbyServers.get(lobbyNumber - 1));
            if (targetLobby == null) {
                player.sendMessage("§cОшибка §7| §fЛобби §b№" + lobbyNumber + "§f на данный момент отключено!");
                return;
            }
            if(player.getServer().getInfo() == targetLobby){
                player.sendMessage("§cОшибка §7| §fВы уже находитесь в этом лобби!");
                return;
            }
            player.connect(targetLobby);
            player.sendMessage("§aИнфо §7| §fВы направлены в лобби §b№" + lobbyNumber);
            return;
        }
        LobbyData data = findLobbyServer();
        if (data == null) {
            player.sendMessage("§cОшибка §7| §fЛобби не найдено! Если сервер в нормальном состоянии и вы видите это сообщение обратитесь сюда §7-> §evk.com/swat1x");
            return;
        }
        if (player.getServer().getInfo() == data.getServer()) {
            player.sendMessage("§cОшибка §7| §fВы уже находитесь в лобби §b№" + data.getLobbyNumber() + " §7(Это лобби с минимальным онлайном)");
            player.sendMessage("§cОшибка §7| §fЕсли вы хотите переместиться в конкретное лобби, используйте §a/lobby <1-3>");
            return;
        } else {
            player.connect(data.getServer());
            player.sendMessage("§aИнфо §7| §fВы переместились в лобби §b№" + data.getLobbyNumber());
        }
    }

    @CommandAlias("switchserver")
    public void switchServer(ProxiedPlayer player, String[] args) {
        if (args.length == 0) {
            List<String> servers = new ArrayList<>();
            ProxyServer.getInstance().getServers().values().forEach(serverInfo -> servers.add(serverInfo.getName()));
            player.sendMessage("§fАктивные сервера: §a" + String.join("§7, §a", servers));
            return;
        }
        ServerInfo server = ProxyServer.getInstance().getServerInfo(args[0].toLowerCase());
        if (server == null) {
            player.sendMessage("§cСервер не найден");
        } else {
            player.connect(server);
            player.sendMessage("Коннект к серверу - " + server.getName());
        }
    }

}
