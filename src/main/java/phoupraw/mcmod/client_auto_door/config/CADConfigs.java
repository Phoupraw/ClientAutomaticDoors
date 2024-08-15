package phoupraw.mcmod.client_auto_door.config;

import com.mojang.serialization.Codec;
import phoupraw.mcmod.client_auto_door.ClientAutomaticDoors;
import phoupraw.mcmod.trilevel_config.api.ConfigKey;
import phoupraw.mcmod.trilevel_config.api.SimpleConfigKey;

import java.nio.file.Path;

public interface CADConfigs {
    Path PATH = Path.of(ClientAutomaticDoors.ID + ".json");
    ConfigKey<Boolean> ON = new SimpleConfigKey<>("on", Codec.BOOL, true);
}
