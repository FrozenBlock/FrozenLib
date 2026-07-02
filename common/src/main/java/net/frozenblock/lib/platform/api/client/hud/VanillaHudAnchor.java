package net.frozenblock.lib.platform.api.client.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum VanillaHudAnchor {
	MISC_OVERLAYS,
	CROSSHAIR,
	SPECTATOR_MENU,
	HOTBAR,
	ARMOR_BAR,
	HEALTH_BAR,
	FOOD_BAR,
	AIR_BAR,
	MOUNT_HEALTH,
	INFO_BAR,
	EXPERIENCE_LEVEL,
	HELD_ITEM_TOOLTIP,
	SPECTATOR_TOOLTIP,
	MOB_EFFECTS,
	BOSS_BAR,
	SLEEP,
	DEMO_TIMER,
	SCOREBOARD,
	OVERLAY_MESSAGE,
	TITLE_AND_SUBTITLE,
	CHAT,
	PLAYER_LIST,
	SUBTITLES
}
