package phoupraw.mcmod.client_auto_door.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

final class EnglishGen extends FabricLanguageProvider {
    EnglishGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder b) {
        
        //b.add(Trifle.NAME_KEY, "Torches in Water");
        //b.add("modmenu.summaryTranslation." + ID, "Place torches in water!");
        //b.add("modmenu.descriptionTranslation." + ID, """
        //  - Glow Ink Torch, which can be placed in water, crafted from glow ink sac and stick.
        //  """);
        //b.add(TiWBlocks.GLOW_INK_TORCH, "Glow Ink Torch");
        //b.add(TiWBlocks.GLOWSTONE_TORCH, "Glowstone Torch");
        //b.add(TiWBlocks.CRIMSON_MAGMA_TORCH, "Crimson Magma Torch");
        //b.add(TiWBlocks.WARPED_MAGMA_TORCH, "Warped Magma Torch");
        //b.add(TiWConfig.ITEM_DESC, "Can be placed in water source or flow. Can be placed on the side of bottom slab or stairs.");
        //b.add(TiWConfig.RESTART_KEY, "After modifying this option, you need to restart the game to take effect");
        //b.add("config." + ID + ".lavaDestroy.desc", "Lava can flow into and destory");
        //b.add("config." + ID + ".glowInkTorch_luminance.desc", "Light level");
        //b.add("config." + ID + ".glowInkTorch_luminance.error", "Placed torches won't auto update light level. You need to break and re-place them");
        //b.add(TIDs.OVERRIDE.toTranslationKey("dataPack"), "Remove recipe of Aquatic Torch.");
    }
}
