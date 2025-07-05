/*
 * Copyright (c) 2018 kulers
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.surgepotionnotifier;

import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

class SurgePotionNotifierOverlay extends WidgetItemOverlay
{
    private final ItemManager itemManager;
    private final SurgePotionNotifierPlugin plugin;
    private final SurgePotionNotifierConfig config;

    private final Cache<Long, Image> fillCache;
    private final Cache<Long, BufferedImage> outlineCache;

    @Inject
    private SurgePotionNotifierOverlay(ItemManager itemManager, SurgePotionNotifierPlugin plugin, SurgePotionNotifierConfig config)
    {
        this.itemManager = itemManager;
        this.plugin = plugin;
        this.config = config;

        this.fillCache = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .maximumSize(32)
                .build();

        this.outlineCache = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .maximumSize(32)
                .build();

        showOnInventory();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget)
    {
        if (plugin.getSurgePotionVariationIds().contains(itemId) && !plugin.isSurgeOnCooldown())
        {
            Color color = config.getHighlightColor();
            if (color == null)
            {
                return;
            }

            Rectangle bounds = itemWidget.getCanvasBounds();

            if (config.outlineSurgePotion())
            {
                BufferedImage outline = getOutlineImage(itemId, itemWidget.getQuantity(), color);
                if (outline != null)
                {
                    graphics.drawImage(outline, bounds.x, bounds.y, null);
                }
            }

            if (config.fillSurgePotion())
            {
                Image filled = getFillImage(itemId, itemWidget.getQuantity(), color);
                if (filled != null)
                {
                    graphics.drawImage(filled, bounds.x, bounds.y, null);
                }
            }
        }
    }

    private BufferedImage getOutlineImage(int itemId, int quantity, Color color)
    {
        long key = (((long) itemId) << 32) | quantity;
        try
        {
            return outlineCache.get(key, () -> itemManager.getItemOutline(itemId, quantity, color));
        }
        catch (ExecutionException e)
        {
            return null;
        }
    }

    private Image getFillImage(int itemId, int quantity, Color color)
    {
        long key = (((long) itemId) << 32) | quantity;
        try
        {
            return fillCache.get(key, () -> {
                Color fillColor = ColorUtil.colorWithAlpha(color, config.fillSurgePotionOpacity());
                return ImageUtil.fillImage(itemManager.getImage(itemId, quantity, false), fillColor);
            });
        }
        catch (ExecutionException e)
        {
            return null;
        }
    }

    public void invalidateCache()
    {
        fillCache.invalidateAll();
        outlineCache.invalidateAll();
    }
}
