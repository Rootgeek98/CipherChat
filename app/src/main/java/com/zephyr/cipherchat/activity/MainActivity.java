package com.zephyr.cipherchat.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.zephyr.cipherchat.R;
import com.zephyr.cipherchat.app.Config;
import com.zephyr.cipherchat.helper.SQLiteHandler;
import com.zephyr.cipherchat.helper.SessionManager;
import com.zephyr.cipherchat.utils.NotificationUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId, txtMessage;

    /*private TextView tvFirstname;
    private TextView tvtLastname;
    private TextView tvUsername;
    private TextView tvPhoneNumber;*/
    private Button buttonSubscribe;
    private Button btnLogout;
    private Button btnDashboard;

    private SQLiteHandler sqLiteHandler;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtRegId = findViewById(R.id.txt_reg_id);
        txtMessage = findViewById(R.id.txt_push_message);


        /*tvFirstname = findViewById(R.id.tvFirstname);
        tvtLastname = findViewById(R.id.tvLastname);
        tvUsername = findViewById(R.id.tvUsername);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);*/
        btnLogout = findViewById(R.id.btnLogout);
        btnDashboard = findViewById(R.id.btnDashboard);

        buttonSubscribe = (Button)findViewById(R.id.button_subscribe);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = "Fcm notifications";
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_DEFAULT));
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    //FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    //Toast.makeText(MainActivity.this, ""+token, Toast.LENGTH_SHORT).show();

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    txtMessage.setText(message);
                }
            }
        };

        buttonSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // Subscribe User To Topic
                FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
            }
        });

        // SqLite database handler
        sqLiteHandler = new SQLiteHandler(getApplicationContext());

        // session manager
        sessionManager = new SessionManager(getApplicationContext());

        if (!sessionManager.isLoggedIn()) {
            logoutUser();
        }

        /*// Fetching user details from sqlite
        HashMap<String, String> user = sqLiteHandler.getUserDetails();

        String firstname = user.get("firstname");
        String lastname = user.get("lastname");
        String username = user.get("username");
        String phone_number = user.get("phone_number");

        // Displaying the user details on the screen
        tvFirstname.setText(firstname);
        tvtLastname.setText(lastname);
        tvUsername.setText(username);
        tvPhoneNumber.setText(phone_number);*/

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        DashboardActivity.class);
                startActivity(i);
                finish();
            }
        });

        displayFirebaseRegId();

    }

    /**
     * Fetches reg id from shared preferences
     * and displays on the screen
     */
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId)) {
            txtRegId.setText("Firebase Reg Id: " + regId);
            Toast.makeText(MainActivity.this, "" + regId, Toast.LENGTH_SHORT).show();
        } else {
            txtRegId.setText("Firebase Reg Id is not received yet!");
        }
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        sessionManager.setLogin(false);

        sqLiteHandler.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}