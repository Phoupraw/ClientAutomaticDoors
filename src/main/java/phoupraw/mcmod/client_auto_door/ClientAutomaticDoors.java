package phoupraw.mcmod.client_auto_door;

import lombok.SneakyThrows;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import phoupraw.mcmod.client_auto_door.events.ToggledBlockState;
import phoupraw.mcmod.client_auto_door.modules.DramaticDoors;
import phoupraw.mcmod.client_auto_door.modules.Vanilla;

import java.lang.invoke.MethodHandles;

public final class ClientAutomaticDoors implements ModInitializer {
    public static final String ID = "client_auto_door";
    @ApiStatus.Internal
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String NAME_KEY = "modmenu.nameTranslation." + ID;
    public static @NotNull MutableText name() {
        return Text.translatable(NAME_KEY);
    }
    @SneakyThrows
    static void loadClass(Class<?> cls) {
        MethodHandles.lookup().ensureInitialized(cls);
    }
    @Override
    public void onInitialize() {
        ToggledBlockState.EVENT.register(Vanilla::door);
        ToggledBlockState.EVENT.register(Vanilla::trapdoor);
        ToggledBlockState.EVENT.register(Vanilla::fenceGate);
        if (FabricLoader.getInstance().isModLoaded(DramaticDoors.MOD_ID)) {
            ToggledBlockState.EVENT.register(DramaticDoors::tallDoor);
            ToggledBlockState.EVENT.register(DramaticDoors::shortDoor);
        }
    }
}
