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

package net.frozenblock.lib.event.impl;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.event.api.events.ServerLivingEntityEvents;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@UtilityClass
public class NeoServerLivingEntityEventsBridge {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(LivingIncomingDamageEvent.class, NeoServerLivingEntityEventsBridge::onIncomingDamage);
		NeoForge.EVENT_BUS.addListener(LivingDamageEvent.Post.class, NeoServerLivingEntityEventsBridge::onDamage);
		NeoForge.EVENT_BUS.addListener(LivingDeathEvent.class, NeoServerLivingEntityEventsBridge::onDeath);
	}

	private static void onIncomingDamage(LivingIncomingDamageEvent event) {
		if (ServerLivingEntityEvents.ALLOW_DAMAGE.invoker().allowDamage(event.getEntity(), event.getSource(), event.getOriginalAmount())) return;
		event.setCanceled(true);
	}

	private static void onDamage(LivingDamageEvent.Post event) {
		ServerLivingEntityEvents.AFTER_DAMAGE.invoker().afterDamage(
			event.getEntity(), event.getSource(), event.getOriginalDamage(), event.getHealthDamage(), event.getBlockedDamage() > 0F
		);
	}

	private static void onDeath(LivingDeathEvent event) {
		ServerLivingEntityEvents.AFTER_DEATH.invoker().afterDeath(event.getEntity(), event.getSource());
	}
}
