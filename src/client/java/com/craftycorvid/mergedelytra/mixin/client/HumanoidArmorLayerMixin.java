package com.craftycorvid.mergedelytra.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {
    @ModifyVariable(method = "renderArmorPiece", at = @At("STORE"), ordinal = 0)
    private ItemStack replaceElytraWithChestplate(ItemStack itemStack) {
        if (!itemStack.is(Items.ELYTRA) || Minecraft.getInstance().player == null) return itemStack;
        Iterable<ItemStack> bundleContents = itemStack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY).items();

        for (ItemStack item : bundleContents) {
            if (item.is(ItemTags.CHEST_ARMOR)) {
                return item;
            }
        }
        return itemStack;
    }
}
