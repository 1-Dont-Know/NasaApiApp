package com.example.nasaapiapp

import android.view.KeyEvent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressKey
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers.containsString
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals




import org.junit.Test

class MainActivityTest {
    @Test
    fun searchViewTest() {
        // Launch MainActivity
        ActivityScenario.launch(MainActivity::class.java)

        // Tap on search bar to bring up keyboard
        onView(withId(R.id.searchView)).perform(click())



        // Enter text into search bar
        onView(withId(R.id.searchView)).perform(typeText("moon"), pressKey(KeyEvent.KEYCODE_ENTER))
        // Wait for results to update
        Thread.sleep(2000)
        // Check that results are updated
        onView(withId(R.id.userList)).check(matches(hasDescendant(withText(containsString("Moon")))))
    }

    @Test
    fun nextButtonTest() {
        // Launch MainActivity
        ActivityScenario.launch(MainActivity::class.java)

        // Tap on search bar to bring up keyboard
        onView(withId(R.id.searchView)).perform(click())

        // Enter text into search bar
        onView(withId(R.id.searchView)).perform(typeText("moon"), pressKey(KeyEvent.KEYCODE_ENTER))

        // Wait for results to update
        Thread.sleep(2000)

        // Check that results are updated
        onView(withId(R.id.userList)).check(matches(hasDescendant(withText(containsString("Moon")))))

        // Click on next button
        onView(withId(R.id.next_button)).perform(click())

        // Wait for results to update
        Thread.sleep(2000)

        // Check that results are updated
        onView(withId(R.id.userList)).check(matches(isDisplayed()))
    }

    @Test
    fun prevButtonTest() {
        // Launch MainActivity
        ActivityScenario.launch(MainActivity::class.java)

        // Tap on search bar to bring up keyboard
        onView(withId(R.id.searchView)).perform(click())

        // Enter text into search bar
        onView(withId(R.id.searchView)).perform(typeText("moon"), pressKey(KeyEvent.KEYCODE_ENTER))

        // Wait for results to update
        Thread.sleep(2000)

        // Check that results are updated
        onView(withId(R.id.userList)).check(matches(hasDescendant(withText(containsString("Moon")))))

        // Check that prev button is disabled
        onView(withId(R.id.prev_button)).check(matches(not(isEnabled())))
    }


    class MainActivityTest {
        @Test
        fun searchViewTest() {
            // Launch MainActivity
            ActivityScenario.launch(MainActivity::class.java)

            // Tap on search bar to bring up keyboard
            onView(withId(R.id.searchView)).perform(click())



            // Enter text into search bar
            onView(withId(R.id.searchView)).perform(typeText("moon"), pressKey(KeyEvent.KEYCODE_ENTER))
            // Wait for results to update
            Thread.sleep(2000)
            // Check that results are updated
            onView(withId(R.id.userList)).check(matches(hasDescendant(withText(containsString("Moon")))))
        }

        @Test
        fun nextButtonTest() {
            // Launch MainActivity
            ActivityScenario.launch(MainActivity::class.java)

            // Tap on search bar to bring up keyboard
            onView(withId(R.id.searchView)).perform(click())

            // Enter text into search bar
            onView(withId(R.id.searchView)).perform(typeText("moon"), pressKey(KeyEvent.KEYCODE_ENTER))

            // Wait for results to update
            Thread.sleep(2000)

            // Check that results are updated
            onView(withId(R.id.userList)).check(matches(hasDescendant(withText(containsString("Moon")))))

            // Click on next button
            onView(withId(R.id.next_button)).perform(click())

            // Wait for results to update
            Thread.sleep(2000)

            // Check that results are updated
            onView(withId(R.id.userList)).check(matches(isDisplayed()))
        }

        @Test
        fun prevButtonTest() {
            // Launch MainActivity
            ActivityScenario.launch(MainActivity::class.java)

            // Tap on search bar to bring up keyboard
            onView(withId(R.id.searchView)).perform(click())

            // Enter text into search bar
            onView(withId(R.id.searchView)).perform(typeText("moon"), pressKey(KeyEvent.KEYCODE_ENTER))

            // Wait for results to update
            Thread.sleep(2000)

            // Check that results are updated
            onView(withId(R.id.userList)).check(matches(hasDescendant(withText(containsString("Moon")))))

            // Check that prev button is disabled
            onView(withId(R.id.prev_button)).check(matches(not(isEnabled())))
        }
    }

    @Test
    fun testNextPrevButtons() {
        // Launch MainActivity
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        // Type "mars" into search bar
        onView(withId(R.id.searchView)).perform(typeText("mars"), pressKey(KeyEvent.KEYCODE_ENTER))

        // Wait for results to update
        Thread.sleep(2000)

        // Get current page number
        var currentPage = 0
        scenario.onActivity { activity ->
            currentPage = activity.displayedPage
        }

        // Click on next button
        onView(withId(R.id.next_button)).perform(click())

        // Wait for results to update
        Thread.sleep(2000)

        // Check that page number has increased by 1
        scenario.onActivity { activity ->
            assertEquals(currentPage + 1, activity.displayedPage)
            currentPage = activity.displayedPage
        }

        // Click on prev button
        onView(withId(R.id.prev_button)).perform(click())

        // Wait for results to update
        Thread.sleep(2000)

        // Check that page number has decreased by 1
        scenario.onActivity { activity ->
            assertEquals(currentPage - 1, activity.displayedPage)
            currentPage = activity.displayedPage
        }
    }

}