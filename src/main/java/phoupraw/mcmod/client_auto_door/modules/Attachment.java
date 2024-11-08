package phoupraw.mcmod.client_auto_door.modules;

import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.client_auto_door.misc.DoorOpening;

@UtilityClass
public class Attachment {
    public static boolean simulation;
    public static boolean affected;
    @Environment(EnvType.CLIENT)
    @ApiStatus.Internal
    public static @Nullable Boolean shouldNotOpen(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockState newState) {
        simulation = true;
        affected = false;
        state.onUse(world, player, DoorOpening.getHitResult(world, pos, state, player));
        state.onUse(world, player, DoorOpening.getHitResult(world, pos, state, player));
        simulation = false;
        return affected ? true : null;
    }
}
