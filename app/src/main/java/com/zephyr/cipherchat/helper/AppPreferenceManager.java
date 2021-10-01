package com.zephyr.cipherchat.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.zephyr.cipherchat.model.User;

/**
 * Contains the code that controls or manages the user sessions in the app,
 * to determine whether the user is logged in or not.
 *
 * @author Bill Glinton
 * @author Romuald Ashuza
 * @author Betty Kyalo
 * @author Kelvin Kimutai
 * @version 1.0
 */
public class AppPreferenceManager {

    // LogCat tag
    private static String TAG = AppPreferenceManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "cipher_chat";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    // All Shared Preferences Keys
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_NOTIFICATIONS = "notifications";

    public AppPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void storeUser(User user) {
        editor.putString(KEY_PHONE_NUMBER, user.getPhone_number());
        editor.putString(KEY_FIRSTNAME, user.getFirstname());
        editor.putString(KEY_LASTNAME, user.getLastname());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.commit();

        Log.e(TAG, "User is stored in shared preferences. " + user.getPhone_number() + ", " + user.getUsername());
    }

    public User getUser() {
        if (pref.getString(KEY_PHONE_NUMBER, null) != null) {
            String phone_number, firstname, lastname, username;
            phone_number = pref.getString(KEY_PHONE_NUMBER, null);
            firstname = pref.getString(KEY_FIRSTNAME, null);
            lastname = pref.getString(KEY_LASTNAME, null);
            username = pref.getString(KEY_USERNAME, null);

            User user = new User(phone_number, firstname, lastname, username);

            return user;

        } else {

            return null;

        }
    }

    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }

}
