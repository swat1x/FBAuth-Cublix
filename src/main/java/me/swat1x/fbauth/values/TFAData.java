package me.swat1x.fbauth.values;

import lombok.Value;

@Value
public class TFAData {

    String player;
    long account;
    long date;
    boolean ban;

}
