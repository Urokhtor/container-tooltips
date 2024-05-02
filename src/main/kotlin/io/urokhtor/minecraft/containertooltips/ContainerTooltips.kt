package io.urokhtor.minecraft.containertooltips

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.registry.BuiltinRegistries

object ContainerTooltips : ModInitializer {

    private val inventoryRequestHandler = InventoryRequestHandler()

    override fun onInitialize() {
        PayloadTypeRegistry.playC2S().register(InventoryRequestPayload.ID, InventoryRequestPayload.PACKET_CODEC)
        PayloadTypeRegistry.playC2S().register(InventoryResponsePayload.ID, InventoryResponsePayload.PACKET_CODEC)
        ServerPlayNetworking.registerGlobalReceiver(InventoryRequestPayload.ID) { payload, context ->
            val registryWrapperLookup = BuiltinRegistries.createWrapperLookup()
            inventoryRequestHandler.createResponse(context.player(), registryWrapperLookup, payload.blockPos)?.let {
                context.responseSender().sendPacket(InventoryResponsePayload(it))
            }
        }
    }
}
