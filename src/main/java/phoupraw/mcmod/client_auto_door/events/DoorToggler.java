package phoupraw.mcmod.client_auto_door.events;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import phoupraw.mcmod.client_auto_door.constant.CADIDs;

import java.util.Collection;

public interface DoorToggler {
    @NotNull
    @UnmodifiableView
    Collection<BlockPos> toggleDoor(PlayerEntity player, @NotNull TransactionContext transaction);
    BlockApiLookup<DoorToggler, Entity> LOOKUP = BlockApiLookup.get(CADIDs.of("door_toggler"), DoorToggler.class, Entity.class);
}
