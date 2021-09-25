package com.zephyr.cipherchat.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zephyr.cipherchat.activity.ChatRoomActivity;
import com.zephyr.cipherchat.activity.MainActivity;
import com.zephyr.cipherchat.app.AppController;
import com.zephyr.cipherchat.app.Config;
import com.zephyr.cipherchat.model.Message;
import com.zephyr.cipherchat.model.User;
import com.zephyr.cipherchat.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class CustomMessagingService extends FirebaseMessagingService {

    private static final String TAG = CustomMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        // Saving reg id to shared preferences
        storeRegIdInPref(s);

        // sending reg id to your server
        sendRegistrationToServer(s);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", s);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.d(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.apply();
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Data containing message data as key/value pairs.
     *                      For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        //Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                String from = remoteMessage.getFrom();
                String title = remoteMessage.getData().get("title");
                boolean isBackground = Boolean.parseBoolean(remoteMessage.getData().get("isBackground"));
                String flag = remoteMessage.getData().get("flag");
                String data = remoteMessage.getData().get("data");

                Log.d(TAG, "From: " + remoteMessage.getFrom());
                Log.d(TAG, "title: " + title);
                Log.d(TAG, "isBackground: " + isBackground);
                Log.d(TAG, "flag: " + flag);
                Log.d(TAG, "data: " + data);

                if (flag == null)
                    return;

                if(AppController.getInstance().getPrefManager().getUser() == null){
                    // user is not logged in, skipping push notification
                    Log.e(TAG, "user is not logged in, skipping push notification");
                    return;
                }

                assert from != null;
                if (from.startsWith("/topics/")) {
                    // message received from some topic.
                } else {
                    // normal downstream message.
                }

                switch (Integer.parseInt(flag)) {
                    case Config.PUSH_TYPE_CHATROOM:
                        // push notification belongs to a chat room
                        processChatRoomPush(title, isBackground, data);
                        Log.i(TAG, "Sending message to chat room");
                        break;
                    case Config.PUSH_TYPE_USER:
                        // push notification is specific to user
                        processUserMessage(title, isBackground, data);
                        Log.i(TAG, "Sending message to user");
                        break;
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    /**
     * Processes user specific message
     *
     * It will be displayed with / without image in push notification tray
     *
     * @param title
     * @param isBackground
     * @param data
     */
    private void processUserMessage(String title, boolean isBackground, String data) {
        if (!isBackground) {

            try {
                JSONObject datObj = new JSONObject(data);
                String imageUrl = datObj.getString("image");
                Message message = new Message();
                JSONObject mObj = datObj.getJSONObject("message");
                message.setMessage(mObj.getString("message"));
                message.setId(mObj.getString("message_id"));
                message.setSentAt(mObj.getString("sent_at"));

                JSONObject uObj = datObj.getJSONObject("user");
                User user = new User();
                user.setPhone_number(uObj.getString("phone_number"));
                user.setFirstname(uObj.getString("firstname"));
                user.setLastname(uObj.getString("lastname"));
                user.setUsername(uObj.getString("username"));
                message.setUser(user);

                // verifying whether the app is in background or foreground
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("type", Config.PUSH_TYPE_USER);
                    pushNotification.putExtra("message", message);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils();
                    notificationUtils.playNotificationSound();
                } else {

                    // app is in background. show the message in notification try
                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

                    // check for push notification image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                        showNotificationMessage(getApplicationContext(), String.valueOf(title), user.getUsername() + " : " + message.getMessage(), message.getSentAt(), resultIntent);
                    } else {
                        // push notification contains image
                        // show it with the image
                        showNotificationMessageWithBigImage(getApplicationContext(), String.valueOf(title), message.getMessage(), message.getSentAt(), resultIntent, imageUrl);
                    }
                }

                message.setUser(user);
            } catch (JSONException e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
        }
    }

    private void processChatRoomPush(String title, boolean isBackground, String data) {
        if (!isBackground) {

            try {
                JSONObject datObj = new JSONObject(data);
                String chatRoomId = datObj.getString("room_id");

                JSONObject mObj = datObj.getJSONObject("message");
                Message message = new Message();
                message.setMessage(mObj.getString("message"));
                message.setId(mObj.getString("ucid"));
                message.setSentAt(mObj.getString("sent_at"));

                JSONObject uObj = datObj.getJSONObject("user");

                // skip the message if the message belongs to same user as
                // the user would be having the same message when he was sending
                // but it might differs in your scenario
                if (uObj.getString("phone_number").equals(AppController.getInstance().getPrefManager().getUser().getPhone_number())) {
                    Log.w(TAG, "Skipping the push message as it belongs to same user");
                    return;
                }
                User user = new User();
                user.setPhone_number(uObj.getString("phone_number"));
                user.setFirstname(uObj.getString("firstname"));
                user.setLastname(uObj.getString("lastname"));
                user.setUsername(uObj.getString("username"));
                message.setUser(user);

                // verifying whether the app is in background or foreground
                if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("type", Config.PUSH_TYPE_CHATROOM);
                    pushNotification.putExtra("message", message);
                    pushNotification.putExtra("room_id", chatRoomId);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils();
                    //notificationUtils.playNotificationSound();
                } else {

                    // app is in background. show the message in notification try
                    Intent resultIntent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                    resultIntent.putExtra("room_id", chatRoomId);
                    showNotificationMessage(getApplicationContext(), String.valueOf(title), user.getUsername() + " : " + message.getMessage(), message.getSentAt(), resultIntent);
                }
            } catch (JSONException e) {
                Log.e(TAG, "json parsing error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
        }
    }

    /**
     * Shows notification with text only
     *
     * @param context
     * @param title
     * @param message
     * @param timeStamp
     * @param intent
     */
    private void showNotificationMessage(
            Context context,
            String title,
            String message,
            String timeStamp,
            Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Shows notification with text and image
     *
     * @param context
     * @param title
     * @param message
     * @param timeStamp
     * @param intent
     * @param imageUrl
     */
    private void showNotificationMessageWithBigImage(
            Context context,
            String title,
            String message,
            String timeStamp,
            Intent intent,
            String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
