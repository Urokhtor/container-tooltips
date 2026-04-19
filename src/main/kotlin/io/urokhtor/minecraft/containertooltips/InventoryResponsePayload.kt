package io.urokhtor.minecraft.containertooltips

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.item.ItemStack

data class InventoryResponsePayload(val name: String, val maxSize: Int, val items: List<ItemStack>) : CustomPacketPayload {
    override fun type() = ID

    companion object {
        val ID: CustomPacketPayload.Type<InventoryResponsePayload> = CustomPacketPayload.createType("container-tooltips/inventory-response")
        val PACKET_CODEC: StreamCodec<RegistryFriendlyByteBuf, InventoryResponsePayload> = object:
            StreamCodec<RegistryFriendlyByteBuf, InventoryResponsePayload> {

            override fun decode(buffer: RegistryFriendlyByteBuf): InventoryResponsePayload {
                val name = buffer.readUtf()
                val maxSize = buffer.readInt()
                val items = ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(buffer)

                return InventoryResponsePayload(
                    name = name,
                    maxSize = maxSize,
                    items = items
                )
            }

            override fun encode(buffer: RegistryFriendlyByteBuf, inventoryResponsePayload: InventoryResponsePayload) {
                with(inventoryResponsePayload) {
                    buffer.writeUtf(name)
                    buffer.writeInt(maxSize)
                    ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(buffer, items)
                }
            }
        }
    }
}
