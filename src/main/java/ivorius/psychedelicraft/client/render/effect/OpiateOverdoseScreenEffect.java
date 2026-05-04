package ivorius.psychedelicraft.client.render.effect;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import ivorius.psychedelicraft.client.render.RenderUtil;
import ivorius.psychedelicraft.entity.drug.DrugProperties;
import ivorius.psychedelicraft.entity.drug.DrugType;
import ivorius.psychedelicraft.util.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

/**
 * A high-dose opiate breakdown effect: dark stutters, harsh color ghosts, and broken frame slices.
 */
public class OpiateOverdoseScreenEffect implements ScreenEffect {
    private static final int MIN_LINGER_TICKS = 20 * 35;
    private static final int FADE_TICKS = 20 * 5;

    private static final int[] BREAKCORE_COLORS = {
            0xFFFF18D8,
            0xFF37FF4F,
            0xFF1D8BFF,
            0xFFFF2F2F,
            0xFFFFFF4C
    };

    private float prevStrength;
    private float strength;
    private int lingerTicks;
    private SimpleFramebuffer snapshot;

    @Override
    public boolean shouldApply(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevStrength, strength) > 0.01F;
    }

    @Override
    public void update(float tickDelta) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        prevStrength = strength;
        if (player == null || !player.isAlive()) {
            lingerTicks = 0;
            prevStrength = 0;
            strength = 0;
            return;
        }
        if (MinecraftClient.getInstance().isPaused()) {
            return;
        }

        DrugProperties properties = DrugProperties.of(player);
        float morphine = properties.getDrugValue(DrugType.MORPHINE);
        float doseBreakdown = MathUtils.project(morphine, 0.58F, 1F);
        float cardiacBreakdown = properties.getCardiacArrestProgress(1) * MathUtils.project(morphine, 0.35F, 0.75F);
        float target = MathHelper.clamp(Math.max(doseBreakdown, cardiacBreakdown), 0, 1);

        if (target > 0.01F) {
            lingerTicks = MIN_LINGER_TICKS;
        } else if (lingerTicks > 0) {
            lingerTicks--;
        }

        if (lingerTicks > 0) {
            float lingerStrength = MathHelper.clamp(lingerTicks / (float)FADE_TICKS, 0, 1) * 0.62F;
            target = Math.max(target, lingerStrength);
        }

        strength = MathUtils.approach(strength, target, target > strength ? 0.08F : 0.035F);
    }

    @Override
    public void render(DrawContext context, Window window, float tickDelta) {
        float opacity = MathHelper.clamp(MathHelper.lerp(tickDelta, prevStrength, strength), 0, 1);
        if (opacity <= 0.01F) {
            return;
        }

        int width = window.getScaledWidth();
        int height = window.getScaledHeight();
        ensureSnapshot(width, height);
        sampleScreen();

        PlayerEntity player = MinecraftClient.getInstance().player;
        int age = player == null ? 0 : player.age;
        float ticks = age + tickDelta;
        Random stutter = new Random((long)(ticks * 11F) * 0x6A09E667F3BCC909L);

        float frameDrop = stutter.nextFloat() < opacity * 0.45F ? stutter.nextFloat() : 0;
        float blackout = MathHelper.clamp(opacity * 0.22F + frameDrop * opacity * 0.6F, 0, 0.88F);

        drawColorGhosts(width, height, opacity, ticks, stutter);
        drawGlitchBars(context, width, height, opacity, ticks);

        if (blackout > 0) {
            context.fill(0, 0, width, height, argb(blackout, 0, 0, 0));
        }

        float pulse = MathHelper.square(MathHelper.sin(ticks * 0.33F)) * opacity;
        context.fill(0, 0, width, height, argb(opacity * 0.08F + pulse * 0.12F, 8, 16, 18));

        if (frameDrop > 0.45F) {
            int color = BREAKCORE_COLORS[stutter.nextInt(BREAKCORE_COLORS.length)];
            context.fill(0, 0, width, height, withAlpha(color, opacity * 0.08F));
        }
    }

    private void drawColorGhosts(int width, int height, float opacity, float ticks, Random random) {
        if (snapshot == null) {
            return;
        }

        float beat = 0.65F + MathHelper.square(MathHelper.sin(ticks * 0.73F)) * 0.75F;
        int colorOffset = MathHelper.floor(ticks / 2F);
        for (int i = 0; i < 3; i++) {
            int color = BREAKCORE_COLORS[(colorOffset + i) % BREAKCORE_COLORS.length];
            float direction = i == 1 ? -1 : 1;
            float x = direction * (width * (0.012F + random.nextFloat() * 0.035F) * opacity);
            float y = (random.nextFloat() - 0.5F) * height * 0.025F * opacity;
            float scale = 1F + (i + 1) * 0.015F * opacity;
            drawSnapshot(x, y, scale, color, MathHelper.clamp(opacity * (0.07F + i * 0.025F) * beat, 0, 0.26F), true);
        }
    }

    private void drawGlitchBars(DrawContext context, int width, int height, float opacity, float ticks) {
        Random random = new Random((long)(ticks * 24F) ^ 0x510E527FADE682D1L);
        int bars = MathHelper.floor(5 + opacity * 18);

        for (int i = 0; i < bars; i++) {
            if (random.nextFloat() > opacity * 0.72F) {
                continue;
            }

            int y = random.nextInt(Math.max(1, height));
            int h = 1 + random.nextInt(Math.max(2, height / 28));
            int xShift = MathHelper.floor((random.nextFloat() - 0.5F) * width * 0.16F * opacity);
            int x0 = Math.max(0, xShift);
            int x1 = Math.min(width, width + xShift);

            if (random.nextFloat() < 0.48F) {
                context.fill(0, y, width, Math.min(height, y + h), argb(opacity * (0.18F + random.nextFloat() * 0.35F), 0, 0, 0));
            } else {
                int color = BREAKCORE_COLORS[random.nextInt(BREAKCORE_COLORS.length)];
                context.fill(x0, y, x1, Math.min(height, y + h), withAlpha(color, opacity * (0.08F + random.nextFloat() * 0.2F)));
            }
        }
    }

    private void ensureSnapshot(int width, int height) {
        if (snapshot != null && (snapshot.viewportWidth != width || snapshot.viewportHeight != height)) {
            close();
        }

        if (snapshot == null) {
            snapshot = new SimpleFramebuffer(width, height, false, MinecraftClient.IS_SYSTEM_MAC);
            snapshot.viewportWidth = width;
            snapshot.viewportHeight = height;
        }
    }

    private void sampleScreen() {
        if (snapshot == null) {
            return;
        }

        Framebuffer input = MinecraftClient.getInstance().getFramebuffer();
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, input.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, snapshot.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, input.textureWidth, input.textureHeight, 0, 0,
                snapshot.textureWidth, snapshot.textureHeight, GL11.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
        input.beginWrite(true);
    }

    private void drawSnapshot(float xOffset, float yOffset, float scale, int color, float alpha, boolean additive) {
        if (snapshot == null || alpha <= 0) {
            return;
        }

        float width = snapshot.viewportWidth;
        float height = snapshot.viewportHeight;
        float scaledWidth = width * scale;
        float scaledHeight = height * scale;
        float x0 = (width - scaledWidth) * 0.5F + xOffset;
        float y0 = (height - scaledHeight) * 0.5F + yOffset;
        float x1 = x0 + scaledWidth;
        float y1 = y0 + scaledHeight;

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, snapshot.getColorAttachment());
        RenderSystem.setShaderColor(
                ColorHelper.Argb.getRed(color) / 255F,
                ColorHelper.Argb.getGreen(color) / 255F,
                ColorHelper.Argb.getBlue(color) / 255F,
                alpha
        );
        RenderSystem.enableBlend();
        if (additive) {
            RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ONE, DstFactor.ZERO);
        } else {
            RenderSystem.defaultBlendFunc();
        }

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(x0, y1, RenderUtil.SCREEN_Z_OFFSET).texture(0, 1)
              .vertex(x1, y1, RenderUtil.SCREEN_Z_OFFSET).texture(1, 1)
              .vertex(x1, y0, RenderUtil.SCREEN_Z_OFFSET).texture(1, 0)
              .vertex(x0, y0, RenderUtil.SCREEN_Z_OFFSET).texture(0, 0);
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    private static int argb(float alpha, int red, int green, int blue) {
        return ColorHelper.Argb.getArgb((int)(MathHelper.clamp(alpha, 0, 1) * 255), red, green, blue);
    }

    private static int withAlpha(int color, float alpha) {
        return ColorHelper.Argb.getArgb(
                (int)(MathHelper.clamp(alpha, 0, 1) * 255),
                ColorHelper.Argb.getRed(color),
                ColorHelper.Argb.getGreen(color),
                ColorHelper.Argb.getBlue(color)
        );
    }

    @Override
    public void close() {
        if (snapshot != null) {
            snapshot.delete();
            snapshot = null;
        }
    }
}
