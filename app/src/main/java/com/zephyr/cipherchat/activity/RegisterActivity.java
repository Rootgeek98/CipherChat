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

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private Button btnSignup;

    private TextView tvLogin;

    private EditText etFirstname;

    private EditText etLastname;

    private EditText etUsername;

    private EditText etPhoneNumber;

    private EditText etPassword;

    private EditText etConfirmPassword;

    private ProgressDialog progressDialog;

    private AppPreferenceManager appPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstname = findViewById(R.id.etFirstname);

        etLastname = findViewById(R.id.etLastname);

        etUsername = findViewById(R.id.etUserName);

        etPhoneNumber = findViewById(R.id.etPhoneNumber);

        etPassword = findViewById(R.id.etSignupPassword);

        etConfirmPassword = findViewById(R.id.etConfirmSignupPassword);

        btnSignup = findViewById(R.id.btnCreateAccount);

        tvLogin = findViewById(R.id.tvLogin);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Session manager
        appPreferenceManager = new AppPreferenceManager(getApplicationContext());

        // Register Button Click event
        btnSignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String firstname = etFirstname.getText().toString().trim();
                String lastname = etLastname.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String phone_number = etPhoneNumber.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirm_password = etConfirmPassword.getText().toString().trim();
                String empty_details = getString(R.string.empty_details);
                String short_password = getString(R.string.short_password);

                if (firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() || phone_number.isEmpty() || password.isEmpty() || confirm_password.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            empty_details, Toast.LENGTH_LONG)
                            .show();
                }
                else if (password.length() < 8){
                    Toast.makeText(getApplicationContext(),
                            short_password, Toast.LENGTH_LONG)
                            .show();
                }else {
                    password.length();
                    if (confirm_password.length() < 8) {
                        Toast.makeText(getApplicationContext(),
                                short_password, Toast.LENGTH_LONG)
                                .show();
                    } else {

                        if (!password.equals(confirm_password)) {

                            Toast.makeText(getApplicationContext(),
                                    "Your Passwords Do not match", Toast.LENGTH_LONG)
                                    .show();

                        }  else {

                            registerUser(firstname, lastname, username, phone_number, password);

                        }
                    }
                }
            }
        });

        // Link to Login Screen
        tvLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String firstname, final String lastname, final String username,
                              final String phone_number, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        String creating_account = getString(R.string.creating_account);

        progressDialog.setMessage(creating_account);
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.SIGNUP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    if (!jObj.getBoolean("error")) {
                        JSONObject userObj = jObj.getJSONObject("user");
                        User user = new User(userObj.getString("phone_number"),
                                userObj.getString("firstname"),
                                userObj.getString("lastname"),
                                userObj.getString("username"));

                        // storing user in shared preferences
                        AppController.getInstance().getPrefManager().storeUser(user);

                        String success_signup = getString(R.string.success_signup);

                        Toast.makeText(getApplicationContext(), success_signup, Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String signup_error = getString(R.string.signup_error);
                        Toast.makeText(getApplicationContext(),
                                signup_error, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Error occurred during registration. Please try again.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Json error: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstname", firstname);
                params.put("lastname", lastname);
                params.put("username", username);
                params.put("phone_number", phone_number);
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