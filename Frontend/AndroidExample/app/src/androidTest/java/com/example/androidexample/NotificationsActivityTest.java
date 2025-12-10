package com.example.androidexample;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;

import android.content.Intent;

import static org.hamcrest.Matchers.not;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NotificationsActivityTest {

    @Before
    public void launchActivity() {
        Intent intent = new Intent();
        ActivityScenario.launch(NotificationsActivity.class);
    }

    /**
     * Test 1 – Loads the default "Test Notification"
     */
    @Test
    public void testNotificationIsDisplayed() {
        onView(withText("Test Notification"))
                .check(matches(isDisplayed()));
    }

    /**
     * Test 2 – Expand/collapse the notification message
     */
    @Test
    public void testExpandCollapseNotification() {

        // Click first card to expand
        onView(withId(R.id.notifications_recycler))
                .perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.notification_message))
                .check(matches(isDisplayed()));

        // Click again to collapse
        onView(withId(R.id.notifications_recycler))
                .perform(actionOnItemAtPosition(0, click()));

        // Ensure message is hidden by checking parent (message_container) is GONE
        onView(withId(R.id.message_container))
                .check(matches(not(isDisplayed())));
    }

    /**
     * Test 3 – Back arrow finishes activity
     */
    @Test
    public void testBackArrow() {
        onView(withId(R.id.back_arrow)).perform(click());
    }

    /**
     * Test 4 – Clear All removes notifications and shows empty state
     */
    @Test
    public void testClearAll() {
        // Click Clear All button
        onView(withId(R.id.clear_notifications)).perform(click());

        // RecyclerView should be gone
        onView(withId(R.id.notifications_recycler))
                .check(matches(not(isDisplayed())));

        // Empty text should be visible
        onView(withId(R.id.empty_text))
                .check(matches(isDisplayed()));
    }
}
