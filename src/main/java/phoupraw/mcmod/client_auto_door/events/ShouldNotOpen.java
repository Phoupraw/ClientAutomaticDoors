package phoupraw.mcmod.client_auto_door.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ShouldNotOpen {
    @Nullable Boolean shouldNotOpen(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockState newState);
    Event<ShouldNotOpen> EVENT = EventFactory.createArrayBacked(ShouldNotOpen.class, callbacks -> (world, pos, state, player, newState) -> {
        for (ShouldNotOpen callback : callbacks) {
            var r = callback.shouldNotOpen(world, pos, state, player, newState);
            if (r != null) return r;
        }
        return null;
    });
}
