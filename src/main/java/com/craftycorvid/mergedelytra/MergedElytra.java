package com.craftycorvid.mergedelytra;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MergedElytra implements ModInitializer {
	public static final String MOD_ID = "mergedelytra";
    public static final Logger LOGGER = LoggerFactory.getLogger("Merged Elytra");

	@Override
	public void onInitialize() {
		LOGGER.info("Merged Elytra Loaded!");
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
