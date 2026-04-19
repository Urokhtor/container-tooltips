package io.urokhtor.minecraft.containertooltips

import net.minecraft.world.item.AirItem
import net.minecraft.world.item.ItemStack

data class Container(val name: String, val inventory: List<ItemStack>) {
    fun isEmpty() = inventory
        .none { inventory -> inventory.item !is AirItem }
}

object CurrentContainerContext {

    private var container: Container? = null

    fun get(): Container? {
        return container
    }

    fun set(container: Container) {
        this.container = container
    }

    fun reset() {
        this.container = null
    }
}
