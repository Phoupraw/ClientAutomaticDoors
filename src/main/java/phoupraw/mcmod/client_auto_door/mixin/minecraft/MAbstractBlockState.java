package phoupraw.mcmod.client_auto_door.mixin.minecraft;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import phoupraw.mcmod.client_auto_door.mixins.minecraft.MMAbstractBlockState;

@Environment(EnvType.CLIENT)
@Mixin(AbstractBlock.AbstractBlockState.class)
abstract class MAbstractBlockState {
    @WrapMethod(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;")
    private VoxelShape noClip(BlockView world, BlockPos pos, ShapeContext context, Operation<VoxelShape> original) {
        return MMAbstractBlockState.noClip(asBlockState(), world, pos) ? VoxelShapes.empty() : original.call(world, pos, context);
    }
    @WrapMethod(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/shape/VoxelShape;")
    private VoxelShape noClip(BlockView world, BlockPos pos, Operation<VoxelShape> original) {
        return MMAbstractBlockState.noClip(asBlockState(), world, pos) ? VoxelShapes.empty() : original.call(world, pos);
    }
    @Shadow
    protected abstract BlockState asBlockState();
}
