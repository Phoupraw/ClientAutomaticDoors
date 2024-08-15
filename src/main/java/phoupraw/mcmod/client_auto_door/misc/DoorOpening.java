package phoupraw.mcmod.client_auto_door.misc;

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public interface DoorOpening {
    ThreadLocal<Object> NO_CLIP = new ThreadLocal<>();
    Map<BlockPos, BlockState> OPENED = Object2ObjectMaps.synchronize(new Object2ObjectRBTreeMap<>(Comparator
      .comparingInt(Vec3i::getY)
      .thenComparingInt(Vec3i::getX)
      .thenComparingInt(Vec3i::getZ)));
    static List<Box> getAABBs(Entity entity) {
        List<Box> boxes = new ObjectArrayList<>();
        for (var i = entity; i != null; i = i.getVehicle()) {
            boxes.add(i.getBoundingBox());
        }
        return boxes;
    }
    static void openDoor(Entity entity, Vec3d movement, MinecraftClient client, Box swimming, ClientPlayerEntity player) {
        ClientPlayerInteractionManager interactor = client.interactionManager;
        if (interactor == null) return;
        World world = entity.getWorld();
        Box entityBox = entity.getBoundingBox();
        Vec3d eyePos = entity.getEyePos();
        NO_CLIP.set(DoorOpening.class);
        Vec3d adjusted = entity.adjustMovementForCollisions(movement);
        NO_CLIP.remove();
        Vec3d max = new Vec3d(
          absMax(movement.getX(), adjusted.getX()),
          absMax(movement.getY(), adjusted.getY()),
          absMax(movement.getZ(), adjusted.getZ()));
        Box stretched = entityBox.stretch(max);
        Box moved = entityBox.offset(max);
        ShapeContext shapeContext = ShapeContext.of(entity);
        for (var iterator = DoorOpening.OPENED.entrySet().iterator(); iterator.hasNext(); ) {
            var entry = iterator.next();
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();
            if (!world.getBlockState(pos).equals(state) || !isDoor(world, pos, state)) {
                iterator.remove();
                continue;
            }
            VoxelShape usedShape = state.cycle(DoorBlock.OPEN).getCollisionShape(world, pos, shapeContext);
            if ((doesCollide(usedShape, pos, swimming) || !doesCollide(usedShape, pos, stretched) || world.getBlockCollisions(entity, entityBox).iterator().hasNext()) && use(player, pos, eyePos, world, state, interactor)) {
                iterator.remove();
            }
        }
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
            BlockState state = world.getBlockState(pos);
            if (!isDoor(world, pos, state)) {
                continue;
            }
            VoxelShape shape = state.getCollisionShape(world, pos, shapeContext);
            if (shape.getMax(Direction.Axis.Y) + pos.getY() <= entityBox.minY) {
                continue;
            }
            if (!doesCollide(shape, pos, entityBox) && doesCollide(shape, pos, stretched)) {
                VoxelShape usedShape = state.cycle(DoorBlock.OPEN).getCollisionShape(world, pos, shapeContext);
                if ((doesCollide(usedShape, pos, entityBox) || !doesCollide(usedShape, pos, stretched)) && use(player, pos, eyePos, world, state, interactor)) {
                    DoorOpening.OPENED.put(pos.toImmutable(), world.getBlockState(pos));
                }
            }
        }
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
        BlockHitResult hitResult = world.raycastBlock(eyePos, end, pos.toImmutable(), state.getRaycastShape(world, pos), state);
        if (hitResult == null) {
            hitResult = new BlockHitResult(center, Direction.UP, pos.toImmutable(), false);
        }
        if (interactor.interactBlock(player, Hand.MAIN_HAND, hitResult).isAccepted()) {
            BlockState usedState = world.getBlockState(pos);
            return usedState.contains(DoorBlock.OPEN) && state.get(DoorBlock.OPEN) != usedState.get(DoorBlock.OPEN);
        }
        return false;
    }
    static boolean isDoor(BlockView world, BlockPos pos, BlockState state) {
        return state.contains(DoorBlock.OPEN) && !(state.isOf(Blocks.IRON_DOOR) || state.isOf(Blocks.IRON_TRAPDOOR));
    }
}
