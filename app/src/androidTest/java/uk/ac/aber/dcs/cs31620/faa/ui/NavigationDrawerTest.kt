package uk.ac.aber.dcs.cs31620.faa.ui

import android.view.Gravity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.ac.aber.dcs.cs31620.faa.R

@RunWith(AndroidJUnit4::class)
@LargeTest
class NavigationDrawerTest {
    @Rule
    @JvmField
    var activityActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun clickOnAndroidHomeIcon_OpensNavigation(){

        // Check that left drawer is closed at startup
        Espresso.onView(withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.

        // Open Drawer
        val navigateUpDesc = activityActivityTestRule.activity
            .getString(R.string.nav_open_drawer)
        Espresso.onView(ViewMatchers.withContentDescription(navigateUpDesc))
            .perform(ViewActions.click())

        // Check if drawer is open
        Espresso.onView(withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isOpen(Gravity.LEFT))) // Left drawer is open open.
    }
}