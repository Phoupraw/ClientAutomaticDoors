package phoupraw.mcmod.client_auto_door.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public final class ClientAutomaticDoorsDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator g) {
        var pack = g.createPack();
        pack.addProvider(ChineseGen::new);
        pack.addProvider(EnglishGen::new);
    }
}
