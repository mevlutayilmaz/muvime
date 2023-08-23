package com.example.muvime.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.example.muvime.view.ConOffActivity;

public class  NetworkChangeReceiver extends BroadcastReceiver {
    private FragmentManager fragmentManager;

    public NetworkChangeReceiver(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isNetworkAvailable(context)) {

        } else {
            intent = new Intent(context, ConOffActivity.class);
            context.startActivity(intent);
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

