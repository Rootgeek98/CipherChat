package com.zephyr.cipherchat.app;

/**
 * Contains the URLs in which the app uses to interact with the server
 *
 * @author Bill Glinton
 * @author Romuald Ashuza
 * @author Betty Kyalo
 * @author Kelvin Kimutai
 * @version 1.0
 */

public class EndPoints {
    public static final String BASE_URL = "https://cipherchat.000webhostapp.com/cipher_chat/v1";
    public static final String LOGIN = BASE_URL + "/user/login";
    public static final String SIGNUP = BASE_URL + "/user/signup";
    public static final String USER = BASE_URL + "/user/_PHONE_NUMBER_";
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
}
