package com.example.muvime.view;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muvime.R;
import com.example.muvime.service.MovieApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupFragment extends Fragment {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "YOUR_API_KEY";
    private static final String SESSION_ID = "YOUR_SESSION_ID";
    private static final Pattern EMAIL_PATTERN = Patterns.EMAIL_ADDRESS;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=.,!?])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^" +
                    "(?=\\S+$)" +
                    ".{4,}" +
                    "$"
    );
    private EditText usernameEditText, passwordEditText, confirmPasswordEditText, nameEditText, mailEditText;
    private Button signupButton;
    private String username;
    private SQLiteDatabase database;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CompositeDisposable compositeDisposable;
    private Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        sharedPreferences = getActivity().getSharedPreferences("com.example.muvime", Context.MODE_PRIVATE);

        Gson gson = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        usernameEditText = view.findViewById(R.id.signup_username);
        passwordEditText = view.findViewById(R.id.signup_password);
        confirmPasswordEditText = view.findViewById(R.id.signup_confirm_password);
        nameEditText = view.findViewById(R.id.signup_name);
        mailEditText = view.findViewById(R.id.signup_email);
        signupButton = view.findViewById(R.id.btn_signup);

        database = getActivity().openOrCreateDatabase("Accounts", getActivity().MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS accounts (username VARCHAR, password VARCHAR, name VARCHAR, mail VARCHAR, listId INT, image BLOB)");

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirm_password = confirmPasswordEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String mail = mailEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty() || confirm_password.isEmpty() || name.isEmpty() || mail.isEmpty()){
                    if(username.isEmpty())
                        usernameEditText.setError(getResources().getText(R.string.not_empty));
                    if(password.isEmpty())
                        passwordEditText.setError(getResources().getText(R.string.not_empty));
                    if(confirm_password.isEmpty())
                        confirmPasswordEditText.setError(getResources().getText(R.string.not_empty));
                    if(name.isEmpty())
                        nameEditText.setError(getResources().getText(R.string.not_empty));
                    if(mail.isEmpty())
                        mailEditText.setError(getResources().getText(R.string.not_empty));
                }else{
                    if(!EMAIL_PATTERN.matcher(mail).matches() || !PASSWORD_PATTERN.matcher(password).matches()|| !USERNAME_PATTERN.matcher(username).matches() || !password.equals(confirm_password)){
                        if(!EMAIL_PATTERN.matcher(mail).matches())
                            mailEditText.setError(getResources().getText(R.string.mail_pattern));
                        if(!PASSWORD_PATTERN.matcher(password).matches())
                            passwordEditText.setError(getResources().getText(R.string.pass_pattern));
                        if(!USERNAME_PATTERN.matcher(username).matches())
                            usernameEditText.setError(getResources().getText(R.string.username_pattern));
                        if(!password.equals(confirm_password))
                            confirmPasswordEditText.setError(getResources().getText(R.string.pass_not_match));
                    }else{
                        Cursor cursor = database.rawQuery("SELECT * FROM accounts WHERE username = ?", new String[]{username});

                        if (cursor != null && cursor.getCount() > 0){
                            usernameEditText.setError(getResources().getText(R.string.username_used));
                        }else{
                            ContentValues values = new ContentValues();
                            values.put("username", username);
                            values.put("password", password);
                            values.put("name", name);
                            values.put("mail", mail);

                            long result = database.insert("accounts", null, values);

                            if (result != -1) {
                                loadDataCreateList();

                                editor = sharedPreferences.edit();
                                editor.putString("username", username);
                                editor.apply();

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), "Kayıt eklenirken bir hata oluştu!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        cursor.close();
                    }
                }
                closeKeyboard(view);
            }
        });
        return view;
    }

    private void loadDataCreateList(){
        final MovieApiService apiService = retrofit.create(MovieApiService.class);
        compositeDisposable = new CompositeDisposable();

        String movieListJson = "{"
                + "\"name\": \""+username+"\","
                + "\"description\": \"My movie watch list later.\","
                + "\"language\": \"en\""
                + "}";

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), movieListJson);

        compositeDisposable.add(apiService.getCreateList(SESSION_ID, requestBody, API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseCreateList));
    }

    private void handleResponseCreateList(MovieApiService.ListResponse response) {
        database.execSQL("UPDATE accounts SET listId = ? WHERE username = ?", new Object[]{response.getList_id(), username});
        Log.v("Tag", "mesaj  " + response.getStatus_message());
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    private void closeKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
