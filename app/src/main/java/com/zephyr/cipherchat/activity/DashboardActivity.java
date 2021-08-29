package com.zephyr.cipherchat.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zephyr.cipherchat.R;

public class DashboardActivity extends AppCompatActivity {

    private Button chatBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        chatBtn = findViewById(R.id.chatBtn);

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, ChatActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}