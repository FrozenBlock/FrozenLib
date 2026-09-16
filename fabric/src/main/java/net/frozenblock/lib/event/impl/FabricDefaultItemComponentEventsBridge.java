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

import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.world.item.Item;

@UtilityClass
public class FabricDefaultItemComponentEventsBridge {
	public static void init() {
		DefaultItemComponentEvents.MODIFY.register(context ->
			net.frozenblock.lib.event.api.events.DefaultItemComponentEvents.MODIFY.invoker().modify(new FabricModifyContextImpl(context))
		);
	}

	static final class FabricModifyContextImpl implements net.frozenblock.lib.event.api.events.DefaultItemComponentEvents.ModifyContext {
		private final DefaultItemComponentEvents.ModifyContext wrapped;

		public FabricModifyContextImpl(DefaultItemComponentEvents.ModifyContext wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public void modify(Predicate<Item> itemPredicate, net.frozenblock.lib.event.api.events.DefaultItemComponentEvents.ModifyConsumer builderConsumer) {
			this.wrapped.modify(itemPredicate, builderConsumer::modify);
		}
	}
}
