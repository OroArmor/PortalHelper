package com.oroarmor.portal_helper.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.OverlayTexture;

@Mixin(OverlayTexture.class)
public class OverlayTextureMixin {
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;activeTexture(I)V", shift = At.Shift.BEFORE))
    public void set99blue(CallbackInfo info){
        ((OverlayTextureAccessor) this).getTexture().getImage().setPixelColor(9, 9, 0xBBFFE200);
    }
}
