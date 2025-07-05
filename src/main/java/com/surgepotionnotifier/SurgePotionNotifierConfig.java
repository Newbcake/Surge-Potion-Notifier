package com.surgepotionnotifier;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

import java.awt.*;

@ConfigGroup("surgepotionnotifier")
public interface SurgePotionNotifierConfig extends Config
{
	@ConfigItem(
		position = 0,
		keyName = "highlightColor",
		name = "Highlight Color",
		description = "Color to highlight your surge potion when it's ready to drink"
	)
	default Color getHighlightColor()
	{
		return new Color(0, 231, 255);
	}

	@ConfigItem(
			position = 1,
			keyName = "fillSurgePotion",
			name = "Fill Surge Potion",
			description = "Fill in the surge potion with the highlight color"
	)
	default boolean fillSurgePotion() {
		return false;
	}

	@Range(
			max = 255
	)
	@ConfigItem(
			position = 2,
			keyName =  "fillSurgePotiontOpacity",
			name = "Fill Opacity",
			description = "The opacity of the highlight color when filling in the Surge potion"
	)
	default int fillSurgePotionOpacity() {
		return 50;
	}

	@ConfigItem(
			position = 3,
			keyName = "outlineSurgePotion",
			name = "Outline Surge Potion",
			description = "Outline the surge potion with the highlight color"
	)
	default boolean outlineSurgePotion() {
		return true;
	}

	@ConfigItem(
			position = 4,
			keyName = "enableNotifier",
			name = "Enable Notifier",
			description = "Enable a system notifier when your surge potion is ready to be used"
	)
	default boolean enableNotifier() {
		return false;
	}
}
