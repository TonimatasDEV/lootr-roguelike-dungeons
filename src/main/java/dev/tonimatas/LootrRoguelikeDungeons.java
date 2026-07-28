package dev.tonimatas;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LootrRoguelikeDungeons implements ModInitializer {
	public static final String MOD_ID = "lootr-roguelike-dungeons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Lootr: Roguelike Dungeons compatibility has been started correctly!");
	}
}
