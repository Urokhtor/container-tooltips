package io.urokhtor.minecraft.containertooltips

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.MinecraftClient

val INVENTORY_OPENED: Event<InventoryOpenedEvent> =
    EventFactory.createArrayBacked(
        InventoryOpenedEvent::class.java
    ) { callbacks ->
        InventoryOpenedEvent { client: MinecraftClient ->
            for (event in callbacks) {
                event.onInventoryOpened(client)
            }
        }
    }

val INVENTORY_CLOSED: Event<InventoryClosedEvent> =
    EventFactory.createArrayBacked(
        InventoryClosedEvent::class.java
    ) { callbacks ->
        InventoryClosedEvent { client: MinecraftClient ->
            for (event in callbacks) {
                event.onInventoryClosed(client)
            }
        }
    }

fun interface InventoryOpenedEvent {
    fun onInventoryOpened(client: MinecraftClient)
}

fun interface InventoryClosedEvent {
    fun onInventoryClosed(client: MinecraftClient)
}
