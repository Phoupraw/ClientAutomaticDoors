package phoupraw.mcmod.client_auto_door.misc;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import phoupraw.mcmod.client_auto_door.config.CADConfigs;
import phoupraw.mcmod.client_auto_door.events.BlockShapeToggler;
import phoupraw.mcmod.client_auto_door.mixins.minecraft.MMClientPlayerEntity;
import phoupraw.mcmod.trilevel_config.api.ClientConfigs;

import java.util.Collection;
import java.util.Comparator;

@Environment(EnvType.CLIENT)
public interface DoorOpening {
    Comparator<Vec3i> COMPARATOR = Comparator
      .comparingInt(Vec3i::getY)
      .thenComparingInt(Vec3i::getX)
      .thenComparingInt(Vec3i::getZ);
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
    //FIXME 在一竖列活板门上跳会关门导致趴下
    @ApiStatus.Internal
    static void close(ClientWorld world) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        var vehicle = player.getRootVehicle();
        if (!shouldAct(client, player, vehicle)) return;
        var interactor = client.interactionManager;
        if (interactor == null) return;
        try (var t = Transaction.openOuter()) {
            float height = player.getHeight();
            Collection<BlockPos> removed = new ObjectOpenHashSet<>();
            for (var iter = MMClientPlayerEntity.OPENEDS.entrySet().iterator(); iter.hasNext(); ) {
                var entry = iter.next();
                BlockPos pos = entry.getKey();
                var pair = entry.getValue();
                long time = pair.secondLong();
                if (time >= world.getTime()) continue;
                BlockState state = world.getBlockState(pos);
                if (state != pair.first()) {
                    iter.remove();
                    continue;
                }
                BlockShapeToggler toggler = BlockShapeToggler.LOOKUP.find(world, pos, state, null, vehicle);
                if (toggler == null || toggler.toggle(player, t).isEmpty()) {
                    continue;
                }
                removed.add(pos);
            }
            if (vehicle == player && height > MMClientPlayerEntity.getHeight(player)) {
                return;
            }
            MMClientPlayerEntity.OPENEDS.keySet().removeAll(removed);
            t.commit();
        }
    }
}
