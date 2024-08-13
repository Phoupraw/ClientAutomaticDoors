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
        String modName = "三级配置";
        b.add(ClientAutomaticDoors.NAME_KEY, modName);
        b.add("modmenu.summaryTranslation." + ID, "存档独立的配置库");
        b.add("modmenu.descriptionTranslation." + ID, """
          代码、全局、存档，三级配置系统。
          - 
          """);
    }
}
