package com.example.muvime.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.ConnectivityManager;

import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muvime.network.NetworkChangeReceiver;

import com.example.muvime.R;


public class MainActivity extends AppCompatActivity {
    private NetworkChangeReceiver networkChangeReceiver;
    private ImageView account_image;
    private TextView toolbar_tittle;
    private FrameLayout frame_image;
    private SQLiteDatabase database;
    private String account_username, language;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkChangeReceiver = new NetworkChangeReceiver(getSupportFragmentManager());
        registerNetworkChangeReceiver();

        account_image = findViewById(R.id.main_account_image);
        frame_image = findViewById(R.id.frame_image);
        toolbar_tittle = findViewById(R.id.main_toolbar_tittle);


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MainFragment fragment = new MainFragment();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment);
        fragmentTransaction.commit();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        sharedPreferences = this.getSharedPreferences("com.example.muvime", Context.MODE_PRIVATE);
        account_username = sharedPreferences.getString("username", null);
        language = sharedPreferences.getString("language" ,null);


        database = this.openOrCreateDatabase("Accounts", this.MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("SELECT * FROM accounts WHERE username = ?", new String[]{account_username});
        int index_image = cursor.getColumnIndex("image");

        if (cursor.moveToFirst()) {
            byte[] imageBytes = cursor.getBlob(index_image);

            if (imageBytes != null && imageBytes.length > 0) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                account_image.setImageBitmap(imageBitmap);
            }
        }

        account_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                AccountFragment accountFragment = new AccountFragment();
                fragmentTransaction.replace(R.id.main_frame_layout, accountFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        toolbar_tittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                MainFragment fragment = new MainFragment();
                fragmentTransaction.replace(R.id.main_frame_layout, fragment);
                fragmentTransaction.commit();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(networkChangeReceiver);
    }

    private void registerNetworkChangeReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }

}