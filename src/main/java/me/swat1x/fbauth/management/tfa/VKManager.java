package me.swat1x.fbauth.management.tfa;

import com.ubivashka.vk.bungee.VKAPI;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Keyboard;

import java.util.Random;

public class VKManager {

    public final VkApiClient VK;
    public final GroupActor ACTOR;
    public final Random RANDOM;

    public VKManager() {
        VK = VKAPI.getInstance().getVK();
        ACTOR = VKAPI.getInstance().getActor();
        RANDOM = new Random();
    }

    public void sendMessage(Integer id, String message) {
        try {
            VK.messages().send(ACTOR).randomId(RANDOM.nextInt()).peerId(id).message(message).execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Integer id, String message, Keyboard keyboard) {
        try {
            VK.messages().send(ACTOR).randomId(RANDOM.nextInt()).peerId(id).message(message).keyboard(keyboard).execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }

}
