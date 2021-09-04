package com.zephyr.cipherchat.app;

/**
 * Contains the URLs in which the app uses to send requests to the server
 *
 * @author Bill Glinton
 * @author Romuald Ashuza
 * @author Betty Kyalo
 * @author Kelvin Kimutai
 * @version 1.0
 */

public class Config {

    public static final String URL_LOGIN = "http://192.168.0.28/cipher_chat/app/login.php"; // Login URL

    public static final String URL_REGISTER = "http://192.168.0.28/cipher_chat/app/register.php"; // Register URL

    public static final String CHAT_WEBSOCKETS = "http://192.168.0.28:8888/cipher_chat/bin/server.php"; // WebSockets Chat URL

    public static final String URL_CHAT = ""; // Chat URL

    // flag to identify whether to show single line
    // or multi line text in push notification tray
    public static boolean appendNotificationMessages = true;

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // type of push messages
    public static final int PUSH_TYPE_CHATROOM = 1;
    public static final int PUSH_TYPE_USER = 2;

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

}
