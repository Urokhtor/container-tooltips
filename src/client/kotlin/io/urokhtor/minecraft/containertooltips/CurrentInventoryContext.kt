package io.urokhtor.minecraft.containertooltips

import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList

data class Container(val name: String, val inventory: DefaultedList<ItemStack>)

object CurrentInventoryContext {

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
