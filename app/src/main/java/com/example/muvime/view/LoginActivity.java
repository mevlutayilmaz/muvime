package com.example.muvime.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.muvime.LanguageManager;
import com.example.muvime.R;
import com.example.muvime.adapter.TabAdapter;
import com.google.android.material.tabs.TabLayout;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = this.getSharedPreferences("com.example.muvime", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        editor = sharedPreferences.edit();
        editor.putString("language", "tr");
        editor.apply();

        if(username != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        tabAdapter.addFragment(new LoginFragment(), getResources().getString(R.string.login));
        tabAdapter.addFragment(new SignupFragment(), getResources().getString(R.string.signup));

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}