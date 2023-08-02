package com.example.muvime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ImageView movie_image, detail_bg_image;
    private TextView duration, category, name_year, realese_year, overview;
    private RecyclerView recyclerView_more_like, recyclerView_top_cast;
    private MovieAdapter movieAdapter;
    private List<Movie> movieList;
    private CastAdapter adapter;
    private List<Cast> castList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String toolbarTitle = "MUVİME";
        SpannableString spannableString = new SpannableString(toolbarTitle);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, toolbarTitle.length(), 0);
        getSupportActionBar().setTitle(spannableString);

        movie_image = findViewById(R.id.detail_movie_image);
        detail_bg_image = findViewById(R.id.detail_bg_image);
        overview = findViewById(R.id.detail_overview);
        realese_year = findViewById(R.id.detail_realese_year);
        duration = findViewById(R.id.detail_hour);
        duration = findViewById(R.id.detail_hour);
        name_year = findViewById(R.id.detail_name_year);
        category = findViewById(R.id.detail_category);

        recyclerView_more_like = findViewById(R.id.recycler_more_like);
        recyclerView_top_cast = findViewById(R.id.recycler_top_cast);
        recyclerView_more_like.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView_top_cast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        castList = new ArrayList<>();
        castList.add(new Cast(R.drawable.marcos_ruiz, "Marcos Ruiz", "Nacho", 82));
        castList.add(new Cast(R.drawable.marcos_ruiz, "Marcos Ruiz", "Nacho", 82));
        castList.add(new Cast(R.drawable.marcos_ruiz, "Marcos Ruiz", "Nacho", 82));
        castList.add(new Cast(R.drawable.marcos_ruiz, "Marcos Ruiz", "Nacho", 82));
        castList.add(new Cast(R.drawable.marcos_ruiz, "Marcos Ruiz", "Nacho", 82));
        castList.add(new Cast(R.drawable.marcos_ruiz, "Marcos Ruiz", "Nacho", 82));
        castList.add(new Cast(R.drawable.marcos_ruiz, "Marcos Ruiz", "Nacho", 82));
        castList.add(new Cast(R.drawable.marcos_ruiz, "Marcos Ruiz", "Nacho", 82));
        castList.add(new Cast(R.drawable.marcos_ruiz, "Marcos Ruiz", "Nacho", 82));

        adapter = new CastAdapter(castList, this);
        recyclerView_top_cast.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null) {
            String movie_name = intent.getStringExtra("name");
            String movie_imageResource = intent.getStringExtra("imageResource");
            String movie_category = intent.getStringExtra("category");
            String movie_duration = intent.getStringExtra("duration");
            String movie_year = intent.getStringExtra("year");
            String movie_overview = intent.getStringExtra("overview");
            String movie_bg_image = intent.getStringExtra("bgImage");

            String[] year = movie_year.split("-");

            name_year.setText(movie_name + " (" + year[0] + ")");
            realese_year.setText(movie_year);
            duration.setText(movie_duration);
            category.setText(movie_category);
            overview.setText(movie_overview);
            Glide.with(this).load(movie_imageResource).into(movie_image);
            Glide.with(this).load(movie_bg_image).into(detail_bg_image);
        }

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}