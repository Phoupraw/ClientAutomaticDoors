package phoupraw.mcmod.client_auto_door.mixins.minecraft;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectLongPair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.MovementType;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.client_auto_door.events.BlockShapeToggler;
import phoupraw.mcmod.client_auto_door.misc.DoorOpening;
import phoupraw.mcmod.client_auto_door.mixin.minecraft.AEntity;
import phoupraw.mcmod.client_auto_door.mixin.minecraft.APlayerEntity;

import java.util.Collection;
import java.util.Map;

@Environment(EnvType.CLIENT)
@ApiStatus.Internal
public interface MMClientPlayerEntity {
    Map<BlockPos, ObjectLongPair<BlockState>> OPENEDS = new Object2ObjectRBTreeMap<>(DoorOpening.COMPARATOR);
    static void openDoor(ClientPlayerEntity self0, MovementType movementType, Vec3d movement, MinecraftClient client) {
        //Input input = self.input;
        //if (input==null)return;
        if (movementType != MovementType.SELF /*|| self.hasVehicle()*/) return;
        var self = (ClientPlayerEntity & APlayerEntity) self0;
        //ClientPlayerInteractionManager interactor = client.interactionManager;
        //if (interactor == null) return;
        var vehicle = (Entity & AEntity) self.getRootVehicle();
        if (!DoorOpening.shouldAct(client, self, vehicle)) return;
        Vec3d movement1 = movement.getY() >= 0 ? movement : new Vec3d(movement.getX(), 0, movement.getZ());
        World world = self.getWorld();
        Box vehicleBox = vehicle.getBoundingBox();
        try (var t = Transaction.openOuter()) {
            var vehicleShape = VoxelShapes.cuboid(vehicleBox);
            Map<BlockPos, VoxelShape> shapes = new Object2ObjectOpenHashMap<>();
            float height = self.getHeight();
            for (var iter = new BlockCollisionSpliterator<>(world, vehicle, vehicleBox.stretch(movement1), false, Pair::of); iter.hasNext(); ) {
                var pair = iter.next();
                VoxelShape shape = pair.second();
                if (VoxelShapes.matchesAnywhere(shape, vehicleShape, BooleanBiFunction.AND)) {
                    continue;
                }
                BlockPos pos = pair.first().toImmutable();
                BlockState state = world.getBlockState(pos);
                BlockShapeToggler toggler = BlockShapeToggler.LOOKUP.find(world, pos, state, null, vehicle);
                if (toggler == null) continue;
                open:
                try (var t2 = t.openNested()) {
                    Collection<BlockPos> positions = toggler.toggle(self, t2);
                    if (positions.isEmpty()) return;
                    for (BlockPos pos1 : positions) {
                        for (Direction direction : RedstoneView.DIRECTIONS) {
                            BlockPos pos2 = pos1.offset(direction);
                            if (!world.getBlockState(pos2).canPlaceAt(world, pos2)) {
                                break open;
                            }
                        }
                    }
                    t2.commit();
                    shapes.put(pos, shape);
                }
            }
            if (vehicle == self && height > getHeight(self)) {
                return;
            }
            var adjusted = VoxelShapes.cuboid(vehicleBox.stretch(vehicle.invokeAdjustMovementForCollisions(movement)));
            for (var shape : shapes.values()) {
                if (!VoxelShapes.matchesAnywhere(shape, adjusted, BooleanBiFunction.AND)) {
                    return;
                }
            }
            t.commit();
            for (BlockPos pos : shapes.keySet()) {
                OPENEDS.put(pos, ObjectLongPair.of(world.getBlockState(pos), world.getTime()));
            }
        }
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
