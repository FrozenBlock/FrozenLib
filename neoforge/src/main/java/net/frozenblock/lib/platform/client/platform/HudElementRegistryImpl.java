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

package net.frozenblock.lib.platform.client.platform;

import java.util.ArrayList;
import java.util.List;
import net.frozenblock.lib.platform.api.client.hud.HudElementRenderer;
import net.frozenblock.lib.platform.api.client.hud.VanillaHudAnchor;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public final class HudElementRegistryImpl {
	private static final List<UnanchoredEntry> FIRST = new ArrayList<>();
	private static final List<UnanchoredEntry> LAST = new ArrayList<>();
	private static final List<AnchoredEntry> BEFORE = new ArrayList<>();
	private static final List<AnchoredEntry> AFTER = new ArrayList<>();

	public static void addFirst(Identifier id, HudElementRenderer renderer) {
		FIRST.add(new UnanchoredEntry(id, renderer));
	}

	public static void addLast(Identifier id, HudElementRenderer renderer) {
		LAST.add(new UnanchoredEntry(id, renderer));
	}

	public static void attachElementBefore(VanillaHudAnchor vanillaElementId, Identifier id, HudElementRenderer renderer) {
		BEFORE.add(new AnchoredEntry(vanillaElementId, id, renderer));
	}

	public static void attachElementAfter(VanillaHudAnchor vanillaElementId, Identifier id, HudElementRenderer renderer) {
		AFTER.add(new AnchoredEntry(vanillaElementId, id, renderer));
	}

	public static void flush(RegisterGuiLayersEvent event) {
		for (UnanchoredEntry entry : FIRST) event.registerBelowAll(entry.id(), entry.renderer()::extractRenderState);

		for (UnanchoredEntry entry : LAST) event.registerAboveAll(entry.id(), entry.renderer()::extractRenderState);

		for (AnchoredEntry entry : BEFORE) event.registerBelow(toNeo(entry.vanillaElementId()), entry.id(), entry.renderer()::extractRenderState);

		for (AnchoredEntry entry : AFTER) event.registerAbove(toNeo(entry.vanillaElementId()), entry.id(), entry.renderer()::extractRenderState);
	}

	private static Identifier toNeo(VanillaHudAnchor anchor) {
		return switch (anchor) {
			case MISC_OVERLAYS -> VanillaGuiLayers.CAMERA_OVERLAYS;
			case CROSSHAIR -> VanillaGuiLayers.CROSSHAIR;
			case SPECTATOR_MENU -> VanillaGuiLayers.AFTER_CAMERA_DECORATIONS;
			case HOTBAR -> VanillaGuiLayers.HOTBAR;
			case ARMOR_BAR -> VanillaGuiLayers.ARMOR_LEVEL;
			case HEALTH_BAR -> VanillaGuiLayers.PLAYER_HEALTH;
			case FOOD_BAR -> VanillaGuiLayers.FOOD_LEVEL;
			case AIR_BAR -> VanillaGuiLayers.AIR_LEVEL;
			case MOUNT_HEALTH -> VanillaGuiLayers.VEHICLE_HEALTH;
			case INFO_BAR -> VanillaGuiLayers.CONTEXTUAL_INFO_BAR_BACKGROUND;
			case EXPERIENCE_LEVEL -> VanillaGuiLayers.EXPERIENCE_LEVEL;
			case HELD_ITEM_TOOLTIP -> VanillaGuiLayers.SELECTED_ITEM_NAME;
			case SPECTATOR_TOOLTIP -> VanillaGuiLayers.SPECTATOR_TOOLTIP;
			case MOB_EFFECTS -> VanillaGuiLayers.EFFECTS;
			case BOSS_BAR -> VanillaGuiLayers.BOSS_OVERLAY;
			case SLEEP -> VanillaGuiLayers.SLEEP_OVERLAY;
			case DEMO_TIMER -> VanillaGuiLayers.DEMO_OVERLAY;
			case SCOREBOARD -> VanillaGuiLayers.SCOREBOARD_SIDEBAR;
			case OVERLAY_MESSAGE -> VanillaGuiLayers.OVERLAY_MESSAGE;
			case TITLE_AND_SUBTITLE -> VanillaGuiLayers.TITLE;
			case CHAT -> VanillaGuiLayers.CHAT;
			case PLAYER_LIST -> VanillaGuiLayers.TAB_LIST;
			case SUBTITLES -> VanillaGuiLayers.SUBTITLE_OVERLAY;
		};
	}

	private record AnchoredEntry(VanillaHudAnchor vanillaElementId, Identifier id, HudElementRenderer renderer) {}
	private record UnanchoredEntry(Identifier id, HudElementRenderer renderer) {}
}
