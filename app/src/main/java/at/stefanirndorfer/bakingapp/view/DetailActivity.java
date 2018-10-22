package at.stefanirndorfer.bakingapp.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import java.util.Objects;

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.data.Step;
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
        mTwoPane = getResources().getBoolean(R.bool.isTablet);

        if (savedInstanceState == null) {


            // find out if we run on a Tablet or a Phone

            FragmentManager fragmentManager = getSupportFragmentManager();

            // we create a RecipeDetailFragment for Phone and Tablet
            RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.detail_fragment_container, recipeDetailFragment)
                    .commit();


            if (mTwoPane) {
                IngredientsFragment ingredientsFragment = new IngredientsFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.ingredients_container, ingredientsFragment)
                        .commit();
            }
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

    public void onIngredientsButtonClicked(View view) {
        showIngredientsFragment();
    }
    
    public void onStepButtonClicked(Step step){
        showStepFragment();
    }

    private void showStepFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        StepFragment stepFragment = new StepFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.detail_fragment_container, stepFragment)
                .commit();
    }

    private void showIngredientsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        IngredientsFragment ingredientsFragment = new IngredientsFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.detail_fragment_container, ingredientsFragment)
                .commit();
    }
}
