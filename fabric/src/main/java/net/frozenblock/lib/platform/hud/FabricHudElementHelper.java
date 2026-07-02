/*
 * Copyright (C) 2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.platform.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.frozenblock.lib.platform.api.client.hud.HudElementRenderer;
import net.frozenblock.lib.platform.api.client.hud.VanillaHudAnchor;
import net.frozenblock.lib.platform.service.HudElementHelper;
import net.minecraft.resources.Identifier;

public class FabricHudElementHelper implements HudElementHelper {

	@Override
	public void addFirst(Identifier id, HudElementRenderer renderer) {
		HudElementRegistry.addFirst(id, renderer::extractRenderState);
	}

	@Override
	public void addLast(Identifier id, HudElementRenderer renderer) {
		HudElementRegistry.addLast(id, renderer::extractRenderState);
	}

	@Override
	public void attachElementBefore(VanillaHudAnchor vanillaElementId, Identifier id, HudElementRenderer renderer) {
		HudElementRegistry.attachElementBefore(toFabric(vanillaElementId), id, renderer::extractRenderState);
	}

	@Override
	public void attachElementAfter(VanillaHudAnchor vanillaElementId, Identifier id, HudElementRenderer renderer) {
		HudElementRegistry.attachElementAfter(toFabric(vanillaElementId), id, renderer::extractRenderState);
	}

	private static Identifier toFabric(VanillaHudAnchor anchor) {
		return switch (anchor) {
			case MISC_OVERLAYS -> VanillaHudElements.MISC_OVERLAYS;
			case CROSSHAIR -> VanillaHudElements.CROSSHAIR;
			case SPECTATOR_MENU -> VanillaHudElements.SPECTATOR_MENU;
			case HOTBAR -> VanillaHudElements.HOTBAR;
			case ARMOR_BAR -> VanillaHudElements.ARMOR_BAR;
			case HEALTH_BAR -> VanillaHudElements.HEALTH_BAR;
			case FOOD_BAR -> VanillaHudElements.FOOD_BAR;
			case AIR_BAR -> VanillaHudElements.AIR_BAR;
			case MOUNT_HEALTH -> VanillaHudElements.MOUNT_HEALTH;
			case INFO_BAR -> VanillaHudElements.INFO_BAR;
			case EXPERIENCE_LEVEL -> VanillaHudElements.EXPERIENCE_LEVEL;
			case HELD_ITEM_TOOLTIP -> VanillaHudElements.HELD_ITEM_TOOLTIP;
			case SPECTATOR_TOOLTIP -> VanillaHudElements.SPECTATOR_TOOLTIP;
			case MOB_EFFECTS -> VanillaHudElements.MOB_EFFECTS;
			case BOSS_BAR -> VanillaHudElements.BOSS_BAR;
			case SLEEP -> VanillaHudElements.SLEEP;
			case DEMO_TIMER -> VanillaHudElements.DEMO_TIMER;
			case SCOREBOARD -> VanillaHudElements.SCOREBOARD;
			case OVERLAY_MESSAGE -> VanillaHudElements.OVERLAY_MESSAGE;
			case TITLE_AND_SUBTITLE -> VanillaHudElements.TITLE_AND_SUBTITLE;
			case CHAT -> VanillaHudElements.CHAT;
			case PLAYER_LIST -> VanillaHudElements.PLAYER_LIST;
			case SUBTITLES -> VanillaHudElements.SUBTITLES;
		};
	}
}
