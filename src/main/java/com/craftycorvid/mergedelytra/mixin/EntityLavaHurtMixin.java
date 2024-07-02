package com.craftycorvid.mergedelytra.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;

@Mixin(Entity.class)
public abstract class EntityLavaHurtMixin {
    @Inject(method = "lavaHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", ordinal = 0))
    private void splitNetheriteInLava(CallbackInfo ci) {
        if ((Entity) (Object) this instanceof ItemEntity) {
            ItemStack itemStack = ((ItemEntity) (Object) this).getItem();
            if (itemStack.is(Items.ELYTRA)) {
                Iterable<ItemStack> bundleContents = itemStack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY).items();
                bundleContents.forEach(item -> {
                    if (item.is(Items.NETHERITE_CHESTPLATE)) {
                        ((ItemEntity) (Object) this).setItem(item);
                        return;
                    }
                });
            }
        }
    }
}
