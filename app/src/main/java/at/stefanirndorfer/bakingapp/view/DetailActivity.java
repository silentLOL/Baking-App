package at.stefanirndorfer.bakingapp.view;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.Objects;

import at.stefanirndorfer.bakingapp.R;
import at.stefanirndorfer.bakingapp.viewmodel.DetailViewModel;
import at.stefanirndorfer.bakingapp.viewmodel.ViewModelFactory;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity {

    public static final String RECIPE_ID_EXTRA = "recipe_id";
    public static final String RECIPE_NAME_EXTRA = "recipe_name_extra";
    private DetailViewModel mViewModel;

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

        // get us a viewmodel
        mViewModel = obtainViewModel(this);
        mViewModel.start(recipeId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Timber.d("Option item home pressed");
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public static DetailViewModel obtainViewModel(FragmentActivity activity) {
        // Use a Factory to inject dependencies into the ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        DetailViewModel viewModel = ViewModelProviders.of(activity, factory).get(DetailViewModel.class);
        return viewModel;
    }
}
