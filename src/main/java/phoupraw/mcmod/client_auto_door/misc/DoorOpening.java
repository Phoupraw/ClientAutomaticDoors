package phoupraw.mcmod.client_auto_door.misc;

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.client_auto_door.config.CADConfigs;
import phoupraw.mcmod.client_auto_door.events.ToggledBlockState;
import phoupraw.mcmod.trilevel_config.api.ClientConfigs;

import java.util.Comparator;
import java.util.Map;

@Environment(EnvType.CLIENT)
public interface DoorOpening {
    ThreadLocal<Object> NO_CLIP = new ThreadLocal<>();
    Map<BlockPos, BlockState> OPENED = Object2ObjectMaps.synchronize(new Object2ObjectRBTreeMap<>(Comparator
      .comparingInt(Vec3i::getY)
      .thenComparingInt(Vec3i::getX)
      .thenComparingInt(Vec3i::getZ)));
    @ApiStatus.Internal
    static void openDoor(Entity entity, Vec3d movement, MinecraftClient client, Box swimming, ClientPlayerEntity player) {
        if (!ClientConfigs.getOrCreate(CADConfigs.PATH).get(CADConfigs.ON)) return;
        if (client.player != player) return;//防止自由相机
        if (player.isSneaking()) return;
        ClientPlayerInteractionManager interactor = client.interactionManager;
        if (interactor == null) return;
        World world = entity.getWorld();
        Box entityBox = entity.getBoundingBox();
        Vec3d eyePos = entity.getEyePos();
        NO_CLIP.set(DoorOpening.class);
        Vec3d adjusted = entity.adjustMovementForCollisions(movement);
        NO_CLIP.remove();
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
        for (BlockPos pos : BlockPos.iterate(
          (int) Math.floor(stretched.minX) - 1,
          (int) Math.floor(stretched.minY) - 1,
          (int) Math.floor(stretched.minZ) - 1,
          (int) Math.ceil(stretched.maxX) + 1,
          (int) Math.ceil(stretched.maxY) + 1,
          (int) Math.ceil(stretched.maxZ) + 1)
        ) {
            
            if (OPENED.containsKey(pos)) {
                continue;
            }
            BlockState currentState = world.getBlockState(pos);
            BlockState toggledState = ToggledBlockState.invoke(world, pos, currentState, player);
            if (toggledState.isAir()) {
                continue;
            }
            VoxelShape shape = currentState.getCollisionShape(world, pos, shapeContext);
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
                VoxelShape usedShape = toggledState.getCollisionShape(world, pos, shapeContext);
                if ((doesCollide(usedShape, pos, entityBox) || !doesCollide(usedShape, pos, stretched)) && use(player, pos, eyePos, world, currentState, interactor)) {
                    DoorOpening.OPENED.put(pos.toImmutable(), world.getBlockState(pos));
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
    static boolean use(ClientPlayerEntity player, BlockPos pos, Vec3d eyePos, World world, BlockState state, ClientPlayerInteractionManager interactor) {
        Vec3d center = pos.toCenterPos();
        Vec3d end = eyePos.lerp(center, 2);
        BlockPos immutable = pos.toImmutable();
        BlockHitResult hitResult = world.raycastBlock(eyePos, end, immutable, state.getRaycastShape(world, pos), state);
        if (hitResult == null) {
            hitResult = new BlockHitResult(center, Direction.UP, immutable, false);
        }
        if (interactor.interactBlock(player, Hand.MAIN_HAND, hitResult).isAccepted()) {
            BlockState usedState = world.getBlockState(pos);
            return !state.getEntries().equals(usedState.getEntries());
        }
        return false;
    }
    @ApiStatus.Internal
    static void onStartTick(ClientWorld world) {
        var client = MinecraftClient.getInstance();
        var player = client.player;
        if (player.isSneaking()) return;
        var entity = player.getRootVehicle();
        var movement = player.getVelocity();
        Box swimming = entity instanceof LivingEntity living ? living.getBoundingBox(EntityPose.SWIMMING).offset(entity.getPos()) : entity.getBoundingBox();
        ClientPlayerInteractionManager interactor = client.interactionManager;
        if (interactor == null) return;
        Box entityBox = entity.getBoundingBox();
        Vec3d eyePos = entity.getEyePos();
        //NO_CLIP.set(DoorOpening.class);
        Vec3d adjusted = entity.adjustMovementForCollisions(movement);
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
            if ((inBlock || !doesCollide(usedShape, pos, stretched)) && use(player, pos, eyePos, world, state, interactor)) {
                iterator.remove();
            }
        }
    }
}
