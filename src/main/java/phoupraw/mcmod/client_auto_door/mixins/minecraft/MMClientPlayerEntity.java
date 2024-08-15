package phoupraw.mcmod.client_auto_door.mixins.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import phoupraw.mcmod.client_auto_door.misc.DoorOpening;

@Environment(EnvType.CLIENT)
public interface MMClientPlayerEntity {
    static void openDoor(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client) {
        if (movementType != MovementType.SELF || self.hasVehicle()) return;
        DoorOpening.openDoor(self, movement, client, self.getBoundingBox(EntityPose.SWIMMING).offset(self.getPos()), self);
    }
}
