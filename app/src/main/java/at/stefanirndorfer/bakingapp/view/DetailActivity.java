package at.stefanirndorfer.bakingapp.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import java.util.Objects;

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.data.Step;
import at.stefanirndorfer.bakingapp.view.input.FragmentNavigationListener;
import at.stefanirndorfer.bakingapp.view.input.StepItemUserActionListener;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity implements StepItemUserActionListener, FragmentNavigationListener {

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

        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // find out if we run on a Tablet or a Phone
        mTwoPane = getResources().getBoolean(R.bool.isTablet);

        if (savedInstanceState == null) {


            FragmentManager fragmentManager = getSupportFragmentManager();

            if (mTwoPane) {
                IngredientsFragment ingredientsFragment = new IngredientsFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.right_pane_container, ingredientsFragment)
                        .commit();
            } else {
                RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.detail_fragment_container, recipeDetailFragment)
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

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void onIngredientsButtonClicked(View view) {
        showIngredientsFragment();
    }


    private void showStepFragment(Step step) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        StepFragment stepFragment = new StepFragment();
        stepFragment.registerFragmentNavigationListener(this);
        stepFragment.setStep(step);
        if (mTwoPane) {
            fragmentManager.beginTransaction()
                    .replace(R.id.right_pane_container, stepFragment)
                    .addToBackStack(stepFragment.getClass().getCanonicalName())
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.detail_fragment_container, stepFragment)
                    .addToBackStack(stepFragment.getClass().getCanonicalName())
                    .commit();
        }
    }

    private void showIngredientsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        IngredientsFragment ingredientsFragment = new IngredientsFragment();
        if (mTwoPane) {
            fragmentManager.beginTransaction()
                    .replace(R.id.right_pane_container, ingredientsFragment)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.detail_fragment_container, ingredientsFragment)
                    .addToBackStack(ingredientsFragment.getClass().getCanonicalName())
                    .commit();
        }
    }

    @Override
    public void onStepClicked(Step step) {
        Timber.d("Clicked on step: " + step.getShortDescription());
        showStepFragment(step);
    }

    @Override
    public void navigateToIngredientsFragment(int recipeId) {
        showIngredientsFragment();
    }
}
