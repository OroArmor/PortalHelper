/*
 * MIT License
 *
 * Copyright (c) 2021 OroArmor (Eli Orona)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.oroarmor.portal_helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.oroarmor.portal_helper.mixin.DimensionTypeAccessor;
import com.oroarmor.portal_helper.mixin.DynamicRegistryManagerAccessor;
import org.lwjgl.glfw.GLFW;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public class PortalHelper implements ClientModInitializer {
    public static String MOD_ID = "portal-helper";

    public static final KeyBinding PORTAL_KEY_BIND = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.portal-helper.create-portal", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N, "category.portal-helper.portal-helper"));

    public static final Map<DimensionType, List<BlockPos>> PORTAL_POSITIONS = new HashMap<>();

    public static final List<DimensionType> VALID_DIMENSIONS = new ImmutableList.Builder<DimensionType>().add(DimensionTypeAccessor.getOverworld(), DimensionTypeAccessor.getTheNether()).build();

    public static String[] pattern = new String[]{"XOOX",
            "OPPO",
            "OPPO",
            "OPPO",
            "XOOX"};

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_WORLD_TICK.register(clientWorld -> {
            while (PORTAL_KEY_BIND.wasPressed()) {
                DimensionType type = clientWorld.getDimension();

                if (!VALID_DIMENSIONS.contains(type)) {
                    return;
                }

                assert MinecraftClient.getInstance().player != null;
                ClientPlayerEntity playerEntity = MinecraftClient.getInstance().player;
                BlockPos position = playerEntity.getBlockPos();
                if (!PORTAL_POSITIONS.containsKey(type)) {
                    PORTAL_POSITIONS.put(type, new ArrayList<>());
                }
                PORTAL_POSITIONS.get(type).add(position);
                double dimensionScale = DimensionType.method_31109(type, getOppositeDimension(type));
                playerEntity.sendChatMessage(String.format("Portal created at %d, %d, %d in %s", (int) (dimensionScale * position.getX()), position.getY(), (int) (dimensionScale * position.getZ()), DynamicRegistryManagerAccessor.getBuiltIn().get(Registry.DIMENSION_TYPE_KEY).getId(getOppositeDimension(type))));
            }
        });

        WorldRenderEvents.BEFORE_ENTITIES.register(worldRenderContext -> {
            MatrixStack stack = worldRenderContext.matrixStack();

            DimensionType dimensionType = worldRenderContext.world().getDimension();
            if (!VALID_DIMENSIONS.contains(dimensionType)) {
                return;
            }

            stack.push();
            Vec3d cameraPosition = worldRenderContext.camera().getPos();
            stack.translate(-cameraPosition.getX(), -cameraPosition.getY(), -cameraPosition.getZ());
            for (BlockPos pos : PORTAL_POSITIONS.getOrDefault(getOppositeDimension(dimensionType), new ArrayList<>())) {
                double dimensionScale = DimensionType.method_31109(getOppositeDimension(dimensionType), dimensionType);
                for (int row = pattern.length - 1; row > -1; row--) {
                    for (int j = 0; j < pattern[row].length(); j++) {
                        BlockState state;
                        switch (pattern[row].charAt(j)) {
                            case 'O':
                                state = Blocks.OBSIDIAN.getDefaultState();
                                break;
                            case 'P':
                                state = Blocks.NETHER_PORTAL.getDefaultState();
                                break;
                            default:
                                continue;
                        }
                        stack.push();
                        stack.translate((int)(dimensionScale * pos.getX()) + j, pos.getY() + row, (int)(dimensionScale * pos.getZ()));
                        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(state, stack, worldRenderContext.consumers(), 15, OverlayTexture.packUv(9, 9));
                        stack.pop();
                    }
                }
            }

            stack.pop();
        });
    }

    private DimensionType getOppositeDimension(DimensionType dimensionType) {
        return dimensionType == DimensionTypeAccessor.getOverworld() ? DimensionTypeAccessor.getTheNether() : DimensionTypeAccessor.getOverworld();
    }
}
