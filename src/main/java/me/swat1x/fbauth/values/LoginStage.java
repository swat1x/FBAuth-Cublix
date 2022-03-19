package me.swat1x.fbauth.values;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LoginStage {

    REGISTER("регистрация"),
    LOGIN("авторизация"),
    WAIT_2FA("ожидает 2fa подтверждения");

    private final String lang;

}
