package phoupraw.mcmod.client_auto_door;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import phoupraw.mcmod.client_auto_door.config.CADConfigs;
import phoupraw.mcmod.client_auto_door.events.DoorToggler;
import phoupraw.mcmod.client_auto_door.events.ShouldNotOpen;
import phoupraw.mcmod.client_auto_door.misc.DoorOpening;
import phoupraw.mcmod.client_auto_door.modules.Attachment;
import phoupraw.mcmod.trilevel_config.api.Configs;

import java.util.Collection;
import java.util.Set;

@Environment(EnvType.CLIENT)
public final class ClientAutomaticDoorsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Configs.register(CADConfigs.PATH, CADConfigs.ON);
        ShouldNotOpen.EVENT.register(Attachment::shouldNotOpen);
        //ClientTickEvents.START_WORLD_TICK.register(DoorOpening::onStartTick);
        DoorToggler.LOOKUP.registerFallback((world, pos, state, blockEntity, context) -> {
            if (!(state.isIn(BlockTags.TRAPDOORS) && !state.isOf(Blocks.IRON_TRAPDOOR) && state.contains(DoorBlock.OPEN))) {
                return null;
            }
            class TrapdoorToggler extends SnapshotParticipant<BlockState> implements DoorToggler {
                @Override
                protected BlockState createSnapshot() {
                    return world.getBlockState(pos);
                }
                @Override
                protected void readSnapshot(BlockState snapshot) {
                    world.setBlockState(pos, snapshot, 0);
                }
                @Override
                public @NotNull @UnmodifiableView Collection<BlockPos> toggleDoor(PlayerEntity player, @NotNull TransactionContext transaction) {
                    BlockState state = createSnapshot();
                    if (!state.contains(DoorBlock.OPEN)) {
                        return Set.of();
                    }
                    updateSnapshots(transaction);
                    BlockState newState = state.cycle(DoorBlock.OPEN);
                    readSnapshot(newState);
                    return Set.of(pos);
                }
                @Override
                protected void onFinalCommit() {
                    super.onFinalCommit();
                    readSnapshot(state);
                    //if (state.get(DoorBlock.OPEN).equals(createSnapshot().getOrEmpty(DoorBlock.OPEN).orElse(null))) {
                    //    return;
                    //}
                    var interactor = MinecraftClient.getInstance().interactionManager;
                    if (interactor == null) return;
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;
                    if (player == null) return;
                    interactor.interactBlock(player, Hand.MAIN_HAND, DoorOpening.getHitResult(world, pos, state, player));
                }
            }
            return new TrapdoorToggler();
        });
    }
}
