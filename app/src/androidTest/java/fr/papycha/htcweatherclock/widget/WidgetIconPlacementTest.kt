package fr.papycha.htcweatherclock.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    @Test fun compactWeeklyIconStaysOnCurrentWeatherBoundaryAtThreeCellHeights() {
        assertAnchored(R.layout.widget_weekly_compact, R.id.current_weather_panel, widthDp = 360, heightDp = 180)
        assertAnchored(R.layout.widget_weekly_compact, R.id.current_weather_panel, widthDp = 360, heightDp = 230)
    }

    @Test fun compactWeeklyKeepsAllFourForecastColumnsInsideTheWidget() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = instrumentation.targetContext
            val density = context.resources.displayMetrics.density
            val root = LayoutInflater.from(context).inflate(R.layout.widget_weekly_compact, null)
            val width = (360 * density).toInt()
            val height = (180 * density).toInt()
            root.measure(exact(width), exact(height))
            root.layout(0, 0, width, height)

            val current = root.findViewById<View>(R.id.current_weather_panel)
            val forecast = root.findViewById<View>(R.id.forecast_panel)
            assertTrue("Les panneaux météo compacts doivent avoir une hauteur utile", current.height > 0 && forecast.height > 0)
            assertTrue("Les panneaux météo compacts se chevauchent", absoluteY(forecast, root) >= absoluteY(current, root) + current.height)
            assertTrue("La prévision compacte dépasse du widget", absoluteY(forecast, root) + forecast.height <= root.height)
            intArrayOf(R.id.day_1, R.id.day_2, R.id.day_3, R.id.day_4).forEach { id ->
                assertTrue("Une colonne de prévision compacte est écrasée", root.findViewById<View>(id).width > 0)
            }
        }
    }

    @Test fun smallClassicDigitsKeepTheLargeClockVisualScale() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val context = instrumentation.targetContext
            val density = context.resources.displayMetrics.density
            val scaledDensity = context.resources.displayMetrics.scaledDensity
            val root = LayoutInflater.from(context).inflate(R.layout.widget_compact, null)
            val width = (200 * density).toInt()
            val height = (180 * density).toInt()
            root.measure(exact(width), exact(height))
            root.layout(0, 0, width, height)
            val hour = root.findViewById<TextView>(R.id.hour_panel)
            assertTrue("Les chiffres du petit widget sont encore sous-dimensionnés", hour.textSize >= 54f * scaledDensity)
            assertTrue("Les chiffres du petit widget sont rognés", hour.paint.measureText("88") < hour.width)
        }
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
