package io.urokhtor.minecraft

import io.urokhtor.minecraft.Requests.INVENTORY_RESPONSE
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

object ContainertooltipsClient : ClientModInitializer {

	private val inventoryResponseHandler = InventoryResponseHandler()

	override fun onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(INVENTORY_RESPONSE) { _, _, buffer, _ ->
			run {
				buffer.readNbt()?.let {
					CurrentInventoryContext.set(inventoryResponseHandler.parseResponse(it))
				}
			}
		}
	}
}
