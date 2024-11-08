package phoupraw.mcmod.client_auto_door.modules;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectLongPair;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.client_auto_door.config.CADConfigs;
import phoupraw.mcmod.client_auto_door.events.BlockShapeToggler;
import phoupraw.mcmod.client_auto_door.togglers.DoorToggler;
import phoupraw.mcmod.client_auto_door.togglers.OpenToggler;
import phoupraw.mcmod.trilevel_config.api.ClientConfigs;
import phoupraw.mcmod.util.MCUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

@ApiStatus.NonExtendable
public interface Vanilla {
    Comparator<Vec3i> COMPARATOR = Comparator
      .comparingInt(Vec3i::getY)
      .thenComparingInt(Vec3i::getX)
      .thenComparingInt(Vec3i::getZ);
    Map<BlockPos, ObjectLongPair<BlockState>> OPENEDS = new Object2ObjectRBTreeMap<>(COMPARATOR);
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
        Long2ObjectMap<Map<BlockPos, BlockState>> timeGrouped = new Long2ObjectArrayMap<>(3);
        for (var entry : OPENEDS.entrySet()) {
            long time = entry.getValue().secondLong();
            var openeds = timeGrouped.get(time);
            if (openeds == null) {
                openeds = new Object2ObjectRBTreeMap<>(COMPARATOR);
                timeGrouped.put(time, openeds);
            }
            openeds.put(entry.getKey(), entry.getValue().first());
        }
        float height = player.getHeight();
        for (var timeEntry : timeGrouped.long2ObjectEntrySet()) {
            if (timeEntry.getLongKey() >= world.getTime()) {
                continue;
            }
            try (var t = Transaction.openOuter()) {
                Collection<BlockPos> removed = new ObjectOpenHashSet<>();
                for (var iter = timeEntry.getValue().entrySet().iterator(); iter.hasNext(); ) {
                    var entry = iter.next();
                    BlockPos pos = entry.getKey();
                    BlockState state = world.getBlockState(pos);
                    if (state != entry.getValue()) {
                        iter.remove();
                        continue;
                    }
                    BlockShapeToggler toggler = BlockShapeToggler.LOOKUP.find(world, pos, state, null, vehicle);
                    if (toggler == null || toggler.toggle(player, t).isEmpty()) {
                        continue;
                    }
                    removed.add(pos);
                }
                if (vehicle == player && height > MCUtils.getHeight(player)) {
                    continue;
                }
                OPENEDS.keySet().removeAll(removed);
                t.commit();
            }
        }
    }
    @ApiStatus.Internal
    static @Nullable BlockShapeToggler findTrapdoorAndFencGate(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Entity context) {
        return (state.isIn(BlockTags.TRAPDOORS) && !state.isOf(Blocks.IRON_TRAPDOOR) || state.isIn(BlockTags.FENCE_GATES)) && state.contains(DoorBlock.OPEN) ? new OpenToggler(world, pos, state) : null;
    }
    @ApiStatus.Internal
    static @Nullable BlockShapeToggler findDoor(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Entity context) {
        return state.isIn(BlockTags.DOORS) && !state.isOf(Blocks.IRON_DOOR) && state.contains(DoorBlock.OPEN) && state.contains(DoorBlock.HALF) ? new DoorToggler(world, pos, state) : null;
    }
}
