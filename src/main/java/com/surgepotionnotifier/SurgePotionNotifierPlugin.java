package com.surgepotionnotifier;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemVariationMapping;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
	name = "Surge Potion Notifier"
)
public class SurgePotionNotifierPlugin extends Plugin {
	private Collection<Integer> SURGE_POTION_VARIATION_IDS;
	private final List<Integer> SURGE_POTION_ITEM_IDS = List.of(
			ItemID._1DOSESURGE,
			ItemID._2DOSESURGE,
			ItemID._3DOSESURGE,
			ItemID._4DOSESURGE
	);

	private Collection<Integer> getAllVariations(List<Integer> itemIds) {
		return itemIds.stream()
				.map(ItemVariationMapping::getVariations)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	public Collection<Integer> getSurgePotionVariationIds() {
		return SURGE_POTION_VARIATION_IDS;
	}

	@Inject
	private Client client;

	@Inject
	private SurgePotionNotifierConfig config;

	@Inject
	private SurgePotionNotifierOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Notifier notifier;

	private int surgePotionTimer = 50;
	private boolean cooldownDisabled = false;

	@Override
	protected void startUp() throws Exception {
		SURGE_POTION_VARIATION_IDS = getAllVariations(SURGE_POTION_ITEM_IDS);
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception {
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged) {
		int varbitId = varbitChanged.getVarbitId();

		if (varbitId == VarbitID.SURGE_POTION_TIMER) {
			surgePotionTimer = client.getVarbitValue(VarbitID.SURGE_POTION_TIMER);

			if (surgePotionTimer == 0 && !cooldownDisabled) {
				notifyUser();
			}
		}

		if (varbitId == VarbitID.BUFF_SURGE_POTION_COOLDOWN_DISABLED) {
			cooldownDisabled = client.getVarbitValue(VarbitID.BUFF_SURGE_POTION_COOLDOWN_DISABLED) == 1;

			if (cooldownDisabled) {
				notifyUser();
			}
		}
	}

	public boolean isSurgeOnCooldown() {
		return surgePotionTimer > 0 && !cooldownDisabled;
	}

	@Provides
	SurgePotionNotifierConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(SurgePotionNotifierConfig.class);
	}

	private void notifyUser() {
		if (config.enableNotifier()) {
			ItemContainer playerInventory = client.getItemContainer(InventoryID.INV);

			if (playerInventory != null && SURGE_POTION_VARIATION_IDS.stream().anyMatch(playerInventory::contains)) {
				notifier.notify("You can drink another dose of a surge potion");
			}
		}
	}
}
