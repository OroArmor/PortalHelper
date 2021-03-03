package com.oroarmor.portal_helper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.texture.NativeImageBackedTexture;

@Mixin(OverlayTexture.class)
public interface OverlayTextureAccessor {
    @Accessor
    NativeImageBackedTexture getTexture();
}
