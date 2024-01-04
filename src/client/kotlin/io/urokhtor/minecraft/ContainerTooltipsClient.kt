package io.urokhtor.minecraft

import io.urokhtor.minecraft.Requests.INVENTORY_RESPONSE
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient

object ContainerTooltipsClient : ClientModInitializer {

	private val inventoryResponseHandler = InventoryResponseHandler()
	private val inventoryTooltip = InventoryTooltip()

	override fun onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(INVENTORY_RESPONSE) { _, _, buffer, _ ->
			run {
				buffer.readNbt()?.let {
					CurrentInventoryContext.set(inventoryResponseHandler.parseResponse(it))
				}
			}
		}

		HudRenderCallback.EVENT.register { guiGraphics, _ ->
			CurrentInventoryContext.get()?.let {
				val client = MinecraftClient.getInstance()
				inventoryTooltip.render(client.textRenderer, client.window.scaledWidth / 2, guiGraphics, it)
			}
		}
	}
}
