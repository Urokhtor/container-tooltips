package io.urokhtor.minecraft

import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList

object CurrentInventoryContext {

    private var inventory: DefaultedList<ItemStack>? = null

    fun get(): DefaultedList<ItemStack>? {
        return inventory
    }

    fun set(inventory: DefaultedList<ItemStack>) {
        this.inventory = inventory
    }

    fun reset() {
        this.inventory = null
    }
}
