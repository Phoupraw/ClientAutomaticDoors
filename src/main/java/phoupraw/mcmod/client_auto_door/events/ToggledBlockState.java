package phoupraw.mcmod.client_auto_door.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@FunctionalInterface
public interface ToggledBlockState {
    Event<ToggledBlockState> EVENT = EventFactory.createArrayBacked(ToggledBlockState.class, callbacks -> (world, pos, state, player) -> {
        for (ToggledBlockState callback : callbacks) {
            var r = callback.getToggledState(world, pos, state, player);
            if (r != null) return r;
        }
        return null;
    });
    static @NotNull BlockState invoke(BlockView world, BlockPos pos, BlockState state, PlayerEntity player) {
        return Objects.requireNonNullElse(EVENT.invoker().getToggledState(world, pos, state, player), Blocks.AIR.getDefaultState());
    }
    @Nullable BlockState getToggledState(BlockView world, BlockPos pos, BlockState state, PlayerEntity player);
}
