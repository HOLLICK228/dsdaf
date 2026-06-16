package xd.cybikpvp.untitled.client.gui

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import java.awt.Color

object WorldTweaksState {
    var enabled: Boolean = false
    var customTime: Long = 6000L
    var timeEnabled: Boolean = false
}

class ClickGUIMenu : Screen(Text.literal("ClickGUI")) {

    private var alphaProgress = 0f
    private var isClosing = false

    private val menuWidth  = 220
    private val menuHeight = 140
    private val cornerRadius = 8

    private val panelW = 200
    private val panelH = 110
    private val panelGap = 8

    private var sliderDragging = false
    private val snowParticles = mutableListOf<SnowParticle>()

    init {
        repeat(60) { snowParticles.add(SnowParticle()) }
    }

    private fun easeOutCubic(t: Float): Float {
        val c = (t - 1f); return 1f + c * c * c
    }

    override fun renderBackground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {}

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val mc = client ?: return
        val ww = mc.window.scaledWidth
        val wh = mc.window.scaledHeight

        val speed = delta * 0.12f
        if (isClosing) {
            alphaProgress = (alphaProgress - speed).coerceAtLeast(0f)
            if (alphaProgress <= 0f) { super.close(); return }
        } else {
            alphaProgress = (alphaProgress + speed).coerceAtMost(1f)
        }

        val ease = easeOutCubic(alphaProgress)

        context.fill(0, 0, ww, wh, Color(0, 0, 0, (140 * ease).toInt()).rgb)

        snowParticles.forEach { it.update(delta); it.render(context, ww, wh, (100 * ease).toInt()) }

        val menuX = (ww - menuWidth) / 2
        val menuY = (wh - menuHeight) / 2

        drawRoundedRect(context, menuX, menuY, menuX + menuWidth, menuY + menuHeight, cornerRadius,
            fadeColor(14, 14, 14, 255))
        drawRoundedRect(context, menuX, menuY, menuX + menuWidth, menuY + 26, cornerRadius,
            fadeColor(10, 10, 10, 255))
        context.fill(menuX, menuY + 18, menuX + menuWidth, menuY + 26, fadeColor(10, 10, 10, 255).rgb)

        val title = "World Tweaks"
        val tw = textRenderer.getWidth(title)
        context.drawText(textRenderer, title,
            menuX + menuWidth / 2 - tw / 2, menuY + 9,
            fadeColor(200, 200, 200, 255).rgb, false)

        context.fill(menuX + 10, menuY + 26, menuX + menuWidth - 10, menuY + 27,
            fadeColor(40, 40, 40, 255).rgb)

        val enabledLabel = "WorldTweaks"
        context.drawText(textRenderer, enabledLabel, menuX + 14, menuY + 38,
            fadeColor(200, 200, 200, 255).rgb, false)
        drawToggle(context,
            menuX + menuWidth - 46, menuY + 34,
            WorldTweaksState.enabled, ease)

        context.drawText(textRenderer, "Time Control", menuX + 14, menuY + 68,
            fadeColor(180, 180, 180, 255).rgb, false)
        drawToggle(context,
            menuX + menuWidth - 46, menuY + 64,
            WorldTweaksState.timeEnabled, ease)

        val sliderX1 = menuX + 14
        val sliderX2 = menuX + menuWidth - 14
        val sliderY  = menuY + 96
        drawSlider(context, sliderX1, sliderX2, sliderY, ease)

        val hours = (WorldTweaksState.customTime / 1000f + 6f) % 24f
        val minutes = ((WorldTweaksState.customTime % 1000) / 1000f * 60).toInt()
        val timeLabel = "%02d:%02d".format(hours.toInt(), minutes)
        val tlw = textRenderer.getWidth(timeLabel)
        context.drawText(textRenderer, timeLabel,
            menuX + menuWidth / 2 - tlw / 2, sliderY + 14,
            fadeColor(140, 140, 140, 255).rgb, false)

        renderRightPanel(context, menuX, menuY, ease)

        super.render(context, mouseX, mouseY, delta)
    }

    private fun renderRightPanel(context: DrawContext, menuX: Int, menuY: Int, ease: Float) {
        val px = menuX + menuWidth + panelGap
        val py = menuY
        val pw = panelW
        val ph = panelH

        drawRoundedRect(context, px, py, px + pw, py + ph, cornerRadius,
            fadeColor(14, 14, 14, 255))
        drawRoundedRect(context, px, py, px + pw, py + 26, cornerRadius,
            fadeColor(10, 10, 10, 255))
        context.fill(px, py + 18, px + pw, py + 26, fadeColor(10, 10, 10, 255).rgb)

        val pTitle = "Time of Day"
        val ptw = textRenderer.getWidth(pTitle)
        context.drawText(textRenderer, pTitle,
            px + pw / 2 - ptw / 2, py + 9,
            fadeColor(200, 200, 200, 255).rgb, false)

        context.fill(px + 10, py + 26, px + pw - 10, py + 27,
            fadeColor(40, 40, 40, 255).rgb)

        val hours = (WorldTweaksState.customTime / 1000f + 6f) % 24f
        val minutes = ((WorldTweaksState.customTime % 1000) / 1000f * 60).toInt()
        val bigTime = "%02d:%02d".format(hours.toInt(), minutes)
        val btw = textRenderer.getWidth(bigTime)
        context.drawText(textRenderer, bigTime,
            px + pw / 2 - btw / 2, py + 36,
            fadeColor(160, 220, 255, 255).rgb, false)

        val period = when (hours.toInt()) {
            in 5..7   -> "Рассвет"
            in 8..11  -> "Утро"
            in 12..16 -> "День"
            in 17..19 -> "Закат"
            in 20..23 -> "Ночь"
            else      -> "Полночь"
        }
        val pw2 = textRenderer.getWidth(period)
        context.drawText(textRenderer, period,
            px + panelW / 2 - pw2 / 2, py + 52,
            fadeColor(120, 120, 120, 255).rgb, false)

        val barX1 = px + 14
        val barX2 = px + pw - 14
        val barY  = py + 72
        context.fill(barX1, barY, barX2, barY + 6, fadeColor(30, 30, 30, 255).rgb)
        val fraction = (WorldTweaksState.customTime / 24000f).coerceIn(0f, 1f)
        val dayColor = when {
            hours < 6 || hours >= 20  -> fadeColor(40, 60, 120, 255)
            hours < 8 || hours >= 18  -> fadeColor(255, 160, 60, 255)
            else                       -> fadeColor(80, 160, 255, 255)
        }
        val fillX = barX1 + ((barX2 - barX1) * fraction).toInt()
        context.fill(barX1, barY, fillX, barY + 6, dayColor.rgb)
        drawRoundedRect(context, fillX - 3, barY - 2, fillX + 3, barY + 8, 2,
            fadeColor(220, 230, 255, 255))

        val hint = if (WorldTweaksState.timeEnabled) "Кастомное время активно" else "Время отключено"
        val hw = textRenderer.getWidth(hint)
        context.drawText(textRenderer, hint,
            px + pw / 2 - hw / 2, barY + 14,
            if (WorldTweaksState.timeEnabled) fadeColor(100, 200, 120, 255).rgb else fadeColor(80, 80, 80, 255).rgb,
            false)
    }

    private fun drawToggle(context: DrawContext, x: Int, y: Int, on: Boolean, ease: Float) {
        val bgColor = if (on) fadeColor(80, 180, 100, 255) else fadeColor(55, 55, 55, 255)
        drawRoundedRect(context, x, y, x + 32, y + 16, 8, bgColor)
        val knobX = if (on) x + 18 else x + 2
        drawRoundedRect(context, knobX, y + 2, knobX + 12, y + 14, 6, fadeColor(230, 230, 230, 255))
    }

    private fun drawSlider(context: DrawContext, x1: Int, x2: Int, y: Int, ease: Float) {
        context.fill(x1, y + 3, x2, y + 7, fadeColor(40, 40, 40, 255).rgb)
        val fraction = (WorldTweaksState.customTime / 24000f).coerceIn(0f, 1f)
        val thumbX = x1 + ((x2 - x1) * fraction).toInt()
        context.fill(x1, y + 3, thumbX, y + 7, fadeColor(80, 160, 255, 255).rgb)
        drawRoundedRect(context, thumbX - 5, y - 1, thumbX + 5, y + 11, 3,
            fadeColor(200, 220, 255, 255))
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (isClosing || alphaProgress < 0.3f) return false
        val mc = client ?: return false
        val ww = mc.window.scaledWidth; val wh = mc.window.scaledHeight
        val menuX = (ww - menuWidth) / 2; val menuY = (wh - menuHeight) / 2

        val mx = mouseX.toInt(); val my = mouseY.toInt()

        if (mx in (menuX + menuWidth - 46)..(menuX + menuWidth - 14) &&
            my in (menuY + 34)..(menuY + 50)) {
            WorldTweaksState.enabled = !WorldTweaksState.enabled
            return true
        }

        if (mx in (menuX + menuWidth - 46)..(menuX + menuWidth - 14) &&
            my in (menuY + 64)..(menuY + 80)) {
            WorldTweaksState.timeEnabled = !WorldTweaksState.timeEnabled
            return true
        }

        val sliderX1 = menuX + 14; val sliderX2 = menuX + menuWidth - 14; val sliderY = menuY + 96
        if (my in (sliderY - 4)..(sliderY + 15)) {
            val frac = ((mouseX - sliderX1) / (sliderX2 - sliderX1)).coerceIn(0.0, 1.0)
            WorldTweaksState.customTime = (frac * 24000).toLong()
            sliderDragging = true
            return true
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dx: Double, dy: Double): Boolean {
        if (sliderDragging) {
            val mc = client ?: return true
            val menuX = (mc.window.scaledWidth - menuWidth) / 2
            val sliderX1 = menuX + 14; val sliderX2 = menuX + menuWidth - 14
            val frac = ((mouseX - sliderX1) / (sliderX2 - sliderX1)).coerceIn(0.0, 1.0)
            WorldTweaksState.customTime = (frac * 24000).toLong()
            return true
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        sliderDragging = false
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close(); return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun close() { isClosing = true }
    override fun shouldPause() = false

    private fun fadeColor(r: Int, g: Int, b: Int, maxA: Int = 255): Color {
        val a = (maxA * alphaProgress).toInt().coerceIn(0, 255)
        return Color(r, g, b, a)
    }

    private fun drawRoundedRect(context: DrawContext, x: Int, y: Int, x2: Int, y2: Int, r: Int, color: Color) {
        val rgb = color.rgb
        if (x2 - x < r * 2 || y2 - y < r * 2) { context.fill(x, y, x2, y2, rgb); return }
        context.fill(x + r, y, x2 - r, y2, rgb)
        context.fill(x, y + r, x + r, y2 - r, rgb)
        context.fill(x2 - r, y + r, x2, y2 - r, rgb)
        for (i in 0 until r) for (j in 0 until r) {
            if (i * i + j * j <= r * r) {
                context.fill(x + r - i - 1, y + r - j - 1, x + r - i, y + r - j, rgb)
                context.fill(x2 - r + i, y + r - j - 1, x2 - r + i + 1, y + r - j, rgb)
                context.fill(x + r - i - 1, y2 - r + j, x + r - i, y2 - r + j + 1, rgb)
                context.fill(x2 - r + i, y2 - r + j, x2 - r + i + 1, y2 - r + j + 1, rgb)
            }
        }
    }
}

class SnowParticle {
    var x = (Math.random() * 1920).toFloat()
    var y = (Math.random() * 1080).toFloat()
    var speed = (Math.random() * 1.0 + 0.3).toFloat()
    var size = (Math.random() * 1.4 + 0.8).toFloat()
    var drift = (Math.random() * 0.6 - 0.3).toFloat()

    fun update(delta: Float) {
        y += speed * delta * 38f
        x += drift * delta * 5f
        if (y > 1082) { y = -8f; x = (Math.random() * 1920).toFloat() }
        if (x > 1921) x = 0f
        if (x < -1) x = 1920f
    }

    fun render(context: DrawContext, ww: Int, wh: Int, baseAlpha: Int) {
        val sx = (x / 1920 * ww).toInt()
        val sy = (y / 1080 * wh).toInt()
        val sz = size.toInt().coerceAtLeast(1)
        context.fill(sx, sy, sx + sz, sy + sz, Color(255, 255, 255, baseAlpha).rgb)
    }
}
