package com.craftycorvid.mergedelytra.mixin;

import com.craftycorvid.mergedelytra.ArmoredElytra;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    public AnvilMenuMixin(MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    // we want to skip vanilla's code if we have a chestplate + elytra
    @ModifyExpressionValue(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1))
    private boolean checkChestplateElytra(boolean original, @Local(ordinal = 1) ItemStack itemStack2,
                                          @Local(ordinal = 2) ItemStack itemStack3) {
        return original || (itemStack2.is(ItemTags.CHEST_ARMOR) && itemStack3.is(Items.ELYTRA));
    }

    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1))
    private void createChestplateElytra(CallbackInfo ci, @Local(ordinal = 1) LocalRef<ItemStack> itemStack2,
                                        @Local(ordinal = 2) ItemStack itemStack3,
                                        @Local(ordinal = 0) LocalIntRef cost) {
        itemStack2.set(ArmoredElytra.createChestplateElytra(itemStack2.get(), itemStack3, cost, this.access));
    }
}
