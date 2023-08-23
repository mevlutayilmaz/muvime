package com.example.muvime.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muvime.LanguageManager;
import com.example.muvime.R;
import com.example.muvime.adapter.MovieAdapter;
import com.example.muvime.response.WatchListResponse;
import com.example.muvime.service.MovieApiService;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountFragment extends Fragment {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "f0ee7c5edfa3b5b8d7fd73d8b1700c72";
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_PERMISSION = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final Pattern EMAIL_PATTERN = Patterns.EMAIL_ADDRESS;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=.,!?])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 8 characters
                    "$"
    );
    private int list_id;
    private ImageView account_image;
    private TextView gallery, camera, account_username, account_name, account_email, account_log_out, account_old_pass, account_new_pass, account_confirm_pass;
    private EditText account_edit_name, account_edit_mail;
    private ImageButton add_image;
    private LinearLayout gallery_camera, layout_edit_name, layout_edit_mail, layout_language, linearLayout_edit_pass;
    private Button languageButton, edit_name_btn, edit_mail_btn, change_mail_btn, change_name_btn, btn_lan_en, btn_lan_tr, edit_pass_button, pass_change;
    private RecyclerView recycler_add_movie;
    private MovieAdapter movieAdapter;
    private SQLiteDatabase database;
    private String username, password, language;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        LanguageManager lang = new LanguageManager(getActivity());

        add_image = view.findViewById(R.id.add_image);
        account_new_pass = view.findViewById(R.id.account_new_pass);
        account_old_pass = view.findViewById(R.id.account_old_pass);
        account_confirm_pass = view.findViewById(R.id.account_confirm_pass);
        pass_change = view.findViewById(R.id.pass_change);
        edit_pass_button = view.findViewById(R.id.edit_pass_button);
        linearLayout_edit_pass = view.findViewById(R.id.linearLayout_edit_pass);
        account_log_out = view.findViewById(R.id.account_log_out);
        account_username = view.findViewById(R.id.account_username);
        account_edit_name = view.findViewById(R.id.account_edit_name);
        account_edit_mail = view.findViewById(R.id.account_edit_mail);
        account_email = view.findViewById(R.id.account_mail);
        account_name = view.findViewById(R.id.account_name);
        btn_lan_tr = view.findViewById(R.id.btn_language_tr);
        btn_lan_en = view.findViewById(R.id.btn_language_en);
        layout_language = view.findViewById(R.id.linearLayout_language);
        change_mail_btn = view.findViewById(R.id.mail_change);
        change_name_btn = view.findViewById(R.id.name_change);
        edit_mail_btn = view.findViewById(R.id.edit_mail_button);
        edit_name_btn = view.findViewById(R.id.edit_name_button);
        layout_edit_name = view.findViewById(R.id.linearLayout_name);
        layout_edit_mail = view.findViewById(R.id.linearLayout_mail);
        gallery = view.findViewById(R.id.agallery);
        camera = view.findViewById(R.id.acamera);
        account_image = view.findViewById(R.id.account_image);
        gallery_camera = view.findViewById(R.id.linear_gallerycamera);
        languageButton = view.findViewById(R.id.language_button);
        recycler_add_movie = view.findViewById(R.id.recycler_add_movie);
        recycler_add_movie.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));


        sharedPreferences = getActivity().getSharedPreferences("com.example.muvime", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
        language = sharedPreferences.getString("language" ,null);


        database = getActivity().openOrCreateDatabase("Accounts", getActivity().MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("SELECT * FROM accounts WHERE username = ?", new String[]{username});
        int index_password = cursor.getColumnIndex("password");
        int index_name = cursor.getColumnIndex("name");
        int index_mail = cursor.getColumnIndex("mail");
        int index_list = cursor.getColumnIndex("listId");
        int index_image = cursor.getColumnIndex("image");

        if (cursor.moveToFirst()) {
            password = cursor.getString(index_password);
            String name = cursor.getString(index_name);
            String mail = cursor.getString(index_mail);
            list_id = cursor.getInt(index_list);
            byte[] imageBytes = cursor.getBlob(index_image);

            if (imageBytes != null && imageBytes.length > 0) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                account_image.setImageBitmap(imageBitmap);
            }


            account_username.setText(username);
            account_name.setText(name);
            account_email.setText(mail);
        }

        cursor.close();


        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieApiService movieApiService = retrofit2.create(MovieApiService.class);

        Call<WatchListResponse> call = movieApiService.getListDetails(list_id, API_KEY, language);
        call.enqueue(new Callback<WatchListResponse>() {
            @Override
            public void onResponse(Call<WatchListResponse> call, Response<WatchListResponse> response) {
                if(response.isSuccessful()){
                    movieAdapter = new MovieAdapter(response.body().getItems(), requireContext(), true);
                    recycler_add_movie.setAdapter(movieAdapter);
                }
            }

            @Override
            public void onFailure(Call<WatchListResponse> call, Throwable t) {

            }
        });


        edit_pass_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linearLayout_edit_pass.getVisibility() == View.GONE)
                    linearLayout_edit_pass.setVisibility(View.VISIBLE);
                else
                    linearLayout_edit_pass.setVisibility(View.GONE);
            }
        });

        pass_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String old_pass = account_old_pass.getText().toString();
                String new_pass = account_new_pass.getText().toString();
                String confirm_pass = account_confirm_pass.getText().toString();

                if(old_pass.isEmpty() || new_pass.isEmpty() ||confirm_pass.isEmpty()){
                    if(old_pass.isEmpty())
                        account_new_pass.setError(getResources().getText(R.string.not_empty));
                    if(old_pass.isEmpty())
                        account_old_pass.setError(getResources().getText(R.string.not_empty));
                    if(confirm_pass.isEmpty())
                        account_confirm_pass.setError(getResources().getText(R.string.not_empty));
                }else{
                    if(!old_pass.equals(password))
                        account_old_pass.setError(getResources().getText(R.string.old_pass_not_match));
                    else{
                        if(!PASSWORD_PATTERN.matcher(new_pass).matches() || !new_pass.equals(confirm_pass)){
                            if(!PASSWORD_PATTERN.matcher(new_pass).matches())
                                account_new_pass.setError(getResources().getText(R.string.pass_pattern));
                            if(!new_pass.equals(confirm_pass))
                                account_confirm_pass.setError(getResources().getText(R.string.pass_not_match));
                        } else{
                            database.execSQL("UPDATE accounts SET password = ? WHERE username = ?", new String[]{new_pass, username});

                            account_old_pass.setText("");
                            account_new_pass.setText("");
                            account_confirm_pass.setText("");

                            Snackbar.make(view, getResources().getString(R.string.change_succes), Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
                closeKeyboard();
            }
        });

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gallery_camera.getVisibility() == View.GONE)
                    gallery_camera.setVisibility(View.VISIBLE);
                else
                    gallery_camera.setVisibility(View.GONE);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSION);
                    else
                        openGallery();
                }else{
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                    else
                        openGallery();
                }

                gallery_camera.setVisibility(View.GONE);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                else
                    dispatchTakePictureIntent();

                gallery_camera.setVisibility(View.GONE);
            }
        });

        edit_name_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layout_edit_name.getVisibility() == View.GONE)
                    layout_edit_name.setVisibility(View.VISIBLE);
                else
                    layout_edit_name.setVisibility(View.GONE);
            }
        });

        edit_mail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layout_edit_mail.getVisibility() == View.GONE)
                    layout_edit_mail.setVisibility(View.VISIBLE);
                else
                    layout_edit_mail.setVisibility(View.GONE);
            }
        });

        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layout_language.getVisibility() == View.GONE)
                    layout_language.setVisibility(View.VISIBLE);
                else
                    layout_language.setVisibility(View.GONE);
            }
        });

        change_name_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(account_edit_name.getText().equals("")){
                    account_edit_name.setError(getResources().getText(R.string.not_empty));
                }else{
                    database.execSQL("UPDATE accounts SET name = ? WHERE username = ?", new String[]{account_edit_name.getText().toString(), username});
                    account_name.setText(account_edit_name.getText());
                    account_edit_name.setText("");

                    Snackbar.make(view, getResources().getString(R.string.change_succes), Snackbar.LENGTH_LONG).show();
                }
                closeKeyboard();
            }
        });

        change_mail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(account_edit_mail.getText().equals("")){
                    account_edit_mail.setError(getResources().getText(R.string.not_empty));
                }else{
                    if(!EMAIL_PATTERN.matcher(account_edit_mail.getText().toString()).matches())
                        account_edit_mail.setError(getResources().getText(R.string.mail_pattern));
                    else{
                        database.execSQL("UPDATE accounts SET mail = ? WHERE username = ?", new String[]{account_edit_mail.getText().toString(), username});
                        account_email.setText(account_edit_mail.getText().toString());
                        account_edit_mail.setText("");

                        Snackbar.make(view, getResources().getString(R.string.change_succes), Snackbar.LENGTH_LONG).show();
                    }


                }
                closeKeyboard();
            }
        });

        btn_lan_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = sharedPreferences.edit();
                editor.putString("language", "en");
                editor.apply();

                lang.updateRespource("en");
                requireActivity().recreate();
            }
        });

        btn_lan_tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = sharedPreferences.edit();
                editor.putString("language", "tr");
                editor.apply();

                lang.updateRespource("tr");
                requireActivity().recreate();
            }
        });

        account_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.log_out));
                builder.setMessage(getResources().getString(R.string.are_you_sure));
                builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor = sharedPreferences.edit();
                        editor.putString("username", null);
                        editor.apply();

                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.no), null);
                builder.show();
            }
        });


        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            account_image.setImageBitmap(imageBitmap);
            saveImageToDatabase(imageBitmap);

            requireActivity().recreate();
        }

        if (requestCode == REQUEST_GALLERY && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            account_image.setImageURI(selectedImageUri);

            try {
                Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                saveImageToDatabase(selectedImageBitmap);

                requireActivity().recreate();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.camera_denied), Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }else{
                Toast.makeText(getActivity(), getResources().getString(R.string.gallery_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImageToDatabase(Bitmap imageBitmap) {
        if (imageBitmap != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            database.execSQL("UPDATE accounts SET image = ? WHERE username = ?", new Object[]{imageBytes, username});
        }
    }
    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}