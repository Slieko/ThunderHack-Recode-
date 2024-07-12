package thunder.hack.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;
import org.lwjgl.opengl.GL11;
import thunder.hack.ThunderHack;
import thunder.hack.modules.Module;
import thunder.hack.modules.client.HudEditor;
import thunder.hack.setting.Setting;
import thunder.hack.setting.impl.ColorSetting;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.Render3DEngine;

import java.awt.*;

public class ChinaHat extends Module {
    public ChinaHat() {
        super("ChinaHat", Module.Category.RENDER);
    }

    private final Setting<Float> radius = new Setting<>("Radius", 0.55f, 0.1f, 1f);
    private final Setting<ColorMode> colorMode = new Setting<>("ColorMode", ColorMode.Sync);
    private final Setting<ColorSetting> color = new Setting<>("ColorMode", new ColorSetting(Color.GRAY.getRGB()), v -> colorMode.is(ColorMode.Custom));

    private enum ColorMode {
        Sync, Custom
    }

    public void onRender3D(MatrixStack stack) {
        for (PlayerEntity pl : ThunderHack.asyncManager.getAsyncPlayers()) {
            if (mc.options.getPerspective() == Perspective.FIRST_PERSON && pl == mc.player)
                continue;

            float lineX = (float) (pl.prevX + (pl.getX() - pl.prevX) * mc.getTickDelta());
            float lineY = (float) (pl.prevY + (pl.getY() - pl.prevY) * mc.getTickDelta() + pl.getEyeHeight(pl.getPose()) + 0.2f);
            float lineZ = (float) (pl.prevZ + (pl.getZ() - pl.prevZ) * mc.getTickDelta());

            float x = lineX - (float) mc.getEntityRenderDispatcher().camera.getPos().getX();
            float y = lineY - (float) mc.getEntityRenderDispatcher().camera.getPos().getY();
            float z = lineZ - (float) mc.getEntityRenderDispatcher().camera.getPos().getZ();

            stack.push();
            RenderSystem.disableCull();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);


            stack.translate(x, y - 0.3f, z);
            stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(pl.getYaw()));
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pl.getPitch()));
            stack.translate(-x, -(y - 0.3f), -z);


            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            for (int i = 0; i <= 32; i++) {
                int c = colorMode.is(ColorMode.Sync) ? Render2DEngine.applyOpacity(HudEditor.getColor(i * 12).getRGB(), 0.7f) : color.getValue().getColor();
                bufferBuilder.vertex(stack.peek().getPositionMatrix(), x, y + 0.2f, z).color(c).next();

                float x2 = (float) (x - Math.sin(i * Math.PI * 2f / 32f) * radius.getValue());
                float z2 = (float) (z + Math.cos(i * Math.PI * 2f / 32f) * radius.getValue());

                bufferBuilder.vertex(stack.peek().getPositionMatrix(), x2, y, z2).color(c).next();
            }
            tessellator.draw();


            Render3DEngine.endRender();
            RenderSystem.enableCull();
            RenderSystem.disableDepthTest();
            stack.pop();
        }
    }
}