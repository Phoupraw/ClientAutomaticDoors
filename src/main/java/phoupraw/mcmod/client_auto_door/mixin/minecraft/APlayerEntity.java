package phoupraw.mcmod.client_auto_door.mixin.minecraft;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerEntity.class)
public interface APlayerEntity {
    @Invoker
    void invokeUpdatePose();
    @Invoker
    boolean invokeCanChangeIntoPose(EntityPose pose);
}
