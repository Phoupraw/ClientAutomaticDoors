package phoupraw.mcmod.client_auto_door.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import phoupraw.mcmod.client_auto_door.ClientAutomaticDoors;

import java.util.concurrent.CompletableFuture;

import static phoupraw.mcmod.client_auto_door.ClientAutomaticDoors.ID;

final class ChineseGen extends FabricLanguageProvider {
    ChineseGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "zh_cn", registryLookup);
    }
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder b) {
        String modName = "客户端自动门";
        b.add(ClientAutomaticDoors.NAME_KEY, modName);
        b.add("modmenu.summaryTranslation." + ID, "自动开关门");
        b.add("modmenu.descriptionTranslation." + ID, """
          自动开关门、活板门、栅栏门。 
          """);
    }
}
