package phoupraw.mcmod.client_auto_door.config;

import com.terraformersmc.modmenu.api.ModMenuApi;

public final class ClientAutomaticDoorsModMenuApi implements ModMenuApi {
    
    //private static Screen createGameRulesScreen(Screen parent) {
    //    GameRules instance = new GameRulesInstance();
    //    for (var entry : TGameRules.INT_KEYS.entrySet()) {
    //        instance.get(entry.getValue()).set(TGameRules.INT_VALUES.getInt(entry.getKey()), null);
    //    }
    //    for (var entry : TGameRules.BOOL_KEYS.entrySet()) {
    //        instance.get(entry.getValue()).set(TGameRules.BOOL_VALUES.getBoolean(entry.getKey()), null);
    //    }
    //    for (var entry : TGameRules.DOUBLE_KEYS.entrySet()) {
    //        IDoubleRule.set(instance.get(entry.getValue()), TGameRules.DOUBLE_VALUES.getDouble(entry.getKey()));
    //    }
    //    EditGameRulesScreen screen = new EditGameRulesScreen(instance, gameRules0 -> {
    //        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
    //        if (gameRules0.isPresent() && networkHandler != null) {
    //            GameRules gameRules = gameRules0.get();
    //            for (var entry : TGameRules.INT_KEYS.entrySet()) {
    //                String name = entry.getKey();
    //                var value = gameRules.get(entry.getValue()).get();
    //                if (value != TGameRules.INT_VALUES.getInt(name)) {
    //                    networkHandler.sendCommand("gamerule " + name + " " + value);
    //                }
    //            }
    //            for (var entry : TGameRules.BOOL_KEYS.entrySet()) {
    //                String name = entry.getKey();
    //                var value = gameRules.get(entry.getValue()).get();
    //                if (value != TGameRules.BOOL_VALUES.getBoolean(name)) {
    //                    networkHandler.sendCommand("gamerule " + name + " " + value);
    //                }
    //            }
    //            for (var entry : TGameRules.DOUBLE_KEYS.entrySet()) {
    //                String name = entry.getKey();
    //                var value = gameRules.get(entry.getValue()).get();
    //                if (value != TGameRules.DOUBLE_VALUES.getDouble(name)) {
    //                    networkHandler.sendCommand("gamerule " + name + " " + value);
    //                }
    //            }
    //        }
    //        MinecraftClient.getInstance().setScreen(parent);
    //    });
    //    ClientPlayerEntity player = MinecraftClient.getInstance().player;
    //    if (player == null) {
    //        return new TipDialogScreen(parent, screen, TipDialogScreen.NO_WORLD);
    //    } else if (!player.hasPermissionLevel(2)) {
    //        return new TipDialogScreen(parent, screen, TipDialogScreen.NO_PERMISSION);
    //    }
    //    return screen;
    //}
    
    //@Override
    //public ConfigScreenFactory<?> getModConfigScreenFactory() {
    //    return parent -> {
    //        List<Screen> nexts = new ObjectArrayList<>(Collections.nCopies(3, null));
    //        SideDialogScreen dialog = new SideDialogScreen(parent, nexts);
    //        nexts.set(0, TClientConfig.createScreen(dialog));
    //        nexts.set(1, TCommonConfig.createScreen(dialog));
    //        nexts.set(2, createGameRulesScreen(dialog));
    //        return dialog;
    //    };
    //}
}
