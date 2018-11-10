package at.stefanirndorfer.bakingapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.stefanirndorfer.bakingapp.view.DetailActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
public class DetailActivityTest {

    public static final String NUTELLA_PIE = "Nutella Pie";
    DetailActivity mActivity;

    @Rule
    public ActivityTestRule<DetailActivity> mActivityTestRule = new ActivityTestRule<DetailActivity>(DetailActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), DetailActivity.class);
            Integer index = 0;
            intent.putExtra("recipe_id", index);
            intent.putExtra("recipe_name_extra", "Nutella Pie");
            return intent;
        }
    };

    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }


    @Test
    public void clickItemOne_checkIfPreviousNavigationButtonIsNotDisplayed() {

        // First, scroll to the position that needs to be matched and click on it.
        onView(withId(R.id.recycler_view_steps_rv)).perform(RecyclerViewActions.scrollToPosition(0))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                        click()));

        // Match the text in an item below the fold and check that it's displayed.
        String itemElementText = mActivityTestRule.getActivity().getResources().getString(R.string.previous_step_button_label);
        onView(withText(itemElementText)).check(matches(not(isDisplayed())));
    }

    @Test
    public void checkCorrectToolbarName() {
        onView(allOf(isAssignableFrom(TextView.class),
                withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText(NUTELLA_PIE)));
    }
}
