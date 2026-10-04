package fr.papycha.htcweatherclock

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashboardLayoutTest {
    @Test fun dashboardDoesNotExposeClockOrAlarmMenu() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                listOf(R.string.clock_section, R.string.open_alarms, R.string.add_alarm).forEach { textId ->
                    val matchingViews = arrayListOf<View>()
                    activity.findViewById<View>(R.id.dashboard_root).findViewsWithText(
                        matchingViews,
                        activity.getString(textId),
                        View.FIND_VIEWS_WITH_TEXT
                    )
                    assertTrue("Le menu horloge/alarmes est encore visible", matchingViews.isEmpty())
                }
            }
        }
    }
}
