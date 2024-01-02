package io.urokhtor.minecraft

import net.minecraft.item.ItemStack

object CurrentInventoryContext {

    private var inventory: List<ItemStack>? = null

    fun get(): List<ItemStack>? {
        return inventory
    }

    fun set(inventory: List<ItemStack>) {
        this.inventory = inventory
    }

    fun reset() {
        this.inventory = null
    }
}
