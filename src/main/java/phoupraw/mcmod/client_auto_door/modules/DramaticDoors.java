package phoupraw.mcmod.client_auto_door.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import phoupraw.mcmod.client_auto_door.events.BlockShapeToggler;
import phoupraw.mcmod.client_auto_door.togglers.OpenToggler;
import phoupraw.mcmod.client_auto_door.togglers.PillarOpenToggler;

@ApiStatus.NonExtendable
public interface DramaticDoors {
    String MOD_ID = "dramaticdoors";
    TagKey<Block> TALL_WOODEN_DOORS = TagKey.of(RegistryKeys.BLOCK, id("tall_wooden_doors"));
    static @NotNull Identifier id(String tall_wooden_doors) {
        return Identifier.of(MOD_ID, tall_wooden_doors);
    }
    TagKey<Block> MOB_INTERACTABLE_TALL_DOORS = TagKey.of(RegistryKeys.BLOCK, id("mob_interactable_tall_doors"));
    TagKey<Block> SHORT_WOODEN_DOORS = TagKey.of(RegistryKeys.BLOCK, id("short_wooden_doors"));
    TagKey<Block> MOB_INTERACTABLE_SHORT_DOORS = TagKey.of(RegistryKeys.BLOCK, id("mob_interactable_short_doors"));
    @ApiStatus.Internal
    static @Nullable BlockShapeToggler findShortDoor(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Entity context) {
        return (state.isIn(SHORT_WOODEN_DOORS) || state.isIn(MOB_INTERACTABLE_SHORT_DOORS)) && state.contains(DoorBlock.OPEN) ? new OpenToggler(world, pos, state) : null;
    }
    @ApiStatus.Internal
    static @Nullable BlockShapeToggler findTallDoor(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Entity context) {
        return (state.isIn(TALL_WOODEN_DOORS) || state.isIn(MOB_INTERACTABLE_TALL_DOORS)) && state.contains(DoorBlock.OPEN) ? new PillarOpenToggler(world, pos, state) : null;
    }
}
