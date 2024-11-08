package phoupraw.mcmod.client_auto_door.mixins.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.client_auto_door.events.ToggledBlockState;
import phoupraw.mcmod.client_auto_door.misc.DoorOpening;
import phoupraw.mcmod.client_auto_door.modules.Attachment;

@Environment(EnvType.CLIENT)
@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface MMAbstractBlockState {
    static boolean noClip(BlockState self, BlockView world, BlockPos pos, ShapeContext context) {
        return DoorOpening.NO_CLIP.get() != null && !ToggledBlockState.invoke(world, pos, self, context instanceof EntityShapeContext e && e.getEntity() instanceof ClientPlayerEntity player ? player : MinecraftClient.getInstance().player).isAir();
    }
    static boolean noClip(BlockState self, BlockView world, BlockPos pos) {
        return noClip(self, world, pos, ShapeContext.absent());
    }
    static boolean simulate(boolean original) {
        if (original) return true;
        if (Attachment.simulation) {
            Attachment.affected = true;
            return true;
        }
        return false;
    }
}
