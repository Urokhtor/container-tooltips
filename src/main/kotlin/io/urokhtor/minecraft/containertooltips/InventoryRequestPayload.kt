package io.urokhtor.minecraft.containertooltips

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

data class InventoryRequestPayload(val blockPos: BlockPos) : CustomPacketPayload {
    override fun type() = ID

    companion object {
        val ID: CustomPacketPayload.Type<InventoryRequestPayload> =
            CustomPacketPayload.createType("container-tooltips/inventory-request")
        val PACKET_CODEC: StreamCodec<ByteBuf, InventoryRequestPayload> = object :
            StreamCodec<ByteBuf, InventoryRequestPayload> {
            override fun decode(buf: ByteBuf): InventoryRequestPayload {
                return InventoryRequestPayload(BlockPos.STREAM_CODEC.decode(buf))
            }

            override fun encode(buf: ByteBuf, value: InventoryRequestPayload) {
                BlockPos.STREAM_CODEC.encode(buf, value.blockPos)
            }
        }
    }
}
