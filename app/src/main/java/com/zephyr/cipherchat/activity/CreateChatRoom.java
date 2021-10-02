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
import com.zephyr.cipherchat.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateChatRoom extends AppCompatActivity {

    private static final String TAG = CreateChatRoom.class.getSimpleName();

    private Button btnCreate;

    private EditText etRoomName;

    private EditText etPassword;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_room);

        etRoomName = findViewById(R.id.etRoomName);

        etPassword = findViewById(R.id.etPassword);

        btnCreate = findViewById(R.id.btnCreate);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Check if user is already logged in or not
        if (AppController.getInstance().getPrefManager().getUser() == null) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(CreateChatRoom.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnCreate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String roomname = etRoomName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String empty_fields = getString(R.string.empty_fields);

                // Check for empty data in the form
                if (roomname.isEmpty() || password.isEmpty()) {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            empty_fields, Toast.LENGTH_LONG)
                            .show();
                }else {
                    // login user
                    createChatRoom(roomname, password);
                }
            }

        });
    }

    private void createChatRoom(final String roomname, final String password) {

        String logging_in = getString(R.string.creating_chat_room);

        // Tag used to cancel the request
        String tag_string_req = "req_login";

        progressDialog.setMessage(logging_in);
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.CREATE_CHAT_ROOM, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Create ChatRoom Response: " + response.toString());
                hideDialog();

                try {
                    String empty_fields = getString(R.string.empty_fields);
                    JSONObject jObj = new JSONObject(response);

                    // Check for error node in json
                    if (!jObj.getBoolean("error")) {
                        // Chat room successfully created
                        String success_create_chat_room = getString(R.string.success_create_chat_room);

                        Toast.makeText(getApplicationContext(), success_create_chat_room, Toast.LENGTH_LONG).show();

                        // Launch main activity
                        Intent intent = new Intent(CreateChatRoom.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        Toast.makeText(getApplicationContext(), empty_fields, Toast.LENGTH_LONG).show();

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
                params.put("room_name", roomname);
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