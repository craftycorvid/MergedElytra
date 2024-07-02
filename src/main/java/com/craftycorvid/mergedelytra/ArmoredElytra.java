package com.craftycorvid.mergedelytra;

import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class ArmoredElytra {
    public static ItemStack createChestplateElytra(ItemStack chestplate, ItemStack elytra, LocalIntRef cost,
                                                   ContainerLevelAccess access) {
        if (!(chestplate.is(ItemTags.CHEST_ARMOR) && chestplate.getItem() instanceof ArmorItem && elytra.is(Items.ELYTRA)))
            return chestplate;

        // switcheroo - chestplate + elytra is the logical order,
        // but vanilla needs it to actually be an elytra for flight to work
        ItemStack output = elytra.copy();
        // item component types are server and client synced
        // (it gets angy if the server has component types the client doesn't)
        List<ItemStack> bundleContents = new ArrayList<ItemStack>();
        bundleContents.add(chestplate);
        bundleContents.add(elytra);

        List<Component> lore = new ArrayList<>(elytra.getOrDefault(DataComponents.LORE, new ItemLore(Collections.emptyList())).lines());

        MutableComponent chestplateName = chestplate.getHoverName().copy();

        if (chestplate.has(DataComponents.CUSTOM_NAME)) chestplateName.withStyle(ChatFormatting.ITALIC);
        lore.add(Component.translatableWithFallback(MergedElytra.MOD_ID + ".elytra.chestplate", "+ %s",
                chestplateName).setStyle(Style.EMPTY.withItalic(false).applyFormat(ChatFormatting.GOLD)));

        if (chestplate.has(DataComponents.TRIM))
            chestplate.get(DataComponents.TRIM).addToTooltip(null,
                    a -> lore.add(a.copy().withStyle(a.getStyle().withItalic(false))),
                    null);
        lore.addAll(chestplate.getOrDefault(DataComponents.LORE, new ItemLore(Collections.emptyList())).lines());

        output.set(DataComponents.LORE, new ItemLore(lore));
        output.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(bundleContents));
        cost.set(cost.get() + 1);

        output.set(DataComponents.ATTRIBUTE_MODIFIERS, mergeAttributeModifiers(chestplate, elytra));

        return output;
    }

    public static ItemAttributeModifiers mergeAttributeModifiers(ItemStack... itemStacks) {
        // this is set up so that default chestplate < default elytra < custom chestplate < custom elytra
        // determines which attribute is applied (assuming you pass the itemstacks in that order)
        LinkedHashMap<Attribute, ItemAttributeModifiers.Entry> attributes = new LinkedHashMap<>();
        for (ItemStack itemStack : itemStacks) {
            if (itemStack.is(ItemTags.CHEST_ARMOR)) {
                ArmorItem armorItem = (ArmorItem) itemStack.getItem();
                armorItem.getDefaultAttributeModifiers().modifiers().forEach(a -> attributes.put(a.attribute().value(), a));
            } else {
                itemStack.getItem().components().getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY)
                        .modifiers().forEach(a -> attributes.put(a.attribute().value(), a));
            }
        }
        for (ItemStack itemStack : itemStacks) {
            ItemAttributeModifiers attributeModifiers =
                    itemStack.get(DataComponents.ATTRIBUTE_MODIFIERS);
            if (attributeModifiers == null) continue;
            attributeModifiers.modifiers().forEach(a -> attributes.put(a.attribute().value(), a));
        }
        return new ItemAttributeModifiers(attributes.values().stream().toList(), true);
    }
}
