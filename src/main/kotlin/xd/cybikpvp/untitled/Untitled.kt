package xd.cybikpvp.untitled

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

const val MOD_ID = "untitled"
const val MOD_NAME = "ClickGUI Mod"

private val logger = LoggerFactory.getLogger(MOD_NAME)

object Untitled : ModInitializer {
    override fun onInitialize() {
        logger.info("Initializing $MOD_NAME")
    }
}
