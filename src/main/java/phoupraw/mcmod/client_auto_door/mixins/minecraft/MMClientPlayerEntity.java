package phoupraw.mcmod.client_auto_door.mixins.minecraft;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.MovementType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.Map;

@Environment(EnvType.CLIENT)
public interface MMClientPlayerEntity {
    Map<BlockPos, BlockState> OPENED = new Object2ObjectOpenHashMap<>();
    static void openDoor(MinecraftClient client, ClientPlayerEntity player, MovementType movementType, Vec3d movement) {
        if (movementType != MovementType.SELF) return;
        ClientPlayerInteractionManager interactor = client.interactionManager;
        if (interactor == null) return;
        World world = player.getWorld();
        Box playerBox = player.getBoundingBox();
        Box swimming = player.getBoundingBox(EntityPose.SWIMMING).offset(player.getPos());
        Vec3d eyePos = player.getEyePos();
        Vec3d adjusted = player.adjustMovementForCollisions(movement);
        Vec3d max = new Vec3d(
          absMax(movement.getX(), adjusted.getX()),
          absMax(movement.getY(), adjusted.getY()),
          absMax(movement.getZ(), adjusted.getZ()));
        Box stretched = playerBox.stretch(max);
        Box moved = playerBox.offset(max);
        ShapeContext shapeContext = ShapeContext.of(player);
        for (var iterator = OPENED.entrySet().iterator(); iterator.hasNext(); ) {
            var entry = iterator.next();
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();
            if (!world.getBlockState(pos).equals(state)) {
                iterator.remove();
                continue;
            }
            if (!state.contains(DoorBlock.OPEN)) {
                iterator.remove();
                continue;
            }
            VoxelShape usedShape = state.cycle(DoorBlock.OPEN).getCollisionShape(world, pos, shapeContext);
            if ((doesCollide(usedShape, pos, swimming) || !doesCollide(usedShape, pos, stretched)) && use(player, pos, eyePos, world, state, interactor)) {
                iterator.remove();
            }
            //Collection<Box> cBBs = getCollisionBoxes(world, blockPos, newBlockState);
            //for (Box afterBB : afterBBs) {
            //    for (Box cBB : cBBs) {
            //        if (cBB.intersects(afterBB)) {
            //            continue outer;
            //        }
            //    }
            //}
            //if (interactBlock(blockPos).isAccepted()) {
            //    iterator.remove();
            //}
        }
        for (BlockPos pos : BlockPos.iterate(
          (int) Math.floor(stretched.minX) - 1,
          (int) Math.floor(stretched.minY) - 1,
          (int) Math.floor(stretched.minZ) - 1,
          (int) Math.ceil(stretched.maxX) + 1,
          (int) Math.ceil(stretched.maxY) + 1,
          (int) Math.ceil(stretched.maxZ) + 1)
        ) {
            BlockState state = world.getBlockState(pos);
            if (state.contains(DoorBlock.OPEN) && !state.isOf(Blocks.IRON_DOOR) && !state.isOf(Blocks.IRON_TRAPDOOR)) {
                VoxelShape shape = state.getCollisionShape(world, pos, shapeContext);
                if (shape.getMax(Direction.Axis.Y) + pos.getY() <= playerBox.minY) {
                    continue;
                }
                if (!doesCollide(shape, pos, playerBox) && doesCollide(shape, pos, stretched)) {
                    VoxelShape usedShape = state.cycle(DoorBlock.OPEN).getCollisionShape(world, pos, shapeContext);
                    if ((doesCollide(usedShape, pos, playerBox) || !doesCollide(usedShape, pos, stretched)) && use(player, pos, eyePos, world, state, interactor)) {
                        OPENED.put(pos.toImmutable(), world.getBlockState(pos));
                    }
                }
            }
        }
        //Entity.adjustMovementForCollisions(player,movement,playerBox,world,);
        //
        //var aabbs = Utils.getAABBs(player);
        //
        //Collection<Box> moveds = new ObjectArrayList<>();
        //Collection<Box> stepeds = new ObjectArrayList<>();
        //float stepHeight = player.getStepHeight();
        //for (Box original : aabbs) {
        //    Box moved = original.stretch(movement);
        //    moveds.add(moved);
        //    stepeds.add(moved.stretch(0, stepHeight,0));
        //}
        
    }
    private static boolean use(ClientPlayerEntity player, BlockPos pos, Vec3d eyePos, World world, BlockState state, ClientPlayerInteractionManager interactor) {
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
}
