package com.keuin.watchdog.monitor.plugin;

import com.keuin.watchdog.WatchdogMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;

public class ItemAutoCleanerPlugin implements MonitorPlugin {
    private short incidenceCounter = 0;
    @Override
    public void accept(Double tps) {
        if (tps <= 4)
            ++incidenceCounter;
        else
            incidenceCounter = 0;
        if (incidenceCounter > 1) {
            // trigger
            System.out.println("TOO SLOW!! tps=" + tps);
            cleanDroppedItems();
        }
    }

    private void cleanDroppedItems() {
        var server = WatchdogMod.server;
        for (ServerWorld world : server.getWorlds()) {
            // get all items, compute count by types
            var entities = world.getEntitiesByType(EntityType.ITEM, entity -> true);
            final Map<Item, Integer> map = new HashMap<>();
            for (Entity entity : entities) {
                var name = ((ItemEntity)entity).getStack().getItem();
                map.put(name, map.getOrDefault(name, 0) + 1);
            }
            // select the type of most items
            int maxCount = -1;
            Item maxItem = null;
            for (Map.Entry<Item, Integer> entry : map.entrySet()) {
                if (entry.getValue() > maxCount)
                    maxItem = entry.getKey();
            }
            // remove dropped items with the type of most entities
            if (maxItem != null) {
                Item finalMaxItem = maxItem;
                world.getEntitiesByType(EntityType.ITEM, entity ->
                        ((ItemEntity)entity).getStack().getItem() == finalMaxItem)
                        .forEach(Entity::kill);
            }
        }
        System.out.println("Killed items");
    }
}
