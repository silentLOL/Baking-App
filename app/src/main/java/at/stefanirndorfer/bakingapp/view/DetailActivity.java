package at.stefanirndorfer.bakingapp.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import at.stefanirndorfer.bakingapp.R;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity {

    public static final String RECIPE_ID_EXTRA = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getExtras();
        int recipeId = (int) bundle.get(RECIPE_ID_EXTRA);
        Timber.d("DetailActivity created with recipe id: " + recipeId);
    }
}
