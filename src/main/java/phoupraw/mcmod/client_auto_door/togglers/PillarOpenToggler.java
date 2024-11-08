package phoupraw.mcmod.client_auto_door.togglers;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

public class PillarOpenToggler extends OpenToggler {
    private final int down;
    private final int up;
    public PillarOpenToggler(World world, BlockPos pos, BlockState state) {
        super(world, pos, state);
        BlockPos.Mutable pos1 = new BlockPos.Mutable().set(pos);
        while (true) {
            if (world.getBlockState(pos1).isOf(state.getBlock())) {
                pos1.move(0, -1, 0);
            } else {
                down = pos1.getY();
                break;
            }
        }
        pos1.set(pos);
        while (true) {
            if (world.getBlockState(pos1).isOf(state.getBlock())) {
                pos1.move(0, 1, 0);
            } else {
                up = pos1.getY();
                break;
            }
        }
    }
    @Override
    protected void readSnapshot(Boolean snapshot) {
        super.readSnapshot(snapshot);
        BlockPos.Mutable pos1 = new BlockPos.Mutable().set(pos);
        for (int i = down; i <= up; i++) {
            if (i == 0) continue;
            pos1.setY(i);
            BlockState state1 = world.getBlockState(pos1);
            if (state1.contains(DoorBlock.OPEN)) {
                world.setBlockState(pos1, state1.with(DoorBlock.OPEN, snapshot), 0);
            }
        }
    }
    @Override
    public @NotNull @UnmodifiableView Collection<BlockPos> toggle(PlayerEntity player, @NotNull TransactionContext transaction) {
        super.toggle(player, transaction);
        return new ObjectArrayList<>(Iterators.transform(BlockPos.iterate(pos.withY(down), pos.withY(up)).iterator(), BlockPos::toImmutable));
    }
}
