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

public class AppConfig {

    public static final String URL_LOGIN = "http://192.168.0.28/cipher_chat/app/login.php"; // Login URL

    public static final String URL_REGISTER = "http://192.168.0.28/cipher_chat/app/register.php"; // Register URL

    public static final String CHAT_WEBSOCKETS = "http://192.168.0.28:8888/cipher_chat/bin/server.php"; // WebSockets Chat URL

    public static final String URL_CHAT = ""; // Chat URL

}
