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
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.network.codec.StreamCodec;
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
	public static final AttachmentType<ScreenShakes> ATTACHMENT_TYPE = AttachmentRegistry.create(
		FrozenLibConstants.id("screen_shakes"),
		builder -> {
			builder.persistent(CODEC);
			builder.syncWith(STREAM_CODEC, AttachmentSyncPredicate.all());
		}
	);

	public static void tick(Level level, AttachmentTarget target) {
		final ScreenShakes screenShakes = target.getAttached(ATTACHMENT_TYPE);
		if (screenShakes == null) return;
		if (screenShakes.isEmpty()) {
			target.removeAttached(ATTACHMENT_TYPE);
			return;
		}

		final long gameTime = level.getGameTime();
		target.setAttached(ATTACHMENT_TYPE, screenShakes.removeIf(screenShake -> screenShake.expired(gameTime)));
	}

	public static void init() {}

	public static void setScreenShakes(AttachmentTarget target, ScreenShake... screenShakes) {
		target.setAttached(ATTACHMENT_TYPE, new ScreenShakes(List.of(screenShakes)));
	}

	public static void addScreenShake(AttachmentTarget target, ScreenShake screenShake) {
		final ScreenShakes screenShakes = target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY);
		if (screenShakes.isEmpty()) {
			setScreenShakes(target, screenShake);
			return;
		}
		target.setAttached(ATTACHMENT_TYPE, screenShakes.add(screenShake));
	}

	public static void remove(AttachmentTarget target) {
		target.removeAttached(ATTACHMENT_TYPE);
	}

	public static void removeScreenShakeIf(AttachmentTarget target, Predicate<ScreenShake> removeIf) {
		final ScreenShakes screenShakes = target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY);
		if (screenShakes.isEmpty()) return;
		target.setAttached(ATTACHMENT_TYPE, screenShakes.removeIf(removeIf));
	}

	public static boolean anyScreenShakesMatch(AttachmentTarget target, Predicate<ScreenShake> predicate) {
		return target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY).anyMatch(predicate);
	}

	public static boolean allScreenShakesMatch(AttachmentTarget target, Predicate<ScreenShake> predicate) {
		return target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY).allMatch(predicate);
	}

	public static boolean noScreenShakesMatch(AttachmentTarget target, Predicate<ScreenShake> predicate) {
		return target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY).noneMatch(predicate);
	}

	public static boolean has(AttachmentTarget target) {
		return !target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY).isEmpty();
	}

	public static ScreenShakes get(AttachmentTarget target) {
		return target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY);
	}

	public ScreenShakes add(ScreenShake screenShake) {
		List<ScreenShake> newIcons = new ArrayList<>(this.screenShakes);
		newIcons.add(screenShake);
		return new ScreenShakes(newIcons);
	}

	public ScreenShakes removeIf(Predicate<ScreenShake> removeIf) {
		List<ScreenShake> newIcons = new ArrayList<>(this.screenShakes);
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
