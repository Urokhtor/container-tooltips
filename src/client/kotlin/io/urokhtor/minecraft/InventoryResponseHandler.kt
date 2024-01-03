package io.urokhtor.minecraft

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.util.collection.DefaultedList

class InventoryResponseHandler {

    fun parseResponse(nbtInventory: NbtCompound): DefaultedList<ItemStack> {
        val maxSize = nbtInventory.getInt("MaxSize")
        val inventory: DefaultedList<ItemStack> = DefaultedList.ofSize(maxSize, ItemStack.EMPTY)
        val items = nbtInventory.get("Items")
        if (items is NbtList) {
            items.map {
                if (it is NbtCompound) {
                    val itemStack = ItemStack.fromNbt(it)
                    val slot = it.getInt("Slot")
                    inventory[slot] = itemStack
                }
            }
        }

        return inventory
    }
}
