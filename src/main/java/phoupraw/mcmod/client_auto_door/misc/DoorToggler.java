package phoupraw.mcmod.client_auto_door.misc;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import phoupraw.mcmod.client_auto_door.events.BlockShapeToggler;

import java.util.Collection;
import java.util.Set;

public class DoorToggler extends OpenToggler {
    @ApiStatus.Internal
    public static @Nullable BlockShapeToggler find(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Entity context) {
        return state.isIn(BlockTags.DOORS) && !state.isOf(Blocks.IRON_DOOR) && state.contains(DoorBlock.OPEN) && state.contains(DoorBlock.HALF) ? new DoorToggler(world, pos, state) : null;
    }
    public DoorToggler(World world, BlockPos pos, BlockState state) {
        super(world, pos, state);
    }
    @Override
    protected void readSnapshot(Boolean snapshot) {
        super.readSnapshot(snapshot);
        BlockState newState = getBlockState();
        Direction direction = newState.get(DoorBlock.HALF).getOppositeDirection();
        BlockPos pos1 = pos.offset(direction);
        BlockState state1 = world.getBlockState(pos1);
        if (state1.contains(DoorBlock.OPEN)) {
            world.setBlockState(pos1, state1.with(DoorBlock.OPEN, snapshot), 0);
        }
    }
    @Override
    public @NotNull @UnmodifiableView Collection<BlockPos> toggle(PlayerEntity player, @NotNull TransactionContext transaction) {
        super.toggle(player, transaction);
        return Set.of(pos, pos.offset(state.get(DoorBlock.HALF).getOppositeDirection()));
    }
}
