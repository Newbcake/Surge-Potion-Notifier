package com.surgepotionnotifier;

import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

class SurgePotionNotifierOverlay extends WidgetItemOverlay {
    private final ItemManager itemManager;
    private final SurgePotionNotifierPlugin plugin;
    private final SurgePotionNotifierConfig config;

    @Inject
    private SurgePotionNotifierOverlay(ItemManager itemManager, SurgePotionNotifierPlugin plugin, SurgePotionNotifierConfig config) {
        this.itemManager = itemManager;
        this.plugin = plugin;
        this.config = config;
        showOnInventory();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget) {
        if (plugin.getSurgePotionVariationIds().contains(itemId) && !plugin.isSurgeOnCooldown()) {
            Color color = config.getHighlightColor();
            if (color != null) {
                Rectangle bounds = itemWidget.getCanvasBounds();
                if (config.outlineSurgePotion()) {
                    final BufferedImage outline = itemManager.getItemOutline(itemId, itemWidget.getQuantity(), color);
                    graphics.drawImage(outline, (int) bounds.getX(), (int) bounds.getY(), null);
                }

                if (config.fillSurgePotion()) {
                    final Color fillColor = ColorUtil.colorWithAlpha(color, config.fillSurgePotionOpacity());
                    Image image = ImageUtil.fillImage(itemManager.getImage(itemId, itemWidget.getQuantity(), false), fillColor);
                    graphics.drawImage(image, (int) bounds.getX(), (int) bounds.getY(), null);
                }
            }
        }
    }
}
