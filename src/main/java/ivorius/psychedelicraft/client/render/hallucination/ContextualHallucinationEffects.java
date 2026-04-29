package ivorius.psychedelicraft.client.render.hallucination;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ivorius.psychedelicraft.entity.drug.Drug;
import ivorius.psychedelicraft.entity.drug.DrugProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class ContextualHallucinationEffects {
    private static final String[] FAKE_CHAT_MESSAGES = {
            "message.psychedelicraft.hallucination.fake_chat.0",
            "message.psychedelicraft.hallucination.fake_chat.1",
            "message.psychedelicraft.hallucination.fake_chat.2",
            "message.psychedelicraft.hallucination.fake_chat.3",
            "message.psychedelicraft.hallucination.fake_chat.4"
    };

    private static final SoundEvent[] FOOTSTEP_SOUNDS = {
            SoundEvents.BLOCK_STONE_STEP,
            SoundEvents.BLOCK_GRAVEL_STEP,
            SoundEvents.BLOCK_GRASS_STEP,
            SoundEvents.BLOCK_WOOD_STEP
    };

    private final List<FakeOre> fakeOres = new ArrayList<>();

    private ClientWorld world;
    private int fakeChatCooldown;
    private int fakeFootstepCooldown;
    private int fakeOreCooldown;
    private int forcedMirageCooldown;

    public void update(DrugProperties properties) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            reset();
            return;
        }

        if (world != client.world) {
            reset();
            world = client.world;
        }

        float strength = getStrength(properties);
        Random random = properties.asEntity().getRandom();

        updateFakeOres(client.world);

        if (strength < 0.2F) {
            return;
        }

        tickFakeChat(client, random, strength);
        tickFakeFootsteps(client, random, strength);
        tickFakeOres(client, random, strength);
        tickForcedMirages(properties, random, strength);
    }

    public void renderFakeOres(MatrixStack matrices, VertexConsumerProvider vertices, Camera camera, float tickDelta, DrugProperties properties) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || fakeOres.isEmpty() || getStrength(properties) < 0.2F) {
            return;
        }

        Vec3d cameraPos = camera.getPos();
        for (FakeOre ore : fakeOres) {
            if (ore.shouldFlicker(tickDelta)) {
                continue;
            }

            matrices.push();
            matrices.translate(ore.pos().getX() - cameraPos.x, ore.pos().getY() - cameraPos.y, ore.pos().getZ() - cameraPos.z);
            matrices.translate(0.5F, 0.5F, 0.5F);
            float scale = 1.002F + MathHelper.sin((ore.age() + tickDelta) * 0.17F) * 0.0015F;
            matrices.scale(scale, scale, scale);
            matrices.translate(-0.5F, -0.5F, -0.5F);

            int light = WorldRenderer.getLightmapCoordinates(client.world, ore.pos());
            client.getBlockRenderManager().renderBlockAsEntity(ore.state(), matrices, vertices, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }
    }

    private void tickFakeChat(MinecraftClient client, Random random, float strength) {
        if (fakeChatCooldown > 0) {
            fakeChatCooldown--;
            return;
        }

        if (random.nextFloat() < strength * 0.02F) {
            String key = FAKE_CHAT_MESSAGES[random.nextInt(FAKE_CHAT_MESSAGES.length)];
            client.inGameHud.getChatHud().addMessage(Text.translatable(key).formatted(Formatting.GRAY, Formatting.ITALIC));
            fakeChatCooldown = 500 + random.nextInt(900);
        }
    }

    private void tickFakeFootsteps(MinecraftClient client, Random random, float strength) {
        if (fakeFootstepCooldown > 0) {
            fakeFootstepCooldown--;
            return;
        }

        if (random.nextFloat() < strength * 0.08F) {
            SoundEvent sound = FOOTSTEP_SOUNDS[random.nextInt(FOOTSTEP_SOUNDS.length)];
            double angle = random.nextDouble() * Math.PI * 2D;
            double distance = 2.5D + random.nextDouble() * 7D;
            Entity player = client.player;
            double x = player.getX() + MathHelper.cos((float)angle) * distance;
            double z = player.getZ() + MathHelper.sin((float)angle) * distance;
            client.world.playSound(x, player.getY(), z, sound, SoundCategory.PLAYERS, 0.35F + strength * 0.3F, 0.7F + random.nextFloat() * 0.6F, false);
            fakeFootstepCooldown = 45 + random.nextInt(120);
        }
    }

    private void tickFakeOres(MinecraftClient client, Random random, float strength) {
        if (fakeOreCooldown > 0) {
            fakeOreCooldown--;
            return;
        }

        if (fakeOres.size() < 7 && random.nextFloat() < strength * 0.3F) {
            spawnFakeOre(client.world, client.player.getBlockPos(), random);
        }
        fakeOreCooldown = 20 + random.nextInt(80);
    }

    private void tickForcedMirages(DrugProperties properties, Random random, float strength) {
        if (forcedMirageCooldown > 0) {
            forcedMirageCooldown--;
            return;
        }

        if (random.nextFloat() < strength * 0.025F) {
            properties.getHallucinations().getEntities().spawnHallucination();
            forcedMirageCooldown = 300 + random.nextInt(600);
        }
    }

    private void spawnFakeOre(ClientWorld world, BlockPos origin, Random random) {
        for (int i = 0; i < 20; i++) {
            BlockPos pos = origin.add(random.nextBetween(-13, 13), random.nextBetween(-6, 6), random.nextBetween(-13, 13));
            BlockState host = world.getBlockState(pos);
            if (!isOreHost(host) || !hasVisibleFace(world, pos) || hasFakeOre(pos)) {
                continue;
            }

            fakeOres.add(new FakeOre(pos.toImmutable(), pickOre(host, random), 80 + random.nextInt(180)));
            return;
        }
    }

    private void updateFakeOres(ClientWorld world) {
        Iterator<FakeOre> iterator = fakeOres.iterator();
        while (iterator.hasNext()) {
            FakeOre ore = iterator.next();
            ore.tick();
            if (ore.isDead() || !isOreHost(world.getBlockState(ore.pos()))) {
                iterator.remove();
            }
        }
    }

    private boolean hasVisibleFace(ClientWorld world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (world.getBlockState(pos.offset(direction)).isAir()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasFakeOre(BlockPos pos) {
        return fakeOres.stream().anyMatch(ore -> ore.pos().equals(pos));
    }

    private boolean isOreHost(BlockState state) {
        return state.isOf(Blocks.STONE)
                || state.isOf(Blocks.DEEPSLATE)
                || state.isOf(Blocks.NETHERRACK);
    }

    private BlockState pickOre(BlockState host, Random random) {
        if (host.isOf(Blocks.NETHERRACK)) {
            return switch (random.nextInt(3)) {
                case 0 -> Blocks.ANCIENT_DEBRIS.getDefaultState();
                case 1 -> Blocks.NETHER_GOLD_ORE.getDefaultState();
                default -> Blocks.NETHER_QUARTZ_ORE.getDefaultState();
            };
        }
        if (host.isOf(Blocks.DEEPSLATE)) {
            return switch (random.nextInt(5)) {
                case 0 -> Blocks.DEEPSLATE_DIAMOND_ORE.getDefaultState();
                case 1 -> Blocks.DEEPSLATE_EMERALD_ORE.getDefaultState();
                case 2 -> Blocks.DEEPSLATE_GOLD_ORE.getDefaultState();
                case 3 -> Blocks.DEEPSLATE_REDSTONE_ORE.getDefaultState();
                default -> Blocks.DEEPSLATE_LAPIS_ORE.getDefaultState();
            };
        }
        return switch (random.nextInt(5)) {
            case 0 -> Blocks.DIAMOND_ORE.getDefaultState();
            case 1 -> Blocks.EMERALD_ORE.getDefaultState();
            case 2 -> Blocks.GOLD_ORE.getDefaultState();
            case 3 -> Blocks.REDSTONE_ORE.getDefaultState();
            default -> Blocks.LAPIS_ORE.getDefaultState();
        };
    }

    private float getStrength(DrugProperties properties) {
        return MathHelper.clamp(properties.getHallucinations().get(Drug.CONTEXTUAL_HALLUCINATION_STRENGTH), 0, 1);
    }

    private void reset() {
        fakeOres.clear();
        fakeChatCooldown = 0;
        fakeFootstepCooldown = 0;
        fakeOreCooldown = 0;
        forcedMirageCooldown = 0;
    }

    private static final class FakeOre {
        private static final int FLICKER_WINDOW = 6;

        private final BlockPos pos;
        private final BlockState state;
        private final int maxAge;
        private int age;

        FakeOre(BlockPos pos, BlockState state, int maxAge) {
            this.pos = pos;
            this.state = state;
            this.maxAge = maxAge;
        }

        BlockPos pos() {
            return pos;
        }

        BlockState state() {
            return state;
        }

        int age() {
            return age;
        }

        void tick() {
            age++;
        }

        boolean isDead() {
            return age >= maxAge;
        }

        boolean shouldFlicker(float tickDelta) {
            int fadeIn = Math.min(age, maxAge - age);
            if (fadeIn > 25) {
                return false;
            }
            return ((int)((age + tickDelta) / FLICKER_WINDOW)) % 2 == 0;
        }
    }
}
