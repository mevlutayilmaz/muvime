package com.example.muvime.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.TestLooperManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muvime.R;

import java.io.IOException;

public class AccountActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 1001;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_PERMISSION = 1;
    private static final int REQUEST_GALLERY = 2;

    private ImageView account_image;
    private TextView gallery, camera;
    private ImageButton add_image;
    private FrameLayout frameLayout;
    private LinearLayout gallery_camera, layout_edit_name, layout_edit_mail;
    private Button languageButton, edit_name_btn, edit_mail_btn, change_mail_btn, change_name_btn;
    private Spinner languageSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        add_image = findViewById(R.id.add_image);
        change_mail_btn = findViewById(R.id.mail_change);
        change_name_btn = findViewById(R.id.name_change);
        edit_mail_btn = findViewById(R.id.edit_mail_button);
        edit_name_btn = findViewById(R.id.edit_name_button);
        layout_edit_name = findViewById(R.id.linearLayout_name);
        layout_edit_mail = findViewById(R.id.linearLayout_mail);
        gallery = findViewById(R.id.agallery);
        camera = findViewById(R.id.acamera);
        account_image = findViewById(R.id.account_image);
        frameLayout = findViewById(R.id.frame_layout);
        gallery_camera = findViewById(R.id.linear_gallerycamera);
        languageButton = findViewById(R.id.language_button);
        languageSpinner = findViewById(R.id.language_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);



        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedLanguageIndex = languageSpinner.getSelectedItemPosition();
                String[] languages = getResources().getStringArray(R.array.languages);
                String selectedLanguage = languages[selectedLanguageIndex];
                Log.v("Tag", "Seçilen Dil: " + selectedLanguage);
            }
        });

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FragmentManager fragmentManager = getSupportFragmentManager();
                //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
                //PhotoFragment photoFragment = new PhotoFragment();
                //fragmentTransaction.replace(R.id.frame_layout,photoFragment).commit();

                gallery_camera.setVisibility(View.VISIBLE);
                Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
                gallery_camera.startAnimation(slideUp);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //ActivityCompat.requestPermissions(AccountActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                    openGallery();
                } else {
                    openGallery();
                }
                gallery_camera.setVisibility(View.INVISIBLE);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AccountActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    dispatchTakePictureIntent();
                }
                gallery_camera.setVisibility(View.INVISIBLE);
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

        change_name_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        change_mail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();

                try {
                    Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    account_image.setImageBitmap(selectedImageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            account_image.setImageBitmap(imageBitmap);

            // Fotoğrafı kaydetme işlemi
            //saveImageToExternalStorage(imageBitmap);
        }

        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null) {
            // Seçilen resmi alma işlemini burada yapabilirsiniz
            Uri selectedImageUri = data.getData();
            // ImageView'e seçilen resmi yükle
            account_image.setImageURI(selectedImageUri);
        }

    }

    /*private void saveImageToExternalStorage(Bitmap imageBitmap) {
        String fileName = "account_image.jpg";
        File directory = getExternalFilesDir(null);
        File file = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Fotoğraf kaydedildi: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("MainActivity", "Hata: " + e.getMessage());
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Kamera izni reddedildi.", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }else{
                Toast.makeText(this, "Galeri izni reddedildi.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}