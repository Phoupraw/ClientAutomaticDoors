package phoupraw.mcmod.client_auto_door.misc;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import phoupraw.mcmod.client_auto_door.events.BlockShapeToggler;
import phoupraw.mcmod.util.MCUtils;

import java.util.Collection;
import java.util.Set;

public class OpenToggler extends SnapshotParticipant<Boolean> implements BlockShapeToggler {
    @ApiStatus.Internal
    public static @Nullable BlockShapeToggler find(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Entity context) {
        return (state.isIn(BlockTags.TRAPDOORS) && !state.isOf(Blocks.IRON_TRAPDOOR) || state.isIn(BlockTags.FENCE_GATES)) && state.contains(DoorBlock.OPEN) ? new OpenToggler(world, pos, state) : null;
    }
    protected final World world;
    protected final BlockPos pos;
    protected final BlockState state;
    public OpenToggler(World world, BlockPos pos, BlockState state) {
        this.world = world;
        this.pos = pos;
        this.state = state;
    }
    @Override
    protected Boolean createSnapshot() {
        return getBlockState().get(DoorBlock.OPEN);
    }
    @Override
    protected void readSnapshot(Boolean snapshot) {
        world.setBlockState(pos, getBlockState().with(DoorBlock.OPEN, snapshot), 0);
    }
    protected BlockState getBlockState() {
        return world.getBlockState(pos);
    }
    @Override
    public @NotNull @UnmodifiableView Collection<BlockPos> toggle(PlayerEntity player, @NotNull TransactionContext transaction) {
        updateSnapshots(transaction);
        readSnapshot(!createSnapshot());
        return Set.of(pos);
    }
    @Override
    protected void onFinalCommit() {
        super.onFinalCommit();
        BlockState newState = getBlockState();
        if (state.get(DoorBlock.OPEN) == newState.get(DoorBlock.OPEN)) {
            return;
        }
        readSnapshot(state.get(DoorBlock.OPEN));
        var interactor = MinecraftClient.getInstance().interactionManager;
        if (interactor == null) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        interactor.interactBlock(player, Hand.MAIN_HAND, MCUtils.getHitResult(world, pos, state, player));
    }
}
