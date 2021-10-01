package com.zephyr.cipherchat.utils;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.core.app.NotificationCompat;

import com.zephyr.cipherchat.R;
import com.zephyr.cipherchat.activity.ChatRoomActivity;
import com.zephyr.cipherchat.activity.MainActivity;
import com.zephyr.cipherchat.app.AppController;
import com.zephyr.cipherchat.app.Config;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationUtils {

    NotificationManager notificationManager;

    private static String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public NotificationUtils() {

    }

    public void showNotificationMessage(
            String title, String message, String timeStamp, Intent intent) {
        showNotificationMessage(title, message, timeStamp, intent, null);
    }

    public void showNotificationMessage(
            final String title,
            final String message,
            final String timeStamp,
            Intent intent,
            String imageUrl) {

        String channelId = Config.PUSH_NOTIFICATION;

        // notification icon
        final int icon = R.drawable.notification_logo;

        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        final NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(
                mContext, channelId);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/notification");

        if (!TextUtils.isEmpty(imageUrl)) {

            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showBigNotification(
                            bitmap,
                            mBuilder,
                            icon,
                            title,
                            message,
                            timeStamp,
                            resultPendingIntent,
                            alarmSound);
                } else {
                    showSmallNotification(
                            mBuilder,
                            icon,
                            title,
                            message,
                            timeStamp,
                            resultPendingIntent,
                            alarmSound);
                }
            }
        } else {
            showSmallNotification(
                    mBuilder,
                    icon,
                    title,
                    message,
                    timeStamp,
                    resultPendingIntent,
                    alarmSound);
            playNotificationSound();
        }
    }

    /**
     * Shows smaller notifications
     * @param mBuilder
     * @param icon
     * @param title
     * @param message
     * @param timeStamp
     * @param resultPendingIntent
     * @param alarmSound
     */
    private void showSmallNotification(
            NotificationCompat.Builder mBuilder,
            int icon,
            String title,
            String message,
            String timeStamp,
            PendingIntent resultPendingIntent,
            Uri alarmSound) {

        String channelId = Config.PUSH_NOTIFICATION;
        String channelName = Config.CHANNEL_NAME;

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        try {
            if(Config.appendNotificationMessages){
                // store the notification in shared pref first
                AppController.getInstance().getPrefManager().addNotification(message);

                // get the notifications from shared preferences
                String oldNotification = AppController.getInstance().getPrefManager().getNotifications();

                List<String> messages = Arrays.asList(oldNotification.split("\\|"));

                for (int i = messages.size() - 1; i >= 0; i--) {
                    inboxStyle.addLine(messages.get(i));
                }
            }else{
                inboxStyle.addLine(message);
            }
            Notification notification;
            notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(inboxStyle)
                    .setWhen(getTimeMilliSec(timeStamp))
                    //.setSmallIcon(R.drawable.ic_notification_small)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .build();
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Config.NOTIFICATION_ID, notification);

            // For android Oreo and above  notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        channelName,
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows big notifications
     * @param bitmap
     * @param mBuilder
     * @param icon
     * @param title
     * @param message
     * @param timeStamp
     * @param resultPendingIntent
     * @param alarmSound
     */
    private void showBigNotification(
            Bitmap bitmap,
            NotificationCompat.Builder mBuilder,
            int icon,
            String title,
            String message,
            String timeStamp,
            PendingIntent resultPendingIntent,
            Uri alarmSound) {
        String channelId = Config.PUSH_NOTIFICATION;
        String channelName = Config.CHANNEL_NAME;

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        try {
            Notification notification;
            notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(bigPictureStyle)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setColor(Color.GREEN)
                    .setSmallIcon(icon)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .build();
            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            // For android Oreo and above  notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        channelName,
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE , notification);

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     * @param imageUrl
     * @return
     */
    private Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Plays notification sound
     */
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the Application has been moved to background
     * @param context
     * @return
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Clears notification tray messages
     * @param context
     */
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) AppController.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
