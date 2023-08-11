package com.example.muvime.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.muvime.R;

public class ConOffActivity extends AppCompatActivity {
    private Button btn_retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_con_off);

        btn_retry = findViewById(R.id.btn_retry);

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConOffActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}