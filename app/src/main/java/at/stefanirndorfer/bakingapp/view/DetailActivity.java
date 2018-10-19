package at.stefanirndorfer.bakingapp.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.Objects;

import at.stefanirndorfer.bakingapp.R;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity {

    public static final String RECIPE_ID_EXTRA = "recipe_id";
    public static final String RECIPE_NAME_EXTRA = "recipe_name_extra";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        int recipeId = (int) bundle.get(RECIPE_ID_EXTRA);
        String recipeName = (String) bundle.get(RECIPE_NAME_EXTRA);
        setTitle(recipeName);
        Timber.d("DetailActivity created with recipe id: %s", recipeId);

        //TODO: rethink back-navigation with the individual fragments
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // find out if we run on a Tablet or a Phone
        mTwoPane = getResources().getBoolean(R.bool.isTablet);

        FragmentManager fragmentManager = getSupportFragmentManager();

        // we create a RecipeDetailFragment for Phone and Tablet
        RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
        fragmentManager.beginTransaction()
                .add(R.id.recipe_detail_container, recipeDetailFragment)
                .commit();
        recipeDetailFragment.setRecipeIdAndUpdateModel(recipeId);


        if (mTwoPane) {

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Timber.d("Option item home pressed");
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
