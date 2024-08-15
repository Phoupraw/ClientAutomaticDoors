package phoupraw.mcmod.client_auto_door.mixin.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phoupraw.mcmod.client_auto_door.mixins.minecraft.MMEntity;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
abstract class MEntity {
    @Inject(method = "move", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;wasOnFire:Z", opcode = Opcodes.PUTFIELD))
    private void openDoor(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        MMEntity.openDoor((Entity) (Object) this, movementType, movement);
    }
}
