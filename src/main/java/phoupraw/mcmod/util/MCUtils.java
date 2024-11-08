package phoupraw.mcmod.util;

import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import phoupraw.mcmod.client_auto_door.mixin.minecraft.APlayerEntity;

@ApiStatus.NonExtendable
public interface MCUtils {
    static @NotNull BlockHitResult getHitResult(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        Vec3d centerPos = pos.toCenterPos();
        Vec3d eyePos = player.getEyePos();
        Vec3d end = eyePos.lerp(centerPos, 2);
        BlockHitResult hitResult = world.raycastBlock(eyePos, end, pos, state.getRaycastShape(world, pos), state);
        if (hitResult == null) {
            hitResult = new BlockHitResult(centerPos, Direction.UP, pos, false);
        }
        return hitResult;
    }
    static float getHeight(ClientPlayerEntity self0) {
        var self = (ClientPlayerEntity & APlayerEntity) self0;
        EntityPose pose;
        if (self.invokeCanChangeIntoPose(EntityPose.SWIMMING)) {
            EntityPose pose0;
            if (self.isFallFlying()) {
                pose0 = EntityPose.FALL_FLYING;
            } else if (self.isSleeping()) {
                pose0 = EntityPose.SLEEPING;
            } else if (self.isSwimming()) {
                pose0 = EntityPose.SWIMMING;
            } else if (self.isUsingRiptide()) {
                pose0 = EntityPose.SPIN_ATTACK;
            } else if (self.isSneaking() && !self.getAbilities().flying) {
                pose0 = EntityPose.CROUCHING;
            } else {
                pose0 = EntityPose.STANDING;
            }
            if (self.isSpectator() || self.hasVehicle() || self.invokeCanChangeIntoPose(pose0)) {
                pose = pose0;
            } else if (self.invokeCanChangeIntoPose(EntityPose.CROUCHING)) {
                pose = EntityPose.CROUCHING;
            } else {
                pose = EntityPose.SWIMMING;
            }
        } else {
            pose = self.getPose();
        }
        float height1 = self.getDimensions(pose).height();
        return height1;
    }
}
