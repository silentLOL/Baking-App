package at.stefanirndorfer.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import at.stefanirndorfer.bakingapp.view.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickItemOne_checkIfStepsAreDisplayed() {
        // First, scroll to the position that needs to be matched and click on it.
        onView(ViewMatchers.withId(R.id.recycler_view_recipes_list_rv))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1,
                        click()));

        // Match the text in an item below the fold and check that it's displayed.
        String itemElementText = "Recipe Introduction";
        onView(withText(itemElementText)).check(matches(isDisplayed()));
    }

    @Test
    public void clickItemOne_checkIfIngredientsButtonIsDisplayed() {
        // First, scroll to the position that needs to be matched and click on it.
        onView(ViewMatchers.withId(R.id.recycler_view_recipes_list_rv))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1,
                        click()));

        // Match the text in an item below the fold and check that it's displayed.
        String itemElementText = mActivityTestRule.getActivity().getResources().getString(R.string.ingredients_button_label);
        onView(withText(itemElementText)).check(matches(isDisplayed()));
    }

}
