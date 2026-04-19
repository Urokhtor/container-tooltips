package io.urokhtor.minecraft.containertooltips

import com.mojang.blaze3d.platform.InputConstants
import io.urokhtor.minecraft.containertooltips.configuration.Configuration
import io.urokhtor.minecraft.containertooltips.rendering.ContainerTooltip
import io.urokhtor.minecraft.containertooltips.rendering.EmptyContainerTooltip
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import net.minecraft.world.InteractionResult

object ContainerTooltipsClient : ClientModInitializer {

	private val containerTooltip = ContainerTooltip()
	private val emptyContainerTooltip = EmptyContainerTooltip()
	private lateinit var configuration: Configuration
	private val tooltipIdentifier = Identifier.fromNamespaceAndPath("container-tooltips", "tooltip")
	private var screenOpen = false

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
			InteractionResult.PASS
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
		SCREEN_OPENED.register {
			screenOpen = true
		}
		SCREEN_CLOSED.register {
			screenOpen = false
		}
	}

	private fun registerRenderingHook() {
		HudElementRegistry.addLast(tooltipIdentifier) { guiGraphics, _ ->
			CurrentContainerContext.get()?.let { container ->
				if (tooltipIsDisabled() || screenOpen) {
					return@let
				}

				val client = Minecraft.getInstance()

				if (container.isEmpty()) {
					emptyContainerTooltip.render(
						client.font,
						client.window.guiScaledWidth / 2,
						guiGraphics,
						container
					)
				} else {
					containerTooltip.render(client.font, client.window.guiScaledWidth / 2, guiGraphics, container)
				}
			}
		}
	}

	private fun tooltipIsDisabled(): Boolean = (
				!configuration.showAutomatically && keyIsNotPressed(configuration.showWithKeyCode)
			)

	private fun keyIsNotPressed(keyCode: Int) =
		!InputConstants.isKeyDown(Minecraft.getInstance().window, keyCode)
}
