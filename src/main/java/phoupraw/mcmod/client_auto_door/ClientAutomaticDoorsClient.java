package phoupraw.mcmod.client_auto_door;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import phoupraw.mcmod.client_auto_door.config.CADConfigs;
import phoupraw.mcmod.client_auto_door.events.BlockShapeToggler;
import phoupraw.mcmod.client_auto_door.modules.DramaticDoors;
import phoupraw.mcmod.client_auto_door.modules.Vanilla;
import phoupraw.mcmod.trilevel_config.api.Configs;

import static phoupraw.mcmod.client_auto_door.ClientAutomaticDoors.LOGGER;

@Environment(EnvType.CLIENT)
public final class ClientAutomaticDoorsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Configs.register(CADConfigs.PATH, CADConfigs.ON);
        ClientTickEvents.END_WORLD_TICK.register(Vanilla::close);
        BlockShapeToggler.LOOKUP.registerFallback(Vanilla::findTrapdoorAndFencGate);
        BlockShapeToggler.LOOKUP.registerFallback(Vanilla::findDoor);
        if (FabricLoader.getInstance().isModLoaded(DramaticDoors.MOD_ID)) {
            LOGGER.info(DramaticDoors.MOD_ID);
            BlockShapeToggler.LOOKUP.registerFallback(DramaticDoors::findShortDoor);
            BlockShapeToggler.LOOKUP.registerFallback(DramaticDoors::findTallDoor);
        }
    }
}
