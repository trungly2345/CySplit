package com.example.androidexample;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    @Before
    public void setup() {
        ActivityScenario.launch(LoginActivity.class);
    }

    @Test
    public void testSuccessfulLogin_realBackend() throws Exception {
        String username = "testuser3";
        String password = "securepass123";

        onView(withId(R.id.login_username))
                .perform(typeText(username), closeSoftKeyboard());

        onView(withId(R.id.login_password))
                .perform(typeText(password), closeSoftKeyboard());

        onView(withId(R.id.login_button)).perform(click());

        Thread.sleep(3500);

        onView(withId(R.id.main_msg_txt))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testFailedLogin_realBackend() throws Exception {
        onView(withId(R.id.login_username))
                .perform(typeText("wrongusername123"), closeSoftKeyboard());

        onView(withId(R.id.login_password))
                .perform(typeText("wrongpass123"), closeSoftKeyboard());

        onView(withId(R.id.login_button)).perform(click());

        Thread.sleep(2500);

        onView(withId(R.id.login_button))
                .check(matches(isDisplayed()));
    }
}
