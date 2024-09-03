package phoupraw.mcmod.client_auto_door;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import phoupraw.mcmod.client_auto_door.config.CADConfigs;
import phoupraw.mcmod.trilevel_config.api.Configs;

@Environment(EnvType.CLIENT)
public final class ClientAutomaticDoorsClient implements ClientModInitializer {
    //@SneakyThrows
    private static void loadClasses() {
        //for (var cls : Arrays.asList(CADConfigs.class)) {
        //    MethodHandles.lookup().ensureInitialized(cls);
        //}
    }
    @Override
    public void onInitializeClient() {
        Configs.register(CADConfigs.PATH, CADConfigs.ON);
        loadClasses();
    }
}
