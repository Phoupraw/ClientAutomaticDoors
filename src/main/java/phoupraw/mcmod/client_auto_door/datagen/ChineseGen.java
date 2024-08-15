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
          执行命令`/trilevel_config current|global client_auto_door.json set on 0`以在当前|所有存档关闭此模组；
          执行命令`/trilevel_config current|global client_auto_door.json set on`以在当前|所有存档开启此模组。
          """);
    }
}
