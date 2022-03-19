package me.swat1x.fbauth.management;

import com.google.gson.Gson;
import com.vk.api.sdk.objects.messages.*;
import lombok.Getter;
import me.swat1x.fbauth.management.tfa.VKManager;
import me.swat1x.fbauth.utils.CacheMap;
import me.swat1x.fbauth.utils.LoggingUtils;
import me.swat1x.fbauth.utils.NumericUtils;
import me.swat1x.fbauth.values.TFAData;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public class TFAManager {

    public static final Gson GSON = new Gson();

    private final CacheMap<String, String> authCodes;

    private final HashMap<String, TFAData> dataMap = new HashMap<>();

    private final AuthManager manager;

    public static VKManager vkManager = null;

    public TFAManager(AuthManager manager) {
        vkManager = new VKManager();
        this.manager = manager;
        loadAllUsers();
        authCodes = new CacheMap<>(5, TimeUnit.MINUTES);
    }

    public boolean hasLink(String player) {
        return getData(player) != null;
    }

    public String getLinkedPlayer(int chatId) {
        String player = null;
        for (String s : dataMap.keySet()) {
            if (dataMap.get(s).getAccount() == chatId) {
                player = s;
                break;
            }
        }
        return player;
    }

    public String getPlayerByCode(String code, boolean remove) {
        String player = null;
        for (String s : authCodes.keySet()) {
            if (authCodes.get(s).equalsIgnoreCase(code)) {
                if (remove) {
                    authCodes.remove(s);
                }
                player = s;
                break;
            }
        }
        return player;
    }

    public void sendConfirmRequest(ProxiedPlayer player) {
        Keyboard keyboard = new Keyboard();
        List<List<KeyboardButton>> keys = new ArrayList<>();
        List<KeyboardButton> line = new ArrayList<>();


        line.add(new KeyboardButton()
                .setAction(
                        new KeyboardButtonAction()
                                .setType(TemplateActionTypeNames.CALLBACK)
                                .setLabel("Подтвердить")
                                .setPayload(GSON.toJson("confirm"))
                )
                .setColor(KeyboardButtonColor.POSITIVE)
        );

        line.add(new KeyboardButton()
                .setAction(
                        new KeyboardButtonAction()
                                .setType(TemplateActionTypeNames.CALLBACK)
                                .setLabel("Отклонить")
                                .setPayload(GSON.toJson("cancel"))
                )
                .setColor(KeyboardButtonColor.NEGATIVE)
        );

        keys.add(line);
        keyboard.setButtons(keys);
        keyboard.setInline(true);

        vkManager.sendMessage(
                (int) getData(player.getName()).getAccount(),
                "\uD83E\uDD14 Зафиксирован вход на аккаунт " + player.getName() + " c IP: " + player.getAddress().getAddress().getHostAddress(),
                keyboard);
    }

    public void handleConnect(long accountId, String player) {
        linkAccount(player, accountId);
    }

    public TFAData getData(String player) {
        return dataMap.getOrDefault(player, null);
    }

    private int formatBoolean(boolean b) {
        return b ? 1 : 0;
    }

    private void linkAccount(String player, long id) {
        manager.getDatabase().sync().update("INSERT INTO `2fa` (`username`, `profile_id`, `date`, `ban`) VALUES ('" + player + "', '" + id + "', '" + System.currentTimeMillis() + "', '" + formatBoolean(false) + "')");
        loadUser(player);
        LoggingUtils.info("Пользователь §b" + player + "§f привязал аккаунт к §9VK");
    }

    public String getCachedCode(String player) {
        return authCodes.getOrDefault(player, null);
    }

    public String requestLinkCode(String player) {
        if (authCodes.containsKey(player)) {
            return null;
        }
        String code = NumericUtils.randomInt(1000, 9999) + "";
        while (authCodes.containsValue(code)) {
            code = NumericUtils.randomInt(1000, 9999) + "";
        }
        authCodes.put(player, code);
        LoggingUtils.info("Пользователь §b" + player + "§f запросил код на привязку аккаунта §7(" + code + ")");
        return code;
    }

    private void loadAllUsers() {
        dataMap.clear();
        manager.getDatabase().sync().query("SELECT * FROM `2fa`", rs -> {
            while (rs.next()) {
                String user = rs.getString("username");
                TFAData data = new TFAData(user, rs.getLong("profile_id"), rs.getLong("date"), rs.getBoolean("ban"));
                dataMap.put(user, data);
            }
            return null;
        });
    }

    public TFAData getOrLoadUser(String user) {
        if (dataMap.containsKey(user)) {
            return dataMap.get(user);
        } else {
            return loadUser(user);
        }
    }

    private TFAData loadUser(String user) {
        dataMap.remove(user);
        TFAData data = manager.getDatabase().sync().query("SELECT * FROM `2fa` WHERE `username`='" + user + "'", rs -> {
            if (rs.next()) {
                return new TFAData(user, rs.getLong("profile_id"), rs.getLong("date"), rs.getBoolean("ban"));
            }
            return null;
        });
        if (data != null) {
            dataMap.put(user, data);
        }
        return data;
    }


}
