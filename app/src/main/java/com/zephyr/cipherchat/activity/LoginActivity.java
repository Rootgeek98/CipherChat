package com.zephyr.cipherchat.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.zephyr.cipherchat.R;
import com.zephyr.cipherchat.app.AppController;
import com.zephyr.cipherchat.app.EndPoints;
import com.zephyr.cipherchat.helper.AppPreferenceManager;
import com.zephyr.cipherchat.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private Button btnLogin;

    private TextView tvRegister;

    private EditText etUsername;

    private EditText etPassword;

    private ProgressDialog progressDialog;

    private AppPreferenceManager appPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);

        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);

        tvRegister = findViewById(R.id.tvRegister);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Session Manager
        appPreferenceManager = new AppPreferenceManager(getApplicationContext());

        // Check if user is already logged in or not
        if (AppController.getInstance().getPrefManager().getUser() != null) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String empty_fields = getString(R.string.empty_fields);

                // Check for empty data in the form
                if (username.isEmpty() || password.isEmpty()) {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            empty_fields, Toast.LENGTH_LONG)
                            .show();
                }else {
                    // login user
                    checkLogin(username, password);
                }
            }

        });

        // Link to Register Screen
        tvRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String username, final String password) {

        String logging_in = getString(R.string.logging_in);

        // Tag used to cancel the request
        String tag_string_req = "req_login";

        progressDialog.setMessage(logging_in);
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    String wrong_credentials = getString(R.string.wrong_credentials);
                    JSONObject jObj = new JSONObject(response);

                    // Check for error node in json
                    if (!jObj.getBoolean("error")) {
                        // user successfully logged in
                        // Create login session
                        appPreferenceManager.setLogin(true);

                        JSONObject userObj = jObj.getJSONObject("user");
                        User user = new User(userObj.getString("phone_number"),
                                userObj.getString("firstname"),
                                userObj.getString("lastname"),
                                userObj.getString("username"));

                        // storing user in shared preferences
                        AppController.getInstance().getPrefManager().storeUser(user);

                        String success_login = getString(R.string.success_login);

                        Toast.makeText(getApplicationContext(), success_login, Toast.LENGTH_LONG).show();

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        Toast.makeText(getApplicationContext(), wrong_credentials, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error occurred during login. Please try again.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Json error: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}