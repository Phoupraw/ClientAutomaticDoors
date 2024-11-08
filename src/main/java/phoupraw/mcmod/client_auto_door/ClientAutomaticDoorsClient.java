package phoupraw.mcmod.client_auto_door;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import phoupraw.mcmod.client_auto_door.config.CADConfigs;
import phoupraw.mcmod.client_auto_door.events.BlockShapeToggler;
import phoupraw.mcmod.client_auto_door.events.ShouldNotOpen;
import phoupraw.mcmod.client_auto_door.misc.DoorToggler;
import phoupraw.mcmod.client_auto_door.misc.OpenToggler;
import phoupraw.mcmod.client_auto_door.modules.Attachment;
import phoupraw.mcmod.trilevel_config.api.Configs;

@Environment(EnvType.CLIENT)
public final class ClientAutomaticDoorsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Configs.register(CADConfigs.PATH, CADConfigs.ON);
        ShouldNotOpen.EVENT.register(Attachment::shouldNotOpen);
        //ClientTickEvents.START_WORLD_TICK.register(DoorOpening::onStartTick);
        BlockShapeToggler.LOOKUP.registerFallback(OpenToggler::find);
        BlockShapeToggler.LOOKUP.registerFallback(DoorToggler::find);
    }
}
