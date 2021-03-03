package com.oroarmor.portal_helper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.dimension.DimensionType;

@Mixin(DimensionType.class)
public interface DimensionTypeAccessor {
    @Accessor("OVERWORLD")
    static DimensionType getOverworld() {
        throw new AssertionError();
    }

    @Accessor("THE_NETHER")
    static DimensionType getTheNether() {
        throw new AssertionError();
    }
}
