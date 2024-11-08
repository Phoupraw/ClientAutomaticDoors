package phoupraw.mcmod.client_auto_door.misc;

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import phoupraw.mcmod.client_auto_door.config.CADConfigs;
import phoupraw.mcmod.client_auto_door.events.DoorToggler;
import phoupraw.mcmod.client_auto_door.events.ToggledBlockState;
import phoupraw.mcmod.trilevel_config.api.ClientConfigs;

import java.util.Comparator;
import java.util.Map;

@Environment(EnvType.CLIENT)
public interface DoorOpening {
    Map<BlockPos, BlockState> OPENED = Object2ObjectMaps.synchronize(new Object2ObjectRBTreeMap<>(Comparator
      .comparingInt(Vec3i::getY)
      .thenComparingInt(Vec3i::getX)
      .thenComparingInt(Vec3i::getZ)));
    @ApiStatus.Internal
    static void openDoor(Entity entity, Vec3d movement, MinecraftClient client, Box swimming, ClientPlayerEntity player) {
        if (!shouldAct(client, player, entity)) {
            return;
        }
        ClientPlayerInteractionManager interactor = client.interactionManager;
        if (interactor == null) return;
        World world = entity.getWorld();
        Box entityBox = entity.getBoundingBox();
        Vec3d adjusted = movement;//entity.adjustMovementForCollisions(movement);
        Box stretched = entityBox.stretch(adjusted);
        ShapeContext shapeContext = ShapeContext.of(entity);
        //for (var iterator = DoorOpening.OPENED.entrySet().iterator(); iterator.hasNext(); ) {
        //    var entry = iterator.next();
        //    BlockPos pos = entry.getKey();
        //    BlockState state = entry.getValue();
        //    if (!world.getBlockState(pos).equals(state)) {
        //        iterator.remove();
        //        continue;
        //    }
        //    BlockState toggledState = ToggledBlockState.invoke(world, pos, state, player);
        //    if (toggledState.isAir()) {
        //        iterator.remove();
        //        continue;
        //    }
        //    if (entity.getBlockPos().equals(pos) && state.getBlock() instanceof TrapdoorBlock && entity instanceof LivingEntity living && living.isClimbing()) {
        //        continue;//防止爬活板门梯子时把活板门关上导致爬不上去
        //    }
        //    VoxelShape usedShape = toggledState.getCollisionShape(world, pos, shapeContext);
        //    boolean inBlock = entity == player && (doesCollide(usedShape, pos, swimming) || world.getBlockCollisions(entity, entityBox).iterator().hasNext());//防止把头部的门关了导致玩家的空间被挤压而使站姿变为泳姿（爬行姿态）
        //    if ((inBlock || !doesCollide(usedShape, pos, stretched)) && use(player, pos, eyePos, world, state, interactor)) {
        //        iterator.remove();
        //    }
        //}
        //Map<BlockPos, Pair<BlockState,BlockState>> collisions = new Object2ObjectOpenHashMap<>();
        try (var t = Transaction.openOuter()) {
            nearby:
            for (BlockPos pos : BlockPos.iterate(
              (int) Math.floor(stretched.minX) - 1,
              (int) Math.floor(stretched.minY) - 1,
              (int) Math.floor(stretched.minZ) - 1,
              (int) Math.ceil(stretched.maxX) + 1,
              (int) Math.ceil(stretched.maxY) + 1,
              (int) Math.ceil(stretched.maxZ) + 1)
            ) {
                if (OPENED.containsKey(pos)) continue;
                BlockState state = world.getBlockState(pos);
                DoorToggler toggler = DoorToggler.LOOKUP.find(world, pos, state, null, entity);
                if (toggler == null) continue;
                try (var t2 = t.openNested()) {
                    var positions = toggler.toggleDoor(player, t2);
                    if (positions.isEmpty()) continue;
                    for (BlockPos pos1 : positions) {
                        for (Direction direction : RedstoneView.DIRECTIONS) {
                            BlockPos pos2 = pos1.offset(direction);
                            if (!world.getBlockState(pos2).canPlaceAt(world, pos2)) {
                                continue nearby;
                            }
                        }
                    }
                    for (BlockPos pos1 : positions) {
                        BlockState state1 = world.getBlockState(pos1);
                        VoxelShape shape = state1.getCollisionShape(world, pos1, shapeContext);
                        if (shape.getMax(Direction.Axis.Y) + pos.getY() <= entityBox.minY) {
                            continue;
                        }
                    }
                    t2.commit();
                }
                var newState = state;
                //if (Boolean.TRUE.equals(ShouldNotOpen.EVENT.invoker().shouldNotOpen(world, pos, state, player, newState))) {
                //    continue;
                //}
                VoxelShape shape = state.getCollisionShape(world, pos, shapeContext);
                //if (!doesCollide(shape, pos, entityBox) && doesCollide(shape, pos, stretched)) {
                //    BlockState toggledState = ToggledBlockState.invoke(world, pos, currentState, player);
                //    if (toggledState.isAir()) {
                //        return;
                //    }
                //    //collisions.put(pos.toImmutable(),Pair.of(currentState,toggledState));
                //}
                //
                //VoxelShape shape = currentState.getCollisionShape(world, pos, shapeContext);
                if (shape.getMax(Direction.Axis.Y) + pos.getY() <= entityBox.minY) {
                    continue;
                }
                if (!doesCollide(shape, pos, entityBox) && doesCollide(shape, pos, stretched)) {
                    VoxelShape usedShape = newState.getCollisionShape(world, pos, shapeContext);
                    if ((doesCollide(usedShape, pos, entityBox) || !doesCollide(usedShape, pos, stretched)) && use(player, pos, world, state, interactor)) {
                        DoorOpening.OPENED.put(pos.toImmutable(), world.getBlockState(pos));
                    }
                }
            }
        }
        //for (var entry : collisions.entrySet()) {
        //    BlockPos pos = entry.getKey();
        //    BlockState currentState = entry.getValue().first();
        //    BlockState toggledState = entry.getValue().second();
        //    world.setBlockState(pos,toggledState);
        //    VoxelShape usedShape = toggledState.getCollisionShape(world, pos, shapeContext);
        //
        //}
        //for (var entry : collisions.entrySet()) {
        //    BlockPos pos = entry.getKey();
        //    BlockState currentState = entry.getValue().first();
        //    BlockState toggledState = entry.getValue().second();
        //
        //    VoxelShape usedShape = toggledState.getCollisionShape(world, pos, shapeContext);
        //
        //}
        
    }
    static boolean doesCollide(VoxelShape shape, BlockPos pos, Box aabb) {
        for (Box box : shape.getBoundingBoxes()) {
            if (box.offset(pos).intersects(aabb)) {
                return true;
            }
        }
        return false;
    }
    static double absMax(double x, double y) {
        return Math.abs(x) >= Math.abs(y) ? x : y;
    }
    static boolean use(ClientPlayerEntity player, BlockPos pos, World world, BlockState state, ClientPlayerInteractionManager interactor) {
        pos = pos.toImmutable();
        BlockHitResult hitResult = getHitResult(world, pos, state, player);
        if (interactor.interactBlock(player, Hand.MAIN_HAND, hitResult).isAccepted()) {
            BlockState usedState = world.getBlockState(pos);
            return !state.getEntries().equals(usedState.getEntries());
        }
        return false;
    }
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
    @ApiStatus.Internal
    static void onStartTick(ClientWorld world) {
        var client = MinecraftClient.getInstance();
        var player = client.player;
        var entity = player.getRootVehicle();
        if (!shouldAct(client, player, entity)) {
            return;
        }
        var movement = player.getVelocity();
        Box swimming = entity instanceof LivingEntity living ? living.getBoundingBox(EntityPose.SWIMMING).offset(entity.getPos()) : entity.getBoundingBox();
        ClientPlayerInteractionManager interactor = client.interactionManager;
        if (interactor == null) return;
        Box entityBox = entity.getBoundingBox();
        //NO_CLIP.set(DoorOpening.class);
        Vec3d adjusted = movement;//entity.adjustMovementForCollisions(movement);
        //NO_CLIP.remove();
        Box stretched = entityBox.stretch(adjusted);
        ShapeContext shapeContext = ShapeContext.of(entity);
        for (var iterator = DoorOpening.OPENED.entrySet().iterator(); iterator.hasNext(); ) {
            var entry = iterator.next();
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();
            if (!world.getBlockState(pos).equals(state)) {
                iterator.remove();
                continue;
            }
            BlockState toggledState = ToggledBlockState.invoke(world, pos, state, player);
            if (toggledState.isAir()) {
                iterator.remove();
                continue;
            }
            if (entity.getBlockPos().equals(pos) && state.getBlock() instanceof TrapdoorBlock && entity instanceof LivingEntity living && living.isClimbing()) {
                continue;//防止爬活板门梯子时把活板门关上导致爬不上去
            }
            VoxelShape usedShape = toggledState.getCollisionShape(world, pos, shapeContext);
            boolean inBlock = entity == player && (doesCollide(usedShape, pos, swimming) || world.getBlockCollisions(entity, entityBox).iterator().hasNext());//防止把头部的门关了导致玩家的空间被挤压而使站姿变为泳姿（爬行姿态）
            if ((inBlock || !doesCollide(usedShape, pos, stretched)) && use(player, pos, world, state, interactor)) {
                iterator.remove();
            }
        }
    }
    static boolean shouldAct(MinecraftClient client, ClientPlayerEntity player, Entity vehicle) {
        if (!ClientConfigs.getOrCreate(CADConfigs.PATH).get(CADConfigs.ON)) return false;
        if (client.player != player) return false;//防止自由相机
        if (player.isSneaking() || vehicle.noClip) return false;
        if (FabricLoader.getInstance().isModLoaded("litematica") && (player.getMainHandStack().isOf(Items.STICK) || player.getOffHandStack().isOf(Items.STICK))) {
            return false;
        }
        if (FabricLoader.getInstance().isModLoaded("worldedit") && (player.getMainHandStack().isOf(Items.WOODEN_AXE))) {
            return false;
        }
        return true;
    }
}
