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
import net.frozenblock.lib.event.api.events.DefaultItemComponentEvents;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

@UtilityClass
public class NeoDefaultItemComponentEventsBridge {

	public static void init(IEventBus modBus) {
		modBus.addListener(ModifyDefaultComponentsEvent.class, NeoDefaultItemComponentEventsBridge::modify);
	}

	private static void modify(ModifyDefaultComponentsEvent event) {
		DefaultItemComponentEvents.MODIFY.invoker().modify(new NeoModifyContextImpl(event));
	}

	static final class NeoModifyContextImpl implements DefaultItemComponentEvents.ModifyContext {
		private final ModifyDefaultComponentsEvent wrapped;

		public NeoModifyContextImpl(ModifyDefaultComponentsEvent wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public void modify(Predicate<Item> itemPredicate, DefaultItemComponentEvents.ModifyConsumer builderConsumer) {
			this.wrapped.modifyMatching(
				((item, components) -> itemPredicate.test(item)),
				builderConsumer::modify
			);
		}
	}
}
