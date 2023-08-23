package com.example.muvime.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.muvime.R;


public class LoginFragment extends Fragment {
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private SQLiteDatabase database;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        sharedPreferences = getActivity().getSharedPreferences("com.example.muvime", Context.MODE_PRIVATE);

        usernameEditText = view.findViewById(R.id.login_username);
        passwordEditText = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.login_btn);

        database = getActivity().openOrCreateDatabase("Accounts", getActivity().MODE_PRIVATE, null);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    if(username.isEmpty())
                        usernameEditText.setError(getResources().getText(R.string.not_empty));
                    if(password.isEmpty())
                        passwordEditText.setError(getResources().getText(R.string.not_empty));
                } else {
                    Cursor cursor = database.rawQuery("SELECT * FROM accounts WHERE username = ? AND password = ?", new String[]{username, password});

                    if (cursor.getCount() > 0) {
                        editor = sharedPreferences.edit();
                        editor.putString("username", username);
                        editor.apply();

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        usernameEditText.setText("");
                        passwordEditText.setText("");
                        usernameEditText.setError(getResources().getText(R.string.userpass_wrong));
                        passwordEditText.setError(getResources().getText(R.string.userpass_wrong));

                    }
                    cursor.close();
                }
                closeKeyboard(view);
            }
        });

        return view;
    }

    private void closeKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}