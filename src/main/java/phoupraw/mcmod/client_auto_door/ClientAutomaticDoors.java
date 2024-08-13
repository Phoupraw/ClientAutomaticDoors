package phoupraw.mcmod.client_auto_door;

import net.fabricmc.api.ModInitializer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class ClientAutomaticDoors implements ModInitializer {
    public static final String ID = "client_auto_door";
    @ApiStatus.Internal
    public static final Logger LOGGER = LogManager.getLogger(ID);
    public static final String NAME_KEY = "modmenu.nameTranslation." + ID;
    public static @NotNull MutableText name() {
        return Text.translatable(NAME_KEY);
    }
    @Override
    public void onInitialize() {
    
    }
}
