package thunder.hack.injection;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thunder.hack.core.impl.ModuleManager;
import thunder.hack.modules.client.QunixNew1;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer {
    @Shadow
    protected abstract void scale(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f);

    @Shadow
    public abstract void render(LivingEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int packedLight);

    @Inject(method = "scale(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At("HEAD"), cancellable = true)
    private void modifyPlayerScale(AbstractClientPlayerEntity player, MatrixStack matrixStack, float tickDelta, CallbackInfo ci) {
        if (ModuleManager.qunixnew1.isEnabled()) {
            long time = System.currentTimeMillis();
            float scaleFactorX = 1.0f + QunixNew1.factor.getValue() * (float) Math.sin((double) time / QunixNew1.speed.getValue());
            float scaleFactorY = 1.0f - QunixNew1.factor.getValue() * (float) Math.sin((double) time / QunixNew1.speed.getValue());
            float scaleFactorZ = 1.0f;
            matrixStack.scale(scaleFactorX, scaleFactorY, scaleFactorZ);
            ci.cancel();
        }
    }
}
