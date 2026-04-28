/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.psychedelicraft.block;

import net.minecraft.block.*;
import net.minecraft.state.property.IntProperty;

public class CocaPlantBlock extends CannabisPlantBlock {
    public static final IntProperty AGE_12 = IntProperty.of("age", 0, 12);
    public static final int AGE_12_MAX = 12;

    public CocaPlantBlock(Settings settings) {
        super(settings);
    }

    @Override
    public IntProperty getAgeProperty() {
        return AGE_12;
    }

    @Override
    public int getMaxAge(BlockState state) {
        return AGE_12_MAX;
    }

    @Override
    protected float getRandomGrowthChance() {
        return 0.1F;
    }

}
