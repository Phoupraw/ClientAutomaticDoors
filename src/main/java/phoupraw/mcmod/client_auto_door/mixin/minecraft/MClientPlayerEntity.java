package phoupraw.mcmod.client_auto_door.mixin.minecraft;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phoupraw.mcmod.client_auto_door.mixins.minecraft.MMClientPlayerEntity;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
abstract class MClientPlayerEntity extends AbstractClientPlayerEntity {
    @Shadow
    @Final
    protected MinecraftClient client;
    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;
    @Shadow
    public abstract StatHandler getStatHandler();
    @Shadow
    public abstract ClientRecipeBook getRecipeBook();
    @Shadow
    private boolean lastSneaking;
    @Shadow
    private boolean lastSprinting;
    public MClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }
    @Inject(method = "move", at = @At("HEAD"))
    private void openDoor(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        //World world =getWorld();
        //var aabbs = Utils.getAABBs(this);
        //ClientPlayerEntity copy = new ClientPlayerEntity(client, clientWorld, networkHandler, getStatHandler(), getRecipeBook(), lastSneaking, lastSprinting);
        
        //adjustMovementForCollisions(this,movement)
        
        MMClientPlayerEntity.openDoor(client, (ClientPlayerEntity) (Object) this, movementType, movement);
    }
}
