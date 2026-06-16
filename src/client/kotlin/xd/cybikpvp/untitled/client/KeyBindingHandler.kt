package xd.cybikpvp.untitled.client

import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

object KeyBindingHandler {

    val openMenuKey = KeyBinding(
        "key.untitled.open_menu",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_RIGHT_SHIFT,
        "category.untitled"
    )
}
