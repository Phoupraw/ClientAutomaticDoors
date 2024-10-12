package phoupraw.mcmod.client_auto_door;

import lombok.SneakyThrows;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import phoupraw.mcmod.client_auto_door.config.CADConfigs;
import phoupraw.mcmod.client_auto_door.misc.DoorOpening;
import phoupraw.mcmod.client_auto_door.modules.DramaticDoors;
import phoupraw.mcmod.client_auto_door.modules.Vanilla;
import phoupraw.mcmod.trilevel_config.api.Configs;

import java.lang.invoke.MethodHandles;

@Environment(EnvType.CLIENT)
public final class ClientAutomaticDoorsClient implements ClientModInitializer {
    @SneakyThrows
    private static void loadClass(Class<?> cls) {
        MethodHandles.lookup().ensureInitialized(cls);
    }
    @Override
    public void onInitializeClient() {
        Configs.register(CADConfigs.PATH, CADConfigs.ON);
        loadClass(Vanilla.class);
        loadClass(DramaticDoors.class);
        ClientTickEvents.START_WORLD_TICK.register(DoorOpening::onStartTick);
    }
}
