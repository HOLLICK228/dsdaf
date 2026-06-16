package xd.cybikpvp.untitled.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import org.slf4j.LoggerFactory
import xd.cybikpvp.untitled.client.gui.ClickGUIMenu

private val logger = LoggerFactory.getLogger("ClickGUI")

object UntitledClient : ClientModInitializer {

    override fun onInitializeClient() {
        logger.info("Initializing ClickGUI Client")
        
        KeyBindingHelper.registerKeyBinding(KeyBindingHandler.openMenuKey)

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            while (KeyBindingHandler.openMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(ClickGUIMenu())
                } else if (client.currentScreen is ClickGUIMenu) {
                    client.setScreen(null)
                }
            }
        }
    }
}
