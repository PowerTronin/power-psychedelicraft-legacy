package ivorius.psychedelicraft.item.component;

import java.util.Optional;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import ivorius.psychedelicraft.fluid.AlcoholicFluid;
import ivorius.psychedelicraft.util.compat.PacketCodec;
import ivorius.psychedelicraft.util.compat.PacketCodecs;
import ivorius.psychedelicraft.util.compat.StackCompat;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public record DrinkLabelComponent(long bottledDay, int quality, String barrelTranslationKey, String signedBy) implements TooltipAppender {
    public static final Codec<DrinkLabelComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("bottledDay").forGetter(DrinkLabelComponent::bottledDay),
            Codec.INT.fieldOf("quality").forGetter(DrinkLabelComponent::quality),
            Codec.STRING.fieldOf("barrelTranslationKey").forGetter(DrinkLabelComponent::barrelTranslationKey),
            Codec.STRING.fieldOf("signedBy").forGetter(DrinkLabelComponent::signedBy)
    ).apply(instance, DrinkLabelComponent::new));
    public static final PacketCodec<PacketByteBuf, DrinkLabelComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_LONG, DrinkLabelComponent::bottledDay,
            PacketCodecs.INTEGER, DrinkLabelComponent::quality,
            PacketCodecs.STRING, DrinkLabelComponent::barrelTranslationKey,
            PacketCodecs.STRING, DrinkLabelComponent::signedBy,
            DrinkLabelComponent::new
    );

    public static Optional<DrinkLabelComponent> get(ItemStack stack) {
        if (ItemFluids.of(stack).isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(StackCompat.get(stack, PSComponents.DRINK_LABEL));
    }

    public static ItemStack apply(ItemStack stack, World world, PlayerEntity player, BlockState barrelState, ItemFluids fluids) {
        if (!stack.isEmpty() && !fluids.isEmpty()) {
            StackCompat.set(stack, PSComponents.DRINK_LABEL, new DrinkLabelComponent(
                    Math.max(0, world.getTimeOfDay() / 24000L),
                    getQuality(fluids),
                    barrelState.getBlock().getTranslationKey(),
                    player.getName().getString()
            ));
        }
        return stack;
    }

    private static int getQuality(ItemFluids fluids) {
        if (fluids.fluid() instanceof AlcoholicFluid) {
            int fermentation = AlcoholicFluid.FERMENTATION.get(fluids);
            int maturation = AlcoholicFluid.MATURATION.get(fluids);
            int distillation = AlcoholicFluid.DISTILLATION.get(fluids);
            int quality = fermentation * 18 + maturation * 5 + distillation * 3;
            if (AlcoholicFluid.VINEGAR.get(fluids)) {
                quality = Math.min(20, quality / 3);
            }
            return MathHelper.clamp(quality, 0, 100);
        }
        return 20;
    }

    @Override
    public void appendTooltip(TooltipContext context, Consumer<Text> tooltip) {
        tooltip.accept(Text.translatable("psychedelicraft.drink_label.header").formatted(Formatting.GOLD));
        tooltip.accept(Text.translatable("psychedelicraft.drink_label.bottled_day", bottledDay).formatted(Formatting.GRAY));
        tooltip.accept(Text.translatable("psychedelicraft.drink_label.quality", getQualityName(), quality).formatted(Formatting.GRAY));
        tooltip.accept(Text.translatable("psychedelicraft.drink_label.barrel", Text.translatable(barrelTranslationKey)).formatted(Formatting.GRAY));
        tooltip.accept(Text.translatable("psychedelicraft.drink_label.signed_by", signedBy).formatted(Formatting.GRAY));
    }

    private Text getQualityName() {
        String name = quality >= 85 ? "excellent"
                : quality >= 65 ? "good"
                : quality >= 40 ? "fair"
                : quality >= 15 ? "rough"
                : "poor";
        return Text.translatable("psychedelicraft.drink_label.quality." + name);
    }
}
