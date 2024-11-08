package phoupraw.mcmod.client_auto_door.mixin.minecraft;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phoupraw.mcmod.client_auto_door.mixins.minecraft.MMClientPlayerEntity;

@Environment(EnvType.CLIENT)
@Mixin(value = ClientPlayerEntity.class, priority = 10000)
abstract class MClientPlayerEntity extends AbstractClientPlayerEntity {
    @Shadow
    @Final
    protected MinecraftClient client;
    public MClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }
    @Inject(method = "move", at = @At("HEAD"))
    private void openDoor(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        MMClientPlayerEntity.openDoor((ClientPlayerEntity) (Object) this, movementType, movement, client);
    }
}
