package ivorius.psychedelicraft.item;

import java.util.Objects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class LaboratoryJournalItem extends Item {
    private static Runnable clientOpener = () -> {};

    public LaboratoryJournalItem(Settings settings) {
        super(settings);
    }

    public static void setClientOpener(Runnable opener) {
        clientOpener = Objects.requireNonNull(opener);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient()) {
            clientOpener.run();
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(stack, world.isClient());
    }
}
