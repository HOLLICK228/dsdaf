package xd.cybikpvp.untitled.client

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("UntitledDataGen")

object UntitledDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        logger.info("Initializing ClickGUI Data Generator")
        fabricDataGenerator.createPack()
    }
}
