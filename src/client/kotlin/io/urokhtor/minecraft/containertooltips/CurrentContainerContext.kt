package io.urokhtor.minecraft.containertooltips

import net.minecraft.item.AirBlockItem
import net.minecraft.item.ItemStack

data class Container(val name: String, val inventory: List<ItemStack>) {
    fun isEmpty() = inventory
        .none { inventory -> inventory.item !is AirBlockItem }
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
