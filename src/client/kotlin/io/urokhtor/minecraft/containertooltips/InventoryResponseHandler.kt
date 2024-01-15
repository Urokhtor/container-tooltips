package io.urokhtor.minecraft.containertooltips

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.util.collection.DefaultedList

class InventoryResponseHandler {

    fun parseAsContainer(nbtInventory: NbtCompound): Container {
        val maxSize = nbtInventory.getInt(Responses.MAX_SIZE)
        val inventory: DefaultedList<ItemStack> = DefaultedList.ofSize(maxSize, ItemStack.EMPTY)
        val items = nbtInventory.get(Responses.ITEMS)
        if (items is NbtList) {
            items.map {
                if (it is NbtCompound) {
                    val itemStack = ItemStack.fromNbt(it)
                    val slot = it.getInt("Slot")
                    inventory[slot] = itemStack
                }
            }
        }

        return Container(nbtInventory.getString(Responses.NAME), inventory)
    }
}
