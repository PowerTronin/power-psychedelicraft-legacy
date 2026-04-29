/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.psychedelicraft.entity;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import ivorius.psychedelicraft.Psychedelicraft;
import ivorius.psychedelicraft.block.PSBlocks;
import ivorius.psychedelicraft.fluid.AlcoholicFluid;
import ivorius.psychedelicraft.fluid.PSFluids;
import ivorius.psychedelicraft.fluid.alcohol.DrinkTypes;
import ivorius.psychedelicraft.item.PSItems;
import ivorius.psychedelicraft.item.component.FluidCapacity;
import ivorius.psychedelicraft.item.component.ItemFluids;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.*;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

/**
 * @author Sollace
 * @since 1 Jan 2023
 */
public interface PSTradeOffers {
    RegistryKey<PointOfInterestType> DRUG_DEALER_POI = poi("drug_dealer");
    RegistryKey<PointOfInterestType> APOTHECARY_POI = poi("apothecary");
    RegistryKey<PointOfInterestType> BOTANIST_POI = poi("botanist");
    RegistryKey<PointOfInterestType> SMUGGLER_POI = poi("smuggler");
    RegistryKey<PointOfInterestType> VINTNER_POI = poi("vintner");

    VillagerProfession DRUG_DEALER_PROFESSION = register("drug_dealer",
            customJobSite(DRUG_DEALER_POI),
            customJobSite(DRUG_DEALER_POI),
            ImmutableSet.of(
                    PSItems.CANNABIS_SEEDS, PSItems.HOP_SEEDS, PSItems.TOBACCO_SEEDS,
                    PSItems.COCA_SEEDS, PSItems.COFFEA_CHERRIES, PSItems.MORNING_GLORY_SEEDS,
                    PSItems.CANNABIS_BUDS, PSItems.CANNABIS_LEAF,
                    PSItems.TOBACCO_LEAVES, PSItems.COCA_LEAVES,
                    PSItems.AGAVE_LEAF,
                    PSItems.PEYOTE, PSItems.COFFEA_CHERRIES,
                    Items.BONE_MEAL
            ),
            ImmutableSet.of(Blocks.FARMLAND),
            SoundEvents.ENTITY_WANDERING_TRADER_DRINK_POTION
    );

    VillagerProfession APOTHECARY_PROFESSION = register("apothecary",
            customJobSite(APOTHECARY_POI),
            customJobSite(APOTHECARY_POI),
            ImmutableSet.of(
                    PSItems.BELLADONNA_LEAF, PSItems.DRIED_BELLADONNA_LEAF,
                    PSItems.JIMSONWEED_LEAF, PSItems.DRIED_JIMSONWEED_LEAF,
                    PSItems.MORNING_GLORY, PSItems.MORNING_GLORY_SEEDS,
                    PSItems.BROKEN_GLASS, PSItems.OBSIDIAN_DUST
            ),
            ImmutableSet.of(PSBlocks.BUNSEN_BURNER, PSBlocks.TRAY),
            SoundEvents.ENTITY_VILLAGER_WORK_CLERIC
    );

    VillagerProfession BOTANIST_PROFESSION = register("botanist",
            customJobSite(BOTANIST_POI),
            customJobSite(BOTANIST_POI),
            ImmutableSet.of(
                    PSItems.TOMATO_SEEDS, PSItems.BELLADONNA_SEEDS, PSItems.JIMSONWEED_SEEDS,
                    PSItems.MORNING_GLORY_SEEDS, PSItems.CANNABIS_SEEDS, PSItems.HOP_SEEDS,
                    PSItems.TOBACCO_SEEDS, PSItems.COCA_SEEDS, PSItems.COFFEA_CHERRIES,
                    PSItems.WINE_GRAPES, PSItems.JUNIPER_BERRIES, Items.BONE_MEAL
            ),
            ImmutableSet.of(PSBlocks.GREENHOUSE_PLANTER, Blocks.FARMLAND),
            SoundEvents.ENTITY_VILLAGER_WORK_FARMER
    );

    VillagerProfession SMUGGLER_PROFESSION = register("smuggler",
            customJobSite(SMUGGLER_POI),
            customJobSite(SMUGGLER_POI),
            ImmutableSet.of(
                    PSItems.PAPER_BAG, PSItems.BOTTLE, PSItems.MOLOTOV_COCKTAIL,
                    PSItems.OBSIDIAN_BOTTLE, PSItems.OBSIDIAN_DUST
            ),
            ImmutableSet.of(PSBlocks.BOTTLE_RACK, PSBlocks.WALL_BOTTLE_RACK),
            SoundEvents.ENTITY_WANDERING_TRADER_DRINK_POTION
    );

    VillagerProfession VINTNER_PROFESSION = register("vintner",
            customJobSite(VINTNER_POI),
            customJobSite(VINTNER_POI),
            ImmutableSet.of(
                    PSItems.WINE_GRAPES, PSItems.HOP_CONES, PSItems.JUNIPER_BERRIES,
                    PSItems.WOODEN_MUG, PSItems.BOTTLE
            ),
            ImmutableSet.copyOf(PSBlocks.ALL_BARRELS),
            SoundEvents.BLOCK_BARREL_OPEN
    );

    VillagerProfession DRUG_ADDICT_PROFESSION = register("drug_addict",
            PointOfInterestType.NONE,
            VillagerProfession.IS_ACQUIRABLE_JOB_SITE,
            ImmutableSet.of(),
            ImmutableSet.of(),
            null
    );

    static void bootstrap() {
        registerDrugAddictOffers();

        if (customVillagerProfessionsEnabled()) {
            registerApothecaryOffers();
            registerBotanistOffers();
            registerSmugglerOffers();
            registerVintnerOffers();

            TradeOfferHelper.registerVillagerOffers(DRUG_DEALER_PROFESSION, 1, factories -> {
                factories.add(sell(1, PSItems.CANNABIS_LEAF, 1, 9, 1, 0.7f));
                factories.add(sell(1, PSItems.CANNABIS_SEEDS, 5, 12, 2, 0.5f));
                factories.add(sell(1, PSItems.HASH_MUFFIN, 2, 3, 1, 0.7f));
                factories.add(sell(1, PSItems.COCA_LEAVES, 4, 4, 1, 0.5f));
                factories.add(sell(2, PSItems.COCA_SEEDS, 4, 4, 1, 0.5f));
                factories.add(sell(1, PSItems.PEYOTE, 5, 4, 4, 0.5f));

                factories.add(sell(1, PSItems.CIGARETTE, 4, 2, 3, 0.8f));
            });
            TradeOfferHelper.registerVillagerOffers(DRUG_DEALER_PROFESSION, 2, factories -> {
                factories.add(sell(2, PSItems.DRIED_CANNABIS_BUDS, 2, 8, 2, 0.9f));
                factories.add(sell(2, PSItems.DRIED_CANNABIS_LEAF, 2, 5, 3, 0.8f));
                factories.add(sell(3, PSItems.DRIED_PEYOTE, 10, 2, 2, 0.5f));
                factories.add(sell(3, PSItems.DRIED_COCA_LEAVES, 20, 3, 2, 0.5f));

                factories.add(sell(1, PSItems.CIGAR, 5, 2, 3, 0.5f));
                factories.add(sell(1, PSItems.SMOKING_PIPE, 5, 2, 3, 0.5f));
                factories.add(sell(6, PSItems.DRYING_TABLE, 1, 2, 3, 0.5f));
            });
            TradeOfferHelper.registerVillagerOffers(DRUG_DEALER_PROFESSION, 3, factories -> {
                factories.add(sell(5, PSItems.BROWN_MAGIC_MUSHROOMS, 8, 3, 3, 0.5f));
                factories.add(sell(2, PSItems.RED_MAGIC_MUSHROOMS, 8, 3, 3, 0.5f));

                factories.add(sell(3, PSItems.SYRINGE, 4, 3, 1, 0.5f));
                factories.add(sell(3, PSItems.BONG, 4, 3, 1, 0.5f));
                factories.add(sell(1, PSItems.PEYOTE_JOINT, 3, 2, 3, 0.5f));
                factories.add(sell(2, PSItems.LSD_PILL, 3, 2, 3, 0.5f));
                factories.add(trade(3, Items.PAPER, 2, PSItems.LSA_SQUARE, 3, 2, 3));

                factories.add(sell(1, PSItems.JOINT, 2, 2, 3, 0.5f));

                if (Psychedelicraft.getConfig().enableHarmonium.get()) {
                    factories.add(new TradeOffers.SellDyedArmorFactory(PSItems.HARMONIUM, 3, 7, 2));
                }
            });
            TradeOfferHelper.registerVillagerOffers(DRUG_DEALER_PROFESSION, 4, factories -> {
                factories.add(sell(10, PSItems.CRACK_COCAINE, 7, 3, 3, 0.5f));
                factories.add(sell(13, PSItems.CRYSTAL_METH, 10, 3, 3, 0.5f));

                factories.add(sell(3, PSItems.EXTACY, 4, 3, 1, 0.5f));
                factories.add(sell(9, PSItems.HEROINE_POWDER, 4, 3, 1, 0.5f));
                factories.add(sell(2, PSItems.PEYOTE_JOINT, 3, 2, 3, 0.5f));
                factories.add(sell(3, PSItems.LSD_PILL, 6, 2, 3, 0.5f));
            });
            TradeOfferHelper.registerVillagerOffers(DRUG_DEALER_PROFESSION, 5, factories -> {
                factories.add(trade(8, PSItems.RIFT_JAR, 10, Items.DIAMOND_AXE, 1, 1, 3));
                factories.add(trade(3, PSItems.OBSIDIAN_DUST, 3, PSItems.OBSIDIAN_BOTTLE, 1, 5, 3));
            });
        }

        PointOfInterestTypes.register(Registries.POINT_OF_INTEREST_TYPE, DRUG_DEALER_POI, Stream.concat(
                        PSBlocks.DRYING_TABLE.getStateManager().getStates().stream(),
                        PSBlocks.IRON_DRYING_TABLE.getStateManager().getStates().stream()
        ).collect(Collectors.toUnmodifiableSet()), 1, 1);
        registerPoi(APOTHECARY_POI, statesOf(PSBlocks.BUNSEN_BURNER));
        registerPoi(BOTANIST_POI, statesOf(PSBlocks.GREENHOUSE_PLANTER));
        registerPoi(SMUGGLER_POI, statesOf(PSBlocks.BOTTLE_RACK, PSBlocks.WALL_BOTTLE_RACK));
        registerPoi(VINTNER_POI, statesOf(Stream.concat(PSBlocks.ALL_BARRELS.stream(), Stream.of(PSBlocks.MASH_TUB))));

        if (Psychedelicraft.getConfig().worldGeneration.get().farmerDrugDeals()) {
            TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 1, factories -> {
                factories.add(sell(2, PSItems.WINE_GRAPES, 3, 8, 1, 0.5F));
                factories.add(sell(1, PSItems.HOP_CONES, 1, 4, 1, 0.6F));
                factories.add(sell(1, PSItems.HOP_SEEDS, 1, 4, 1, 0.4F));
                factories.add(sell(1, PSItems.WOODEN_MUG, 1, 4, 1, 0.5F));
                factories.add(sell(4, PSItems.DRIED_TOBACCO, 1, 4, 1, 0.3F));
                factories.add(sell(2, PSItems.CIGARETTE, 1, 4, 1, 0.8F));
                factories.add(sell(2, PSItems.CIGAR, 1, 4, 1, 0.8F));
                factories.add(sell(2, PSItems.TOBACCO_SEEDS, 1, 4, 1, 0.3F));
                factories.add(sell(1, PSItems.COFFEE_BEANS, 1, 4, 1, 0.8F));
                factories.add(sell(1, PSItems.COFFEA_CHERRIES, 1, 4, 1, 0.6F));
            });
        }

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 3, factories -> {
            factories.add(sell(6, PSItems.BOTTLE_RACK, 1, 12, 1, 0.5F));
        });
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 4, factories -> {
            var barrels = PSItems.ALL_BARRELS.stream().map(barrel -> sell(6, barrel, 1, 12, 1, 0.5F)).toList();
            factories.add((e, r) -> barrels.get(r.nextInt(barrels.size()) % barrels.size()).create(e, r));
        });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CLERIC, 4, factories -> {
            factories.add(new PrepareFluidFactory(40, PSItems.BOTTLE, PSFluids.RED_GRAPES, 9, 1));
        });
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.BUTCHER, 4, factories -> {
            factories.add(new PrepareFluidFactory(25, PSItems.BOTTLE, PSFluids.MILK, 9, 1));
        });
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER, 4, factories -> {
            factories.add(new PrepareFluidFactory(30, PSItems.BOTTLE, PSFluids.APPLE, 9, 1));
        });
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 4, factories -> {
            factories.add(new TradeFluidFactory(30, PSItems.BOTTLE, PSFluids.RED_GRAPES, PSFluids.HONEY, 9, 1));
            factories.add(new TradeFluidFactory(25, PSItems.BOTTLE, PSFluids.MILK, PSFluids.CORN, 9, 1));
            factories.add(new TradeFluidFactory(35, PSItems.BOTTLE, PSFluids.TOMATO, PSFluids.PINEAPPLE, 9, 1));
        });
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 5, factories -> {
            factories.add(new TradeFluidFactory(45, PSItems.BOTTLE, PSFluids.WHEAT, PSFluids.BANANA, 9, 1));
            factories.add(new TradeFluidFactory(25, PSItems.BOTTLE, PSFluids.POTATO, PSFluids.PINEAPPLE, 9, 1));
            factories.add(new TradeFluidFactory(45, PSItems.BOTTLE, PSFluids.HONEY, PSFluids.RICE, 9, 1));
        });

        TradeOfferHelper.registerWanderingTraderOffers(1, factories -> {
            factories.add(sell(6, PSItems.BELLADONNA_SEEDS, 1, 1, 2, 0.8F));
            factories.add(sell(5, PSItems.COCA_SEEDS, 1, 2, 1, 0.8F));
            factories.add(sell(7, PSItems.HOP_SEEDS, 1, 2, 1, 0.8F));
            factories.add(sell(9, PSItems.CANNABIS_SEEDS, 1, 1, 3, 0.8F));
            factories.add(sell(5, PSItems.COFFEA_CHERRIES, 1, 2, 1, 0.8F));
            factories.add(sell(7, PSItems.JUNIPER_SAPLING, 1, 1, 2, 0.8F));
            factories.add(sell(9, PSItems.JIMSONWEED_SEEDS, 1, 1, 3, 0.8F));
            factories.add(sell(5, PSItems.MORNING_GLORY_SEEDS, 1, 2, 1, 0.8F));
            factories.add(sell(5, PSItems.TOBACCO_SEEDS, 1, 2, 1, 0.8F));
            factories.add(sell(19, PSItems.BLUE_CRYSTAL_METH, 10, 2, 6, 0.5F));
        });
    }

    private static void registerDrugAddictOffers() {
        TradeOfferHelper.registerVillagerOffers(DRUG_ADDICT_PROFESSION, 1, factories -> {
            factories.add(buy(5, PSItems.BROWN_MAGIC_MUSHROOMS, 8, 3, 3));
            factories.add(buy(2, PSItems.RED_MAGIC_MUSHROOMS, 8, 3, 3));
            factories.add(buy(2, PSItems.LSD_PILL, 3, 2, 3));
            factories.add(buy(1, PSItems.JOINT, 2, 2, 3));
            factories.add(buy(1, PSItems.CIGARETTE, 4, 2, 3));
            factories.add(buy(1, PSItems.CIGAR, 5, 2, 3));
            factories.add(buy(1, PSItems.HASH_MUFFIN, 2, 3, 1));

            factories.add(buy(4, PSItems.DRIED_CANNABIS_BUDS, 2, 8, 2));
            factories.add(buy(4, PSItems.DRIED_CANNABIS_LEAF, 2, 5, 3));
            factories.add(buy(5, PSItems.DRIED_PEYOTE, 10, 2, 2));
            factories.add(buy(5, PSItems.DRIED_COCA_LEAVES, 20, 3, 2));
        });
        TradeOfferHelper.registerVillagerOffers(DRUG_ADDICT_PROFESSION, 2, factories -> {
            factories.add(buy(6, PSItems.CRACK_COCAINE, 7, 4, 3));
        });
        TradeOfferHelper.registerVillagerOffers(DRUG_ADDICT_PROFESSION, 3, factories -> {
            factories.add(buy(5, PSItems.HEROINE_POWDER, 5, 5, 3));
            factories.add(buy(3, PSItems.CRYSTAL_METH, 3, 5, 6));
        });
        TradeOfferHelper.registerVillagerOffers(DRUG_ADDICT_PROFESSION, 4, factories -> {
            factories.add(buy(15, PSItems.HEROINE_POWDER, 10, 6, 3));
            factories.add(buy(12, PSItems.CRYSTAL_METH, 11, 6, 3));
        });
        TradeOfferHelper.registerVillagerOffers(DRUG_ADDICT_PROFESSION, 5, factories -> {
            factories.add(buy(5, PSItems.EXTACY, 8, 7, 3));
            factories.add(trade(3, PSItems.CRYSTAL_METH, 3, PSItems.JOLLY_RANCHER, 1, 7, 5));
        });
    }

    private static void registerApothecaryOffers() {
        TradeOfferHelper.registerVillagerOffers(APOTHECARY_PROFESSION, 1, factories -> {
            factories.add(buy(1, PSItems.BELLADONNA_LEAF, 8, 12, 2));
            factories.add(buy(1, PSItems.JIMSONWEED_LEAF, 8, 12, 2));
            factories.add(sell(2, PSItems.LABORATORY_JOURNAL, 1, 4, 2, 0.2F));
            factories.add(sell(4, PSItems.BUNSEN_BURNER, 1, 4, 3, 0.3F));
        });
        TradeOfferHelper.registerVillagerOffers(APOTHECARY_PROFESSION, 2, factories -> {
            factories.add(buy(2, PSItems.DRIED_BELLADONNA_LEAF, 6, 10, 3));
            factories.add(buy(2, PSItems.DRIED_JIMSONWEED_LEAF, 6, 10, 3));
            factories.add(sell(3, PSItems.TRAY, 1, 6, 3, 0.3F));
            factories.add(sell(2, PSItems.GLASS_TUBE, 3, 8, 2, 0.25F));
        });
        TradeOfferHelper.registerVillagerOffers(APOTHECARY_PROFESSION, 3, factories -> {
            factories.add(sell(3, PSItems.SYRINGE, 2, 8, 4, 0.35F));
            factories.add(sell(4, PSItems.LSA_SQUARE, 2, 6, 4, 0.4F));
            factories.add(trade(2, Items.PAPER, 4, PSItems.LSA_SQUARE, 1, 6, 4));
        });
        TradeOfferHelper.registerVillagerOffers(APOTHECARY_PROFESSION, 4, factories -> {
            factories.add(sell(8, PSItems.MORPHINE_TABLET, 2, 6, 5, 0.35F));
            factories.add(sell(6, PSItems.OBSIDIAN_BOTTLE, 1, 5, 5, 0.35F));
        });
        TradeOfferHelper.registerVillagerOffers(APOTHECARY_PROFESSION, 5, factories -> {
            factories.add(sell(9, PSItems.OBSIDIAN_DUST, 4, 5, 8, 0.4F));
            factories.add(sell(18, PSItems.BLUE_CRYSTAL_METH, 4, 3, 8, 0.45F));
        });
    }

    private static void registerBotanistOffers() {
        TradeOfferHelper.registerVillagerOffers(BOTANIST_PROFESSION, 1, factories -> {
            factories.add(buy(1, PSItems.TOMATO, 12, 16, 2));
            factories.add(sell(1, PSItems.TOMATO_SEEDS, 3, 12, 1, 0.2F));
            factories.add(sell(1, Items.BONE_MEAL, 8, 12, 1, 0.2F));
            factories.add(sell(4, PSItems.GREENHOUSE_PLANTER, 2, 8, 2, 0.25F));
        });
        TradeOfferHelper.registerVillagerOffers(BOTANIST_PROFESSION, 2, factories -> {
            factories.add(sell(3, PSItems.BELLADONNA_SEEDS, 2, 8, 3, 0.3F));
            factories.add(sell(3, PSItems.JIMSONWEED_SEEDS, 2, 8, 3, 0.3F));
            factories.add(sell(2, PSItems.MORNING_GLORY_SEEDS, 2, 8, 3, 0.3F));
            factories.add(sell(4, PSItems.WINE_GRAPES, 3, 8, 2, 0.3F));
        });
        TradeOfferHelper.registerVillagerOffers(BOTANIST_PROFESSION, 3, factories -> {
            factories.add(sell(4, PSItems.HOP_SEEDS, 2, 8, 4, 0.35F));
            factories.add(sell(5, PSItems.TOBACCO_SEEDS, 2, 8, 4, 0.35F));
            factories.add(sell(6, PSItems.CANNABIS_SEEDS, 2, 6, 5, 0.4F));
            factories.add(sell(6, PSItems.JUNIPER_SAPLING, 1, 5, 5, 0.35F));
        });
        TradeOfferHelper.registerVillagerOffers(BOTANIST_PROFESSION, 4, factories -> {
            factories.add(buy(3, PSItems.CANNABIS_LEAF, 10, 10, 5));
            factories.add(buy(3, PSItems.COCA_LEAVES, 10, 10, 5));
            factories.add(sell(7, PSItems.COCA_SEEDS, 2, 5, 6, 0.45F));
            factories.add(sell(7, PSItems.COFFEA_CHERRIES, 2, 5, 6, 0.45F));
        });
        TradeOfferHelper.registerVillagerOffers(BOTANIST_PROFESSION, 5, factories -> {
            factories.add(sell(10, PSItems.PEYOTE, 3, 4, 8, 0.45F));
            factories.add(sell(8, PSItems.MORNING_GLORY_LATTICE, 2, 4, 7, 0.35F));
            factories.add(sell(8, PSItems.WINE_GRAPE_LATTICE, 2, 4, 7, 0.35F));
        });
    }

    private static void registerSmugglerOffers() {
        TradeOfferHelper.registerVillagerOffers(SMUGGLER_PROFESSION, 1, factories -> {
            factories.add(sell(1, PSItems.PAPER_BAG, 2, 12, 1, 0.2F));
            factories.add(sell(2, PSItems.BOTTLE, 1, 10, 1, 0.2F));
            factories.add(sell(2, PSItems.CIGARETTE, 3, 8, 2, 0.5F));
            factories.add(buy(2, PSItems.OBSIDIAN_DUST, 4, 8, 2));
        });
        TradeOfferHelper.registerVillagerOffers(SMUGGLER_PROFESSION, 2, factories -> {
            factories.add(sell(5, PSItems.MOLOTOV_COCKTAIL, 1, 6, 3, 0.45F));
            factories.add(sell(5, PSItems.OBSIDIAN_BOTTLE, 1, 6, 3, 0.4F));
            factories.add(sell(6, PSItems.COCAINE_POWDER, 3, 5, 4, 0.45F));
        });
        TradeOfferHelper.registerVillagerOffers(SMUGGLER_PROFESSION, 3, factories -> {
            factories.add(sell(7, PSItems.CRACK_COCAINE, 4, 5, 5, 0.5F));
            factories.add(sell(8, PSItems.HEROINE_POWDER, 3, 5, 5, 0.5F));
            factories.add(sell(4, PSItems.JOLLY_RANCHER, 2, 6, 4, 0.35F));
        });
        TradeOfferHelper.registerVillagerOffers(SMUGGLER_PROFESSION, 4, factories -> {
            factories.add(sell(12, PSItems.CRYSTAL_METH, 4, 4, 6, 0.55F));
            factories.add(sell(16, PSItems.BLUE_CRYSTAL_METH, 4, 3, 7, 0.6F));
            factories.add(sell(7, PSItems.EXTACY, 3, 5, 5, 0.45F));
        });
        TradeOfferHelper.registerVillagerOffers(SMUGGLER_PROFESSION, 5, factories -> {
            factories.add(trade(3, PSItems.OBSIDIAN_DUST, 3, PSItems.OBSIDIAN_BOTTLE, 1, 7, 8));
            if (Psychedelicraft.getConfig().enableRiftJars.get()) {
                factories.add(sell(18, PSItems.RIFT_JAR, 1, 2, 10, 0.6F));
            }
        });
    }

    private static void registerVintnerOffers() {
        TradeOfferHelper.registerVillagerOffers(VINTNER_PROFESSION, 1, factories -> {
            factories.add(sell(1, PSItems.WOODEN_MUG, 2, 12, 1, 0.2F));
            factories.add(sell(2, PSItems.BOTTLE, 1, 10, 1, 0.2F));
            factories.add(sell(2, PSItems.WINE_GRAPES, 4, 10, 2, 0.25F));
            factories.add(sell(2, PSItems.HOP_CONES, 3, 10, 2, 0.25F));
        });
        TradeOfferHelper.registerVillagerOffers(VINTNER_PROFESSION, 2, factories -> {
            factories.add(sell(6, PSItems.MASH_TUB, 1, 5, 3, 0.3F));
            factories.add(sell(4, PSItems.BOTTLE_RACK, 1, 8, 3, 0.25F));
            factories.add(sellRandomBarrel(8, 6, 3, 0.3F));
        });
        TradeOfferHelper.registerVillagerOffers(VINTNER_PROFESSION, 3, factories -> {
            factories.add(new PrepareFluidFactory(8, PSItems.BOTTLE, PSFluids.RED_GRAPES, 8, 4));
            factories.add(new PrepareFluidFactory(6, PSItems.BOTTLE, PSFluids.WHEAT_HOP, 8, 4));
            factories.add(new PrepareFluidFactory(7, PSItems.BOTTLE, PSFluids.APPLE, 8, 4));
        });
        TradeOfferHelper.registerVillagerOffers(VINTNER_PROFESSION, 4, factories -> {
            factories.add(new PrepareFluidFactory(10, PSItems.BOTTLE, PSFluids.HONEY, 6, 6));
            factories.add(new PrepareFluidFactory(10, PSItems.BOTTLE, PSFluids.JUNIPER, 6, 6));
            factories.add(sell(7, PSItems.FLASK, 1, 5, 5, 0.35F));
        });
        TradeOfferHelper.registerVillagerOffers(VINTNER_PROFESSION, 5, factories -> {
            factories.add(new PrepareFluidFactory(12, PSItems.BOTTLE, PSFluids.AGAVE, 5, 8));
            factories.add(new PrepareFluidFactory(12, PSItems.BOTTLE, PSFluids.WHEAT, 5, 8));
            factories.add(sell(12, PSItems.DISTILLERY, 1, 3, 8, 0.4F));
        });
    }

    private static TradeOffers.Factory buy(int price, Item item, int count, int maxUses, int experience) {
        return (entity, random) -> new TradeOffer(new ItemStack(item, count), new ItemStack(Items.EMERALD, price), maxUses, experience, 0.05F);
    }

    private static TradeOffers.Factory sell(int price, Item item, int count, int maxUses, int experience, float priceChange) {
        return new TradeOffers.SellItemFactory(item.getDefaultStack(), price, count, maxUses, experience, priceChange);
    }

    private static TradeOffers.Factory trade(int price, ItemConvertible item, int count, Item returnItem, int returnCount, int maxUses, int experience) {
        return new TradeOffers.ProcessItemFactory(item, count, price, returnItem, returnCount, maxUses, experience);
    }

    private static TradeOffers.Factory sellRandomBarrel(int price, int maxUses, int experience, float priceChange) {
        List<TradeOffers.Factory> barrels = PSItems.ALL_BARRELS.stream()
                .map(barrel -> sell(price, barrel, 1, maxUses, experience, priceChange))
                .toList();
        return (entity, random) -> barrels.get(random.nextInt(barrels.size())).create(entity, random);
    }

    private static void registerPoi(RegistryKey<PointOfInterestType> poi, Set<BlockState> states) {
        PointOfInterestTypes.register(Registries.POINT_OF_INTEREST_TYPE, poi, states, 1, 1);
    }

    private static Set<BlockState> statesOf(Block... blocks) {
        return statesOf(Stream.of(blocks));
    }

    private static Set<BlockState> statesOf(Stream<? extends Block> blocks) {
        return blocks.flatMap(block -> block.getStateManager().getStates().stream()).collect(Collectors.toUnmodifiableSet());
    }

    private static Predicate<RegistryEntry<PointOfInterestType>> customJobSite(RegistryKey<PointOfInterestType> poi) {
        return type -> customVillagerProfessionsEnabled() && type.matchesKey(poi);
    }

    public static boolean customVillagerProfessionsEnabled() {
        return Psychedelicraft.getConfig().enableCustomVillagerProfessions.get();
    }

    public static boolean isCustomWorkstationProfession(VillagerProfession profession) {
        return profession == DRUG_DEALER_PROFESSION
                || profession == APOTHECARY_PROFESSION
                || profession == BOTANIST_PROFESSION
                || profession == SMUGGLER_PROFESSION
                || profession == VINTNER_PROFESSION;
    }

    private static RegistryKey<PointOfInterestType> poi(String id) {
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Psychedelicraft.id(id));
    }

    private static VillagerProfession register(String id, Predicate<RegistryEntry<PointOfInterestType>> heldWorkstation, Predicate<RegistryEntry<PointOfInterestType>> acquirableWorkstation, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, @Nullable SoundEvent workSound) {
        return Registry.register(Registries.VILLAGER_PROFESSION, Psychedelicraft.id(id), new VillagerProfession("psychedelicraft:" + id, heldWorkstation, acquirableWorkstation, gatherableItems, secondaryJobSites, workSound));
    }

    static class PrepareFluidFactory implements TradeOffers.Factory {
        private final int price;
        private final Item item;
        private final AlcoholicFluid fluid;
        private final List<DrinkTypes.Variant> variants;
        private final int maxUses;
        private final int experience;

        public PrepareFluidFactory(int price, Item item, AlcoholicFluid fluid, int maxUses, int experience) {
            this.price = price;
            this.fluid = fluid;
            this.variants = fluid.getVariants();
            this.item = item;
            this.maxUses = maxUses;
            this.experience = experience;
        }

        @Override
        public TradeOffer create(Entity entity, Random random) {
            DrinkTypes.Variant variant = variants.get(random.nextInt(variants.size()) % variants.size());
            ItemStack tradedItem = ItemFluids.set(new ItemStack(item, 1), fluid.getDefaultStack(FluidCapacity.get(item.getDefaultStack())));
            ItemStack soldItem = ItemFluids.set(item.getDefaultStack(), variant.predicate().state().apply(fluid.getDefaultStack(FluidCapacity.get(item.getDefaultStack()))));
            return new TradeOffer(new ItemStack(Items.EMERALD, price), tradedItem, soldItem, maxUses, experience, 0.3F);
        }
    }

    static class TradeFluidFactory implements TradeOffers.Factory {
        private final int price;
        private final Item item;
        private final AlcoholicFluid buy;
        private final AlcoholicFluid sell;

        private final int maxUses;
        private final int experience;

        public TradeFluidFactory(int price, Item item, AlcoholicFluid buy, AlcoholicFluid sell, int maxUses, int experience) {
            this.price = price;
            this.buy = buy;
            this.sell = sell;
            this.item = item;
            this.maxUses = maxUses;
            this.experience = experience;
        }

        @Override
        public TradeOffer create(Entity entity, Random random) {
            ItemStack tradedItem = ItemFluids.set(item.getDefaultStack(), buy.getDefaultStack(FluidCapacity.get(item.getDefaultStack())));
            ItemStack soldItem = ItemFluids.set(item.getDefaultStack(), sell.getDefaultStack(FluidCapacity.get(item.getDefaultStack())));
            return new TradeOffer(new ItemStack(Items.EMERALD, price), tradedItem, soldItem, maxUses, experience, 0.3F);
        }
    }
}
