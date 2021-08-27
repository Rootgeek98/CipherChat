package com.zephyr.cipherchat.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zephyr.cipherchat.R;
import com.zephyr.cipherchat.helper.SQLiteHandler;
import com.zephyr.cipherchat.helper.SessionManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView tvFirstname;
    private TextView tvtLastname;
    private TextView tvUsername;
    private TextView tvPhoneNumber;
    private Button btnLogout;
    private Button btnDashboard;

    private SQLiteHandler sqLiteHandler;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvFirstname = findViewById(R.id.tvFirstname);
        tvtLastname = findViewById(R.id.tvLastname);
        tvUsername = findViewById(R.id.tvUsername);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        btnLogout = findViewById(R.id.btnLogout);
        btnDashboard = findViewById(R.id.btnDashboard);

        // SqLite database handler
        sqLiteHandler = new SQLiteHandler(getApplicationContext());

        // session manager
        sessionManager = new SessionManager(getApplicationContext());

        if (!sessionManager.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = sqLiteHandler.getUserDetails();

        String firstname = user.get("firstname");
        String lastname = user.get("lastname");
        String username = user.get("username");
        String phone_number = user.get("phone_number");

        // Displaying the user details on the screen
        tvFirstname.setText(firstname);
        tvtLastname.setText(lastname);
        tvUsername.setText(username);
        tvPhoneNumber.setText(phone_number);

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
}