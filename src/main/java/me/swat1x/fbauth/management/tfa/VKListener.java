package me.swat1x.fbauth.management.tfa;

import com.ubivashka.vk.bungee.events.VKCallbackButtonPressEvent;
import com.ubivashka.vk.bungee.events.VKMessageEvent;
import com.vk.api.sdk.objects.messages.*;
import me.swat1x.fbauth.AuthPlugin;
import me.swat1x.fbauth.management.TFAManager;
import me.swat1x.fbauth.utils.TextUtils;
import me.swat1x.fbauth.values.LoginStage;
import me.swat1x.fbauth.values.TFAData;
import me.swat1x.fbauth.values.UserData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

import static me.swat1x.fbauth.management.TFAManager.GSON;
import static me.swat1x.fbauth.management.TFAManager.vkManager;

public class VKListener implements Listener {

    private final TFAManager manager;
    private final Keyboard infoKeyboard;

    private static final String helpMessage = "ℹ Сводка по привязке аккаунта\n" +
            " Всё взаимодействие происходит интерактивно с помощью кнопок у сообщений \n \n" +
            "\uD83E\uDDF2 Чтобы привязать аккаунт зайдите на сервер и напишите команду /link. Вам сгенерирует код, который вы должны прислать сюда\n" +
            "\uD83D\uDED2 После этого вам будут доступны команда управления (/info). С её помощью вы можете посмотреть информацию об аккаунте или кикнуть его с сервера";

    private static final String infoMessage =
            "Аккаунт <account>\n" +
                    "Зарегистрирован <regdate> назад\n" +
                    "IP регистрации <regip>\n" +
                    "Последний авторизированный IP <authip> назад\n" +
                    "Привязан к соц.сети <linkdate>\n" +
                    "Сейчас аккаунт <status>";

    public VKListener(TFAManager manager) {
        this.manager = manager;
        infoKeyboard = generateInfoKB();
    }

    private Keyboard generateInfoKB() {
        Keyboard keyboard = new Keyboard();
        List<List<KeyboardButton>> keys = new ArrayList<>();
        List<KeyboardButton> line1 = new ArrayList<>();
        List<KeyboardButton> line2 = new ArrayList<>();


        line1.add(new KeyboardButton()
                .setAction(
                        new KeyboardButtonAction()
                                .setType(TemplateActionTypeNames.CALLBACK)
                                .setLabel("Кикнуть аккаунт с сервера")
                                .setPayload(GSON.toJson("kick"))
                )
                .setColor(KeyboardButtonColor.POSITIVE)
        );

        line2.add(new KeyboardButton()
                .setAction(
                        new KeyboardButtonAction()
                                .setType(TemplateActionTypeNames.CALLBACK)
                                .setLabel("Получить обновлённую информацию")
                                .setPayload(GSON.toJson("update"))
                )
                .setColor(KeyboardButtonColor.PRIMARY)
        );

        keys.add(line1);
        keys.add(line2);
        keyboard.setButtons(keys);
        keyboard.setInline(true);
        return keyboard;
    }

    @EventHandler
    public void help(VKMessageEvent e) {
        int id = e.getPeer();
        String message = e.getMessage().getText();
        if (message.toLowerCase().startsWith("/help") || message.toLowerCase().startsWith("/помощь") || message.equalsIgnoreCase("/") || message.equalsIgnoreCase("/link")) {
            vkManager.sendMessage(id, helpMessage);
            return;
        }
    }

    @EventHandler
    public void info(VKMessageEvent e) {
        int id = e.getPeer();
        String message = e.getMessage().getText();
        if (!message.toLowerCase().startsWith("/info") && !message.toLowerCase().startsWith("/аккаунт")) {
            return;
        }
        if (!checkOn2faSimple(id)) {
            vkManager.sendMessage(id, "\uD83D\uDC7B К Вашему профилю VK не привязан аккаунт");
            return;
        }
        String user = manager.getLinkedPlayer(id);
        if(user == null){
            return;
        }
        UserData userData = manager.getManager().getOrLoadUserData(user);
        if(userData == null){
            return;
        }
        TFAData tfaData = manager.getData(user);
        if(tfaData == null){
            return;
        }
        LoginStage stage = manager.getManager().loginManager().getStage(user);
        vkManager.sendMessage(id, infoMessage
                .replace("<account>", tfaData.getPlayer())
                .replace("<regdate>", TextUtils.getTimeLabel(userData.getRegData(), System.currentTimeMillis()))
                .replace("<regip>", userData.getRegIp())
                .replace("<authip>", userData.getIp())
                .replace("<linkdate>", TextUtils.getTimeLabel(tfaData.getDate(), System.currentTimeMillis()))
                .replace("<status>", stage == null ? "оффлайн/авторизирован" : stage.getLang())
                , generateInfoKB());

    }

    @EventHandler
    public void link(VKMessageEvent e) {
        int id = e.getPeer();
        String player = manager.getLinkedPlayer(id);
        if (player != null) {
            return;
        }
        String message = e.getMessage().getText();
        player = manager.getPlayerByCode(message, true);
        if (player == null) {
            return;
        }
        manager.handleConnect(id, player);
        vkManager.sendMessage(id, "\uD83E\uDDF2 Вы успешно привязали аккаунт " + player + " к своему профилю VK");
    }

    private boolean checkOn2faSimple(int id) {
        return manager.getLinkedPlayer(id) != null;
    }

    private boolean checkOn2fa(int id) {
        String player = manager.getLinkedPlayer(id);
        if (player == null) {
            vkManager.sendMessage(id, "\uD83D\uDC7B К Вашему профилю VK не привязан аккаунт");
            return false;
        }
        LoginStage activeStage = AuthPlugin.getAuthManager().loginManager().getStage(player);
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
        if (activeStage == null) {
            vkManager.sendMessage(id, "\uD83D\uDC7B Аккаунт оффлайн либо уже авторизирован");
            return false;
        }
        if (activeStage != LoginStage.WAIT_2FA) {
            vkManager.sendMessage(id, "\uD83D\uDC7B Сначала введите пароль на сервере");
            return false;
        }
        if (proxiedPlayer == null) {
            vkManager.sendMessage(id, "\uD83D\uDC7B Аккаунт сейчас не на сервере");
            return false;
        }
        return true;
    }

    @EventHandler
    public void confirm(VKCallbackButtonPressEvent e) {
        int id = e.getButtonEvent(null).getPeerID();
        String uuid = e.getButtonEvent(null).getPayload();
        if (uuid.equalsIgnoreCase("confirm")) {
            if (!checkOn2fa(id)) {
                return;
            }
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(manager.getLinkedPlayer(id));
            manager.getManager().loginManager().handle2FAConfirm(id, proxiedPlayer);
        }
    }

    @EventHandler
    public void cancel(VKCallbackButtonPressEvent e) {
        int id = e.getButtonEvent(null).getPeerID();
        String uuid = e.getButtonEvent(null).getPayload();
        if (uuid.equalsIgnoreCase("cancel")) {
            if (!checkOn2fa(id)) {
                return;
            }
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(manager.getLinkedPlayer(id));
            proxiedPlayer.disconnect(new ComponentBuilder("§cАвторизация отклонена из §9VK").create());
            vkManager.sendMessage(id, "\uD83C\uDF44 Авторизация отклонена и аккаунт кикнут!");

        }
    }

}
