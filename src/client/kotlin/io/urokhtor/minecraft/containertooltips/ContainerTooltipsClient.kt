package io.urokhtor.minecraft.containertooltips

import io.urokhtor.minecraft.containertooltips.configuration.Configuration
import io.urokhtor.minecraft.containertooltips.rendering.ContainerTooltip
import io.urokhtor.minecraft.containertooltips.rendering.EmptyContainerTooltip
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier

object ContainerTooltipsClient : ClientModInitializer {

	private val containerTooltip = ContainerTooltip()
	private val emptyContainerTooltip = EmptyContainerTooltip()
	private lateinit var configuration: Configuration
	private val tooltipIdentifier = Identifier.of("container-tooltips", "tooltip")
	private var inventoryOpen = false

	override fun onInitializeClient() {
		registerConfigurationIntegration()
		registerMessageListeners()
		registerEventListeners()
		registerRenderingHook()
	}

	private fun registerConfigurationIntegration() {
		AutoConfig.register(Configuration::class.java, ::JanksonConfigSerializer)

		val configHolder = AutoConfig.getConfigHolder(Configuration::class.java)
		configuration = configHolder.config
		configHolder.registerSaveListener { _, config ->
			configuration = config
			ActionResult.PASS
		}
	}

	private fun registerMessageListeners() {
		ClientPlayNetworking.registerGlobalReceiver(InventoryResponsePayload.ID) { payload, _ ->
			run {
				payload.let {
					CurrentContainerContext.set(Container(it.name, it.items))
				}
			}
		}
	}

	private fun registerEventListeners() {
		INVENTORY_OPENED.register {
			inventoryOpen = true
		}
		INVENTORY_CLOSED.register {
			inventoryOpen = false
		}
	}

	private fun registerRenderingHook() {
		HudElementRegistry.addLast(tooltipIdentifier) { guiGraphics, _ ->
			CurrentContainerContext.get()?.let { container ->
				if (
					(!configuration.showAutomatically &&
					keyIsNotPressed(configuration.showWithKeyCode)) ||
					inventoryOpen) {
					return@let
				}

				val client = MinecraftClient.getInstance()

				if (container.isEmpty()) {
					emptyContainerTooltip.render(
						client.textRenderer,
						client.window.scaledWidth / 2,
						guiGraphics,
						container
					)
				} else {
					containerTooltip.render(client.textRenderer, client.window.scaledWidth / 2, guiGraphics, container)
				}
			}
		}
	}

	private fun keyIsNotPressed(keyCode: Int) =
		!InputUtil.isKeyPressed(MinecraftClient.getInstance().window, keyCode)
}
