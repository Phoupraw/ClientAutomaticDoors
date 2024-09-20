package phoupraw.mcmod.client_auto_door.mixins.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import phoupraw.mcmod.client_auto_door.events.ToggledBlockState;
import phoupraw.mcmod.client_auto_door.misc.DoorOpening;

@Environment(EnvType.CLIENT)
public interface MMAbstractBlockState {
    static boolean noClip(BlockState self, BlockView world, BlockPos pos) {
        return DoorOpening.NO_CLIP.get() != null && !ToggledBlockState.invoke(world, pos, self, MinecraftClient.getInstance().player).isAir();
    }
}
