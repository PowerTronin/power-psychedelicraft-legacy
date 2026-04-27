package ivorius.psychedelicraft.mixin.client.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.texture.Sprite;

@Pseudo
@Mixin(targets = {
        "me.jellysquid.mods.sodium.client.render.texture.SpriteUtil"
}, remap = false)
public interface JellySquidSpriteUtil {
    @Invoker("markSpriteActive")
    static void invokeMarkSpriteActive(Sprite sprite) {

    }
}
