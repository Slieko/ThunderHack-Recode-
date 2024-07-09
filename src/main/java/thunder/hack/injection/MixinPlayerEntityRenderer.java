package thunder.hack.injection;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.core.impl.ModuleManager;
import thunder.hack.modules.client.QunixNew1;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer {
    @Inject(method = "scale(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At("HEAD"), cancellable = true)
    public void modifyPlayerScale(AbstractClientPlayerEntity player, MatrixStack matrixStack, float tickDelta, CallbackInfo ci) {
        if (ModuleManager.qunixnew1.isEnabled()) {
            long time = System.currentTimeMillis();
            float scaleFactorX = 1.0f + QunixNew1.factor.getValue() * (float) Math.sin((double) time / QunixNew1.speed.getValue());
            float scaleFactorY = 1.0f - QunixNew1.factor.getValue() * (float) Math.sin((double) time / QunixNew1.speed.getValue());
            matrixStack.scale(scaleFactorX, scaleFactorY, 1.0f);
        }
    }
}
