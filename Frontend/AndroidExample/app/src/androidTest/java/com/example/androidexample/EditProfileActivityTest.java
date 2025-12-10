package com.example.androidexample;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EditProfileActivityTest {

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_USERNAME = "username";

    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USERNAME, "oldUser").apply();
    }

    @Test
    public void testLoadsExistingUsername() {
        try (ActivityScenario<EditProfileActivity> scenario =
                     ActivityScenario.launch(EditProfileActivity.class)) {

            onView(withId(R.id.edit_name))
                    .check(matches(withText("oldUser")));
        }
    }

    @Test
    public void testUpdatesUsernameSuccessfully() {
        try (ActivityScenario<EditProfileActivity> scenario =
                     ActivityScenario.launch(EditProfileActivity.class)) {

            onView(withId(R.id.edit_name))
                    .perform(clearText(), typeText("newUser"));
            closeSoftKeyboard();

            onView(withId(R.id.save_button)).perform(click());

            TestUtils.assertToastDisplayed("Profile updated");

            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String saved = prefs.getString(KEY_USERNAME, "");
            assert(saved.equals("newUser"));
        }
    }

    @Test
    public void testEmptyUsernameShowsErrorToast() {
        try (ActivityScenario<EditProfileActivity> scenario =
                     ActivityScenario.launch(EditProfileActivity.class)) {

            onView(withId(R.id.edit_name))
                    .perform(clearText());
            closeSoftKeyboard();

            onView(withId(R.id.save_button)).perform(click());

            TestUtils.assertToastDisplayed("Username cannot be empty");
        }
    }
}
