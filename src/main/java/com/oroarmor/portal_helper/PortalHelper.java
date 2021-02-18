package com.oroarmor.portal_helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.oroarmor.portal_helper.mixin.DimensionTypeAccessor;
import org.lwjgl.glfw.GLFW;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class PortalHelper implements ClientModInitializer {
    public static String MOD_ID = "portal-helper";

    public static KeyBinding portalKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.portal-helper.create-portal", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N, "category.portal-helper.portal-helper"));

    public static Map<DimensionType, List<BlockPos>> portalPositions = new HashMap<>();

    public static final List<DimensionType> VALID_DIMENSIONS= new ImmutableList.Builder<DimensionType>().add(DimensionTypeAccessor.getOVERWORLD(), DimensionTypeAccessor.getTHE_NETHER()).build();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_WORLD_TICK.register(clientWorld -> {
            while (portalKeyBind.wasPressed()) {
                DimensionType type = clientWorld.getDimension();

                if(!VALID_DIMENSIONS.contains(type)) {
                    return;
                }

                assert MinecraftClient.getInstance().player != null;
                BlockPos position = MinecraftClient.getInstance().player.getBlockPos();
                if (!portalPositions.containsKey(type)) {
                    portalPositions.put(type, new ArrayList<>());
                }
                portalPositions.get(type).add(position);
            }
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register(worldRenderContext -> {
            MatrixStack stack = worldRenderContext.matrixStack();
            stack.push();

            if(!VALID_DIMENSIONS.contains(worldRenderContext.world().getDimension())) {
                return;
            }

            for (BlockPos pos : portalPositions.getOrDefault(getOppositeDimension(worldRenderContext.world()), new ArrayList<>())) {
                worldRenderContext.worldRenderer().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.OBSIDIAN.getDefaultState()), true, true, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
            }

            stack.pop();
        });
    }

    private DimensionType getOppositeDimension(ClientWorld world) {
        return world.getDimension() == DimensionTypeAccessor.getOVERWORLD() ? DimensionTypeAccessor.getTHE_NETHER() : DimensionTypeAccessor.getOVERWORLD();
    }
}
