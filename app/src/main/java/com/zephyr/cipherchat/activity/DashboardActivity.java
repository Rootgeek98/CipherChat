package com.zephyr.cipherchat.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.zephyr.cipherchat.R;
import com.zephyr.cipherchat.app.AppController;
import com.zephyr.cipherchat.helper.AppPreferenceManager;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = DashboardActivity.class.getSimpleName();
    private TextView txtFirstName;
    private TextView txtLastName;
    private TextView txtUserName;
    private TextView txtPhoneNumber;
    private AppPreferenceManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        txtFirstName = findViewById(R.id.fname);
        txtLastName = findViewById(R.id.lname);
        txtUserName = findViewById(R.id.uname);
        txtPhoneNumber = findViewById(R.id.contact);

        /*
         * Check for login session.
         * If not logged in launch Login Activity
         */
        if (AppController.getInstance().getPrefManager().getUser() == null) {
            logoutUser();
        }

        String firstname = AppController.getInstance().getPrefManager().getUser().getFirstname();
        Log.d(TAG, "firstname: "+firstname);
        String lastname = AppController.getInstance().getPrefManager().getUser().getLastname();
        Log.d(TAG, "lastname: "+lastname);
        String username = AppController.getInstance().getPrefManager().getUser().getUsername();
        Log.d(TAG, "lastname: "+firstname);
        String contact = AppController.getInstance().getPrefManager().getUser().getPhone_number();
        Log.d(TAG, "lastname: "+contact);

        String resFname = getString(R.string.firstname);
        String resLname = getString(R.string.lastname);
        String resUsername = getString(R.string.username);
        String resContact = getString(R.string.phone_number);

        // Displaying the user details on the screen
        txtFirstName.setText(resFname+": "+firstname);
        txtLastName.setText(resLname+": "+ lastname);
        txtUserName.setText(resUsername+": "+username);
        txtPhoneNumber.setText(resContact+": "+contact);

        // session manager
        session = new AppPreferenceManager(getApplicationContext());

    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.clear();
        session.setLogin(false);

        // Launching the login activity
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}