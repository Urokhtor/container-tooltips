package io.urokhtor.minecraft.containertooltips

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

object ContainerTooltips : ModInitializer {

    private val inventoryRequestHandler = InventoryRequestHandler()

    override fun onInitialize() {
        PayloadTypeRegistry.serverboundPlay().register(InventoryRequestPayload.ID, InventoryRequestPayload.PACKET_CODEC)
        PayloadTypeRegistry.clientboundPlay().register(InventoryResponsePayload.ID, InventoryResponsePayload.PACKET_CODEC)
        ServerPlayNetworking.registerGlobalReceiver(InventoryRequestPayload.ID) { payload, context ->
            inventoryRequestHandler.createResponse(context.player(), payload.blockPos)?.let {
                context.responseSender().sendPacket(it)
            }
        }
    }
}
