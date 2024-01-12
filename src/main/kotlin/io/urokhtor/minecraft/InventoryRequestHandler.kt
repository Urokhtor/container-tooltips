package io.urokhtor.minecraft

import net.minecraft.block.ChestBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.block.entity.EnderChestBlockEntity
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtInt
import net.minecraft.nbt.NbtString
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos

private const val MAX_SIZE = "MaxSize"
private const val ITEMS = "Items"
private const val NAME = "Name"

class InventoryRequestHandler {

    fun createResponse(player: ServerPlayerEntity, blockPosition: BlockPos): NbtCompound? {
        return when (val blockEntity: BlockEntity? = player.serverWorld.getBlockEntity(blockPosition)) {
            is ChestBlockEntity -> readChestInventory(player, blockEntity)
            is LootableContainerBlockEntity -> readGenericInventory(blockEntity)
            is EnderChestBlockEntity -> readEnderChestInventory(player)
            else -> null
        }
    }

    /**
     * Reads contents of a chest container. Double chests are supported by using [ChestBlock.getInventory]. They are
     * also the reason why [readGenericInventory] does not work for chests, because each side of a double chest has a
     * separate inventory. Without this, the client would get only one half of double chest's inventory.
     */
    private fun readChestInventory(
        player: ServerPlayerEntity,
        blockEntity: ChestBlockEntity
    ): NbtCompound {
        val blockState = blockEntity.cachedState
        val inventory = ChestBlock.getInventory(
            blockState.block as ChestBlock,
            blockState,
            player.serverWorld,
            blockEntity.pos,
            true
        )

        val defaultedList = DefaultedList.ofSize(inventory!!.size(), ItemStack.EMPTY);
        IntRange(0, inventory.size() - 1).map { slot ->
            defaultedList.set(slot, inventory.getStack(slot))
        }

        val nbtCompound = NbtCompound()
        nbtCompound.put(MAX_SIZE, NbtInt.of(inventory.size()))
        Inventories.writeNbt(nbtCompound, defaultedList)
        nbtCompound.put(NAME, NbtString.of(blockEntity.displayName.asTruncatedString(32)))
        return nbtCompound
    }

    private fun readGenericInventory(blockEntity: LootableContainerBlockEntity): NbtCompound {
        val nbtCompound = blockEntity.createNbt()
        nbtCompound.put(MAX_SIZE, NbtInt.of(blockEntity.size()))
        nbtCompound.put(NAME, NbtString.of(blockEntity.displayName.asTruncatedString(32)))
        return nbtCompound
    }

    private fun readEnderChestInventory(player: ServerPlayerEntity): NbtCompound {
        val nbtList = player.enderChestInventory.toNbtList()
        val nbtCompound = NbtCompound()
        nbtCompound.put(ITEMS, nbtList)
        nbtCompound.put(MAX_SIZE, NbtInt.of(player.enderChestInventory.size()))
        nbtCompound.put(NAME, NbtString.of(Text.translatable("container.enderchest").asTruncatedString(32)))
        return nbtCompound
    }
}
