package com.oroarmor.portal_helper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.dimension.DimensionType;

@Mixin(DimensionType.class)
public interface DimensionTypeAccessor {
    @Accessor
    static DimensionType getOVERWORLD() {
        throw new AssertionError();
    }

    @Accessor
    static DimensionType getTHE_NETHER() {
        throw new AssertionError();
    }
}
