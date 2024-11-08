package phoupraw.mcmod.client_auto_door.modules;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface Vanilla {
    @ApiStatus.Internal
    static @Nullable BlockState door(BlockView world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (state.isIn(BlockTags.MOB_INTERACTABLE_DOORS) && state.contains(DoorBlock.OPEN)) {
            return state.cycle(DoorBlock.OPEN);
        }
        return null;
    }
    @ApiStatus.Internal
    static @Nullable BlockState trapdoor(BlockView world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (state.isIn(BlockTags.TRAPDOORS) && !state.isOf(Blocks.IRON_TRAPDOOR) && state.contains(DoorBlock.OPEN)) {
            return state.cycle(DoorBlock.OPEN);
        }
        return null;
    }
    @ApiStatus.Internal
    static @Nullable BlockState fenceGate(BlockView world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (state.isIn(BlockTags.FENCE_GATES) && state.contains(DoorBlock.OPEN)) {
            return state.cycle(DoorBlock.OPEN);
        }
        return null;
    }
}
