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

package net.frozenblock.lib.screenshake.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.event.api.events.FrozenLibServerTickEvents;
import net.frozenblock.lib.platform.api.data.FrozenDataAttachmentType;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record ScreenShakes(List<ScreenShake> screenShakes) implements Iterable<ScreenShake> {
	public static final ScreenShakes EMPTY = new ScreenShakes(List.of());
	public static final Codec<ScreenShakes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		ScreenShake.LIST_CODEC.fieldOf("screen_shakes").forGetter(ScreenShakes::screenShakes)
	).apply(instance, ScreenShakes::new));
	public static final StreamCodec<ByteBuf, ScreenShakes> STREAM_CODEC = StreamCodec.composite(
		ScreenShake.LIST_STREAM_CODEC, ScreenShakes::screenShakes,
		ScreenShakes::new
	);
	public static final FrozenDataAttachmentType<ScreenShakes> ATTACHMENT = FrozenDataAttachmentType.<ScreenShakes>builder(
		FrozenLibConstants.id("screen_shakes")
	).persistent(CODEC).sync(STREAM_CODEC).build();

	public static void tick(Level level, Object target) {
		final ScreenShakes screenShakes = ATTACHMENT.get(target);
		if (screenShakes == null) return;
		if (screenShakes.isEmpty()) {
			ATTACHMENT.remove(target);
			return;
		}

		final long gameTime = level.getGameTime();
		ATTACHMENT.set(target, screenShakes.removeIf(screenShake -> screenShake.expired(gameTime)));
		if (!has(target)) removeAttachment(target);
	}

	public static void init() {
		FrozenLibServerTickEvents.START_LEVEL_TICK.register(serverLevel -> {
			tick(serverLevel, serverLevel);
			for (Entity entity : serverLevel.getAllEntities()) {
				if (entity.isRemoved()) continue;
				tick(serverLevel, entity);
			}
		});
	}

	public static void set(Object target, ScreenShake... screenShakes) {
		ATTACHMENT.set(target, new ScreenShakes(List.of(screenShakes)));
	}

	public static void add(Object target, ScreenShake screenShake) {
		final ScreenShakes screenShakes = ATTACHMENT.getOrDefault(target, EMPTY);
		if (screenShakes.isEmpty()) {
			set(target, screenShake);
			return;
		}
		ATTACHMENT.set(target, screenShakes.add(screenShake));
	}

	public static void removeAttachment(Object target) {
		ATTACHMENT.remove(target);
	}

	public static void removeIf(Object target, Predicate<ScreenShake> removeIf) {
		final ScreenShakes screenShakes = ATTACHMENT.getOrDefault(target, EMPTY);
		if (screenShakes.isEmpty()) return;
		ATTACHMENT.set(target, screenShakes.removeIf(removeIf));
	}

	public static boolean anyMatch(Object target, Predicate<ScreenShake> predicate) {
		return ATTACHMENT.getOrDefault(target, EMPTY).anyMatch(predicate);
	}

	public static boolean allMatch(Object target, Predicate<ScreenShake> predicate) {
		return ATTACHMENT.getOrDefault(target, EMPTY).allMatch(predicate);
	}

	public static boolean noneMatch(Object target, Predicate<ScreenShake> predicate) {
		return ATTACHMENT.getOrDefault(target, EMPTY).noneMatch(predicate);
	}

	public static boolean has(Object target) {
		return !ATTACHMENT.getOrDefault(target, EMPTY).isEmpty();
	}

	public static ScreenShakes get(Object target) {
		return ATTACHMENT.getOrDefault(target, EMPTY);
	}

	public ScreenShakes add(ScreenShake screenShake) {
		final List<ScreenShake> newIcons = new ArrayList<>(this.screenShakes);
		newIcons.add(screenShake);
		return new ScreenShakes(newIcons);
	}

	public ScreenShakes removeIf(Predicate<ScreenShake> removeIf) {
		final List<ScreenShake> newIcons = new ArrayList<>(this.screenShakes);
		newIcons.removeIf(removeIf);
		return new ScreenShakes(newIcons);
	}

	public boolean anyMatch(Predicate<ScreenShake> predicate) {
		return this.screenShakes.stream().anyMatch(predicate);
	}

	public boolean allMatch(Predicate<ScreenShake> predicate) {
		return this.screenShakes.stream().allMatch(predicate);
	}

	public boolean noneMatch(Predicate<ScreenShake> predicate) {
		return this.screenShakes.stream().noneMatch(predicate);
	}

	public boolean isEmpty() {
		return this.screenShakes.isEmpty();
	}

	@Override
	public Iterator<ScreenShake> iterator() {
		return this.screenShakes().iterator();
	}
}
