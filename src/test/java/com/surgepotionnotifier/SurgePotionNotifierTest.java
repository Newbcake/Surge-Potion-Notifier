package com.surgepotionnotifier;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SurgePotionNotifierTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SurgePotionNotifierPlugin.class);
		RuneLite.main(args);
	}
}