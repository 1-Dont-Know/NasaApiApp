package com.example.nasaapiapp
import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class MainActivityUnitTest {
    @Test
    fun testFetchData() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
        val query = "mars"
        activity.fetchData(query)
        assertEquals(query, activity.currentQuery)
    }
}