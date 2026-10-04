package fr.papycha.htcweatherclock.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fr.papycha.htcweatherclock.R
import kotlin.math.abs
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WidgetIconPlacementTest {
    @Test fun classicIconStaysOnWeatherPanelBoundaryAtDifferentHostHeights() {
        assertAnchored(R.layout.widget_classic, R.id.weather_panel, widthDp = 360, heightDp = 190)
        assertAnchored(R.layout.widget_classic, R.id.weather_panel, widthDp = 360, heightDp = 260)
    }

    @Test fun weeklyIconStaysOnCurrentWeatherBoundaryAtDifferentHostHeights() {
        assertAnchored(R.layout.widget_weekly, R.id.current_weather_panel, widthDp = 360, heightDp = 300)
        assertAnchored(R.layout.widget_weekly, R.id.current_weather_panel, widthDp = 360, heightDp = 380)
    }

    private fun assertAnchored(layoutId: Int, panelId: Int, widthDp: Int, heightDp: Int) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = instrumentation.targetContext
            val density = context.resources.displayMetrics.density
            val root = LayoutInflater.from(context).inflate(layoutId, null)
            val width = (widthDp * density).toInt()
            val height = (heightDp * density).toInt()
            root.measure(exact(width), exact(height))
            root.layout(0, 0, width, height)

            val panel = root.findViewById<View>(panelId)
            val icon = root.findViewById<View>(R.id.weather_icon)
            val panelTop = absoluteY(panel, root)
            val iconCenter = absoluteY(icon, root) + icon.height / 2f
            val tolerance = 2f * density
            assertTrue(
                "Le centre de l’icône ($iconCenter) dérive de la séparation météo ($panelTop)",
                abs(iconCenter - panelTop) <= tolerance
            )
        }
    }

    private fun absoluteY(view: View, root: View): Float {
        var y = view.y
        var parent = view.parent
        while (parent is View && parent !== root) {
            y += parent.y
            parent = parent.parent
        }
        return y
    }

    private fun exact(size: Int): Int = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)
}
