/*
 * Copyright (C) 2024-2026 FrozenBlock
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

package net.frozenblock.lib.sound.api.predicate;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import java.util.function.Supplier;

@UtilityClass
public final class SoundPredicateTypes {
	private static final DeferredRegister<Supplier<SoundPredicate<?>>> REGISTER = DeferredRegister.create(
		FrozenLibRegistries.SOUND_PREDICATE_TYPE,
		FrozenLibConstants.MOD_ID
	);

	public static final DeferredHolder<Supplier<SoundPredicate<?>>, Supplier<SoundPredicate<?>>> NOT_SILENT = REGISTER.register(
		"not_silent",
		() -> () -> new SoundPredicate<>() {
			@Override
			public boolean test(Entity entity) {
				return !entity.isSilent();
			}
		}
	);

	public static final DeferredHolder<Supplier<SoundPredicate<?>>, Supplier<SoundPredicate<?>>> NOT_SILENT_AND_ALIVE = REGISTER.register(
		"not_silent_and_alive",
		() -> () -> new SoundPredicate<>() {
			@Override
			public boolean test(Entity entity) {
				return !entity.isSilent() && entity.isAlive();
			}
		}
	);

	public static final DeferredHolder<Supplier<SoundPredicate<?>>, Supplier<SoundPredicate<?>>> USING_INSTRUMENT = REGISTER.register(
		"using_instrument",
		() -> () -> new SoundPredicate<>() {
			private boolean firstCheck = true;
			private ItemStack lastStack;

			@Override
			public Boolean firstTickTest(Entity entity) {
				return entity instanceof LivingEntity;
			}

			@Override
			public boolean test(Entity entity) {
				final LivingEntity livingEntity = (LivingEntity) entity;

				if (this.firstCheck) {
					this.firstCheck = false;
					final InteractionHand hand = !livingEntity.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()
						? InteractionHand.MAIN_HAND
						: !livingEntity.getItemInHand(InteractionHand.OFF_HAND).isEmpty() ? InteractionHand.OFF_HAND
						: null;
					if (hand == null) return false;

					ItemStack stack = livingEntity.getItemInHand(hand);
					if (stack.getItem() instanceof InstrumentItem) {
						this.lastStack = stack;
						return true;
					}
					return false;
				}

				final ItemStack stack = livingEntity.getUseItem();
				if (stack.getItem() instanceof InstrumentItem) {
					if (this.lastStack == null || ItemStack.matches(this.lastStack, stack)) {
						this.lastStack = stack;
						return true;
					}
					this.firstCheck = true;
				}
				return false;
			}
		}
	);

	static {
		REGISTER.register();
	}

	public static void init() {}
}
