package phoupraw.mcmod.client_auto_door.mixins.minecraft;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.client_auto_door.events.DoorToggler;
import phoupraw.mcmod.client_auto_door.misc.DoorOpening;
import phoupraw.mcmod.client_auto_door.mixin.minecraft.AEntity;

import java.util.Collection;

@Environment(EnvType.CLIENT)
@ApiStatus.Internal
public interface MMClientPlayerEntity {
    static void openDoor(ClientPlayerEntity self, MovementType movementType, Vec3d movement, MinecraftClient client) {
        //Input input = self.input;
        //if (input==null)return;
        if (movementType != MovementType.SELF /*|| self.hasVehicle()*/) return;
        //ClientPlayerInteractionManager interactor = client.interactionManager;
        //if (interactor == null) return;
        var vehicle = (Entity & AEntity) self.getRootVehicle();
        if (!DoorOpening.shouldAct(client, self, vehicle)) return;
        Vec3d movement1 = movement.getY() >= 0 ? movement : new Vec3d(movement.getX(), 0, movement.getZ());
        World world = self.getWorld();
        Box vehicleBox = vehicle.getBoundingBox();
        //ShapeContext shapeContext = ShapeContext.of(vehicle);
        //class MovementSimulator extends SnapshotParticipant<Vec3d> {
        //    @Override
        //    protected Vec3d createSnapshot() {
        //        return vehicle.getPos();
        //    }
        //    @Override
        //    protected void readSnapshot(Vec3d snapshot) {
        //        vehicle.setPosition(snapshot);
        //    }
        //    public void move(Vec3d movement) {
        //
        //    }
        //    @Override
        //    protected void onFinalCommit() {
        //        super.onFinalCommit();
        //
        //    }
        //}
        try (var t = Transaction.openOuter()) {
            var vehicleShape = VoxelShapes.cuboid(vehicleBox);
            Collection<VoxelShape> shapes = new ObjectArrayList<>();
            for (var iter = new BlockCollisionSpliterator<>(world, vehicle, vehicleBox.stretch(movement1), false, Pair::of); iter.hasNext(); ) {
                var pair = iter.next();
                VoxelShape shape = pair.second();
                if (VoxelShapes.matchesAnywhere(shape, vehicleShape, BooleanBiFunction.AND)) {
                    continue;
                }
                BlockPos pos = pair.first().toImmutable();
                BlockState state = world.getBlockState(pos);
                DoorToggler toggler = DoorToggler.LOOKUP.find(world, pos, state, null, vehicle);
                if (toggler == null) continue;
                if (toggler.toggleDoor(self, t).isEmpty()) {
                    return;
                }
                shapes.add(shape);
            }
            var adjusted = VoxelShapes.cuboid(vehicleBox.stretch(vehicle.invokeAdjustMovementForCollisions(movement)));
            for (var shape : shapes) {
                if (!VoxelShapes.matchesAnywhere(shape, adjusted, BooleanBiFunction.AND)) {
                    return;
                }
            }
            t.commit();
        }
        //try (var t = Transaction.openOuter()) {
        //    for (BlockPos pos : BlockPos.iterate(
        //      (int) Math.floor(stretched.minX) - 1,
        //      (int) Math.floor(stretched.minY) - 1,
        //      (int) Math.floor(stretched.minZ) - 1,
        //      (int) Math.ceil(stretched.maxX) + 1,
        //      (int) Math.ceil(stretched.maxY) + 1,
        //      (int) Math.ceil(stretched.maxZ) + 1)
        //    ) {
        //        BlockState state = world.getBlockState(pos);
        //        VoxelShape shape = state.getCollisionShape(world, pos, shapeContext);
        //
        //        if (world.canCollide(vehicle, stretched)) {
        //
        //        }
        //        DoorToggler toggler = DoorToggler.LOOKUP.find(world, pos, state, null, vehicle);
        //        if (toggler == null) continue;
        //
        //    }
        //}
        //DoorOpening.openDoor(vehicle, movement, client, vehicle instanceof LivingEntity living ? living.getBoundingBox(EntityPose.SWIMMING).offset(self.getPos()) : self.getBoundingBox(), self);
    }
    
}
