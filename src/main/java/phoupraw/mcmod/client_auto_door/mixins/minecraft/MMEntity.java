package phoupraw.mcmod.client_auto_door.mixins.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import phoupraw.mcmod.client_auto_door.misc.DoorOpening;

@Environment(EnvType.CLIENT)
public interface MMEntity {
    static void openDoor(Entity self, MovementType movementType, Vec3d movement) {
        if (self.isPlayer() || !(movementType == MovementType.SELF || movementType == MovementType.PLAYER) || !self.hasPlayerRider()) {
            return;
        }
        ClientPlayerEntity player = null;
        for (Entity rider : self.getPassengersDeep()) {
            if (rider instanceof ClientPlayerEntity playerRider) {
                player = playerRider;
                break;
            }
        }
        if (player != null) {
            DoorOpening.openDoor(self, movement, MinecraftClient.getInstance(), self instanceof LivingEntity living ? living.getBoundingBox(EntityPose.SWIMMING).offset(self.getPos()) : self.getBoundingBox(), player);
        }
    }
}
