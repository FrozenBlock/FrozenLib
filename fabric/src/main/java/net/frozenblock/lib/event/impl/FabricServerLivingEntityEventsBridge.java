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
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

@UtilityClass
public class FabricServerLivingEntityEventsBridge {
	public static void init() {
		ServerLivingEntityEvents.ALLOW_DAMAGE.register(((entity, source, amount) ->
			net.frozenblock.lib.event.api.events.ServerLivingEntityEvents.ALLOW_DAMAGE.invoker().allowDamage(entity, source, amount))
		);

		ServerLivingEntityEvents.AFTER_DAMAGE.register(((entity, source, baseDamageTaken, damageTaken, blocked) ->
			net.frozenblock.lib.event.api.events.ServerLivingEntityEvents.AFTER_DAMAGE.invoker().afterDamage(entity, source, baseDamageTaken, damageTaken, blocked))
		);

		ServerLivingEntityEvents.ALLOW_DEATH.register(((entity, source, amount) ->
			net.frozenblock.lib.event.api.events.ServerLivingEntityEvents.ALLOW_DEATH.invoker().allowDeath(entity, source, amount))
		);

		ServerLivingEntityEvents.AFTER_DEATH.register(((entity, source) ->
			net.frozenblock.lib.event.api.events.ServerLivingEntityEvents.AFTER_DEATH.invoker().afterDeath(entity, source))
		);

		ServerLivingEntityEvents.MOB_CONVERSION.register(((previous, converted, keepEquipment) ->
			net.frozenblock.lib.event.api.events.ServerLivingEntityEvents.MOB_CONVERSION.invoker().onConversion(previous, converted, keepEquipment))
		);
	}
}
