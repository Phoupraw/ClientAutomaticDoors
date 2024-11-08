package phoupraw.mcmod.client_auto_door.mixin.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phoupraw.mcmod.client_auto_door.modules.Attachment;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
abstract class MClientWorld {
    @Inject(method = "playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZJ)V", at = @At("RETURN"), cancellable = true)
    private void simulate(CallbackInfo ci) {
        if (Attachment.simulation) {
            ci.cancel();
        }
    }
}
