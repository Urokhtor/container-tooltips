package io.urokhtor.minecraft.containertooltips

import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.network.packet.CustomPayload.Id

data class InventoryResponsePayload(val name: String, val maxSize: Int, val items: List<ItemStack>) : CustomPayload {
    override fun getId() = ID

    companion object {
        val ID: Id<InventoryResponsePayload> = CustomPayload.id("container-tooltips:inventory-response")
        val PACKET_CODEC: PacketCodec<RegistryByteBuf, InventoryResponsePayload> = object:
            PacketCodec<RegistryByteBuf, InventoryResponsePayload> {

            override fun decode(buffer: RegistryByteBuf): InventoryResponsePayload {
                val name = buffer.readString()
                val maxSize = buffer.readInt()
                val items = ItemStack.OPTIONAL_LIST_PACKET_CODEC.decode(buffer)

                return InventoryResponsePayload(
                    name = name,
                    maxSize = maxSize,
                    items = items
                )
            }

            override fun encode(buffer: RegistryByteBuf, inventoryResponsePayload: InventoryResponsePayload) {
                with(inventoryResponsePayload) {
                    buffer.writeString(name)
                    buffer.writeInt(maxSize)
                    ItemStack.OPTIONAL_LIST_PACKET_CODEC.encode(buffer, items)
                }
            }
        }
    }
}
