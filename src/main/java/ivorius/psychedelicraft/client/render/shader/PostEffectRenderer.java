package ivorius.psychedelicraft.client.render.shader;

import java.util.*;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;

import ivorius.psychedelicraft.client.PsychedelicraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import org.slf4j.Logger;

public class PostEffectRenderer {
    private static final Logger LOGGER = LogUtils.getLogger();

    private List<LoadedShader> shaders = new ArrayList<>();
    private boolean disabledAfterRenderError;

    public void render(float tickDelta) {
        if (!disabledAfterRenderError && PsychedelicraftClient.getConfig().shader2DEnabled.get()) {
            try {
                renderShaders(tickDelta);
            } catch (Throwable t) {
                disabledAfterRenderError = true;
                LOGGER.error("Disabling Psychedelicraft 2D shader effects after a render failure.", t);
                shaders.forEach(PostEffectProcessor::close);
                shaders = List.of();
                restoreMainFramebuffer();
            }
        }
    }

    private void renderShaders(float tickDelta) {
        RenderSystem.enableDepthTest();

        if (shaders.size() == 1) {
            shaders.get(0).render(tickDelta);
        } else {
            shaders.forEach(shader -> shader.render(tickDelta));
        }

        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
        RenderSystem.disableDepthTest();
    }

    private void restoreMainFramebuffer() {
        try {
            MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
            RenderSystem.disableDepthTest();
        } catch (Throwable ignored) {
        }
    }

    public void setupDimensions(int width, int height) {
        shaders.forEach(shader -> shader.setupDimensions(width, height));
    }

    public void onShadersLoaded(List<LoadedShader> shaders) {
        List<LoadedShader> oldShaders = this.shaders;
        this.shaders = shaders;
        disabledAfterRenderError = false;
        oldShaders.forEach(PostEffectProcessor::close);
    }
}
