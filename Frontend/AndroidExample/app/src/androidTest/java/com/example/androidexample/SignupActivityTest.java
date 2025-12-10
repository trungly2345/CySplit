package com.example.androidexample;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.allOf;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.androidexample.TestUtils.ToastMatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented UI tests for SignupActivity.
 */
@RunWith(AndroidJUnit4.class)
public class SignupActivityTest {

    @Rule
    public ActivityScenarioRule<SignupActivity> activityRule =
            new ActivityScenarioRule<>(SignupActivity.class);

    @Before
    public void setup() {
        Intents.init();
    }

    @After
    public void teardown() {
        Intents.release();
    }

    /**
     * Test: Successful signup → show toast → redirect to LoginActivity.
     */
    @Test
    public void testSuccessfulSignup() {

        // Fake backend response so Volley succeeds
        Intents.intending(IntentMatchers.anyIntent())
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

        onView(withId(R.id.signup_username)).perform(typeText("testuser"));
        closeSoftKeyboard();
        onView(withId(R.id.signup_email)).perform(typeText("test@demo.com"));
        closeSoftKeyboard();
        onView(withId(R.id.signup_password)).perform(typeText("pass123"));
        closeSoftKeyboard();
        onView(withId(R.id.signup_name)).perform(typeText("John Doe"));
        closeSoftKeyboard();
        onView(withId(R.id.signup_phone)).perform(typeText("1234567890"));
        closeSoftKeyboard();
        onView(withId(R.id.signup_payment)).perform(typeText("VISA"));
        closeSoftKeyboard();

        onView(withId(R.id.signup_button)).perform(click());

        // Verify toast
        onView(withText("Signup successful! Please login."))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        // Verify next screen
        Intents.intended(IntentMatchers.hasComponent(LoginActivity.class.getName()));
    }

    /**
     * Test: Signup failure → show error toast.
     */
    @Test
    public void testFailedSignup() {

        // Fake a failure from backend
        Intents.intending(IntentMatchers.anyIntent())
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));

        onView(withId(R.id.signup_username)).perform(typeText("failuser"));
        closeSoftKeyboard();
        onView(withId(R.id.signup_email)).perform(typeText("fail@demo.com"));
        closeSoftKeyboard();
        onView(withId(R.id.signup_password)).perform(typeText("pass123"));
        closeSoftKeyboard();
        onView(withId(R.id.signup_name)).perform(typeText("Jane"));
        closeSoftKeyboard();
        onView(withId(R.id.signup_phone)).perform(typeText("0000000000"));
        closeSoftKeyboard();
        onView(withId(R.id.signup_payment)).perform(typeText("CASH"));
        closeSoftKeyboard();

        onView(withId(R.id.signup_button)).perform(click());

        // Verify toast
        onView(withText(org.hamcrest.Matchers.containsString("Signup failed")))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Test: Empty fields → show toast "Please fill all fields"
     */
    @Test
    public void testEmptyFields() {
        onView(withId(R.id.signup_button)).perform(click());

        onView(withText("Please fill all fields"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }
}
