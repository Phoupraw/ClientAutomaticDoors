package phoupraw.mcmod.client_auto_door.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface DramaticDoors {
    String MOD_ID = "dramaticdoors";
    TagKey<Block> TALL_WOODEN_DOORS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "tall_wooden_doors"));
    TagKey<Block> MOB_INTERACTABLE_TALL_DOORS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "mob_interactable_tall_doors"));
    TagKey<Block> SHORT_WOODEN_DOORS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "short_wooden_doors"));
    TagKey<Block> MOB_INTERACTABLE_SHORT_DOORS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "mob_interactable_short_doors"));
    @ApiStatus.Internal
    static @Nullable BlockState tallDoor(BlockView world, BlockPos pos, BlockState state, PlayerEntity player) {
        if ((state.isIn(TALL_WOODEN_DOORS) || state.isIn(MOB_INTERACTABLE_TALL_DOORS)) && state.contains(DoorBlock.OPEN)) {
            return state.cycle(DoorBlock.OPEN);
        }
        return null;
    }
    @ApiStatus.Internal
    static @Nullable BlockState shortDoor(BlockView world, BlockPos pos, BlockState state, PlayerEntity player) {
        if ((state.isIn(SHORT_WOODEN_DOORS) || state.isIn(MOB_INTERACTABLE_SHORT_DOORS)) && state.contains(DoorBlock.OPEN)) {
            return state.cycle(DoorBlock.OPEN);
        }
        return null;
    }
}
