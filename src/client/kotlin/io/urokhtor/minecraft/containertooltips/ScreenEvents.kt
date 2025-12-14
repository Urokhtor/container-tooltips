package io.urokhtor.minecraft.containertooltips

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.client.MinecraftClient

val SCREEN_OPENED: Event<ScreenOpenedEvent> =
    EventFactory.createArrayBacked(
        ScreenOpenedEvent::class.java
    ) { callbacks ->
        ScreenOpenedEvent { client: MinecraftClient ->
            for (event in callbacks) {
                event.onScreenOpened(client)
            }
        }
    }

val SCREEN_CLOSED: Event<ScreenClosedEvent> =
    EventFactory.createArrayBacked(
        ScreenClosedEvent::class.java
    ) { callbacks ->
        ScreenClosedEvent { client: MinecraftClient ->
            for (event in callbacks) {
                event.onScreenClosed(client)
            }
        }
    }

fun interface ScreenOpenedEvent {
    fun onScreenOpened(client: MinecraftClient)
}

fun interface ScreenClosedEvent {
    fun onScreenClosed(client: MinecraftClient)
}
