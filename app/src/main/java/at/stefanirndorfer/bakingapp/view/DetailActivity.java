package at.stefanirndorfer.bakingapp.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.viewmodel.MainViewModel;

import static at.stefanirndorfer.bakingapp.viewmodel.MainViewModel.RECIPE_ID_EXTRA;

public class DetailActivity extends AppCompatActivity {
    private static final String RECIPE_ID_EXTRA = "recipe_id_extra";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String id = intent.getStringExtra(RECIPE_ID_EXTRA);
    }
}
