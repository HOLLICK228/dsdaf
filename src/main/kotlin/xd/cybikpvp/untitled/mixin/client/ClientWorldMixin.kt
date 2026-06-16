package xd.cybikpvp.untitled.mixin.client

import net.minecraft.client.world.ClientWorld
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import xd.cybikpvp.untitled.client.gui.WorldTweaksState

@Mixin(ClientWorld::class)
class ClientWorldMixin {

    @Inject(
        method = ["getTimeOfDay"],
        at = [At("RETURN")],
        cancellable = true
    )
    private fun onGetTimeOfDay(cir: CallbackInfoReturnable<Long>) {
        if (WorldTweaksState.enabled && WorldTweaksState.timeEnabled) {
            cir.returnValue = WorldTweaksState.customTime
        }
    }
}
