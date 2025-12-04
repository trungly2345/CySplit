package com.example.androidexample;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class KovidhSystemTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testSuccessfulLoginHardcoded() {
        onView(withId(R.id.login_username))
                .perform(typeText("hi"), closeSoftKeyboard());
        onView(withId(R.id.login_password))
                .perform(typeText("hi"), closeSoftKeyboard());

        onView(withId(R.id.login_button)).perform(click());

        onView(withText("Welcome")).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginEmptyFieldsShowsError() {
        onView(withId(R.id.login_button)).perform(click());

        onView(withId(R.id.login_username)).check(matches(isDisplayed()));
    }

    @Test
    public void testOpenSignupPage() {
        onView(withId(R.id.signUpRedirectText)).perform(click());

        onView(withText("Create Account")).check(matches(isDisplayed()));
    }

    @Test
    public void testRotateAndPreserveFields() {
        onView(withId(R.id.login_username))
                .perform(typeText("rotateTest"), closeSoftKeyboard());

        activityRule.getScenario().onActivity(activity ->
                activity.setRequestedOrientation(
                        android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));

        activityRule.getScenario().onActivity(activity ->
                activity.setRequestedOrientation(
                        android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));

        onView(withId(R.id.login_username))
                .check(matches(withText("rotateTest")));
    }
}
