package phoupraw.mcmod.client_auto_door.modules;

import lombok.experimental.UtilityClass;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.client_auto_door.events.ToggledBlockState;

@UtilityClass
public class DramaticDoors {
    public static final String MOD_ID = "dramaticdoors";
    public static final TagKey<Block> TALL_WOODEN_DOORS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "tall_wooden_doors"));
    public static final TagKey<Block> MOB_INTERACTABLE_TALL_DOORS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "mob_interactable_tall_doors"));
    public static final TagKey<Block> SHORT_WOODEN_DOORS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "short_wooden_doors"));
    public static final TagKey<Block> MOB_INTERACTABLE_SHORT_DOORS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "mob_interactable_short_doors"));
    static {
        ToggledBlockState.EVENT.register(DramaticDoors::tallDoor);
        ToggledBlockState.EVENT.register(DramaticDoors::shortDoor);
    }
    private static @Nullable BlockState tallDoor(BlockView world, BlockPos pos, BlockState state, PlayerEntity player) {
        if ((state.isIn(TALL_WOODEN_DOORS) || state.isIn(MOB_INTERACTABLE_TALL_DOORS)) && state.contains(DoorBlock.OPEN)) {
            return state.cycle(DoorBlock.OPEN);
        }
        return null;
    }
    private static @Nullable BlockState shortDoor(BlockView world, BlockPos pos, BlockState state, PlayerEntity player) {
        if ((state.isIn(SHORT_WOODEN_DOORS) || state.isIn(MOB_INTERACTABLE_SHORT_DOORS)) && state.contains(DoorBlock.OPEN)) {
            return state.cycle(DoorBlock.OPEN);
        }
        return null;
    }
}
