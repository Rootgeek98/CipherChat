package com.zephyr.cipherchat.app;

public class EndPoints {
    public static final String BASE_URL = "http://192.168.0.28/cipher_chat/v1";
    public static final String LOGIN = BASE_URL + "/user/login";
    public static final String CREATE_ACCOUNT = BASE_URL + "/user/create_account";
    public static final String USER = BASE_URL + "/user/_ID_";
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
}
