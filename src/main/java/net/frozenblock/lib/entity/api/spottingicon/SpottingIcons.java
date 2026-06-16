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

package net.frozenblock.lib.entity.api.spottingicon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.frozenblock.lib.FrozenLibConstants;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record SpottingIcons(List<SpottingIcon> icons) implements Iterable<SpottingIcon> {
	public static final SpottingIcons EMPTY = new SpottingIcons(List.of());
	public static final Codec<SpottingIcons> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		SpottingIcon.LIST_CODEC.fieldOf("windDisturbances").forGetter(SpottingIcons::icons)
	).apply(instance, SpottingIcons::new));
	public static final StreamCodec<ByteBuf, SpottingIcons> STREAM_CODEC = StreamCodec.composite(
		SpottingIcon.LIST_STREAM_CODEC, SpottingIcons::icons,
		SpottingIcons::new
	);
	public static final AttachmentType<SpottingIcons> ATTACHMENT_TYPE = AttachmentRegistry.create(
		FrozenLibConstants.id("spotting_icons"),
		builder -> {
			builder.persistent(CODEC);
			builder.syncWith(STREAM_CODEC, AttachmentSyncPredicate.all());
		}
	);

	public static void init() {}

	public static void set(Entity target, SpottingIcon... icons) {
		target.setAttached(ATTACHMENT_TYPE, new SpottingIcons(List.of(icons)));
	}

	public static void add(Entity target, SpottingIcon icon) {
		final SpottingIcons icons = target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY);
		if (icons.isEmpty()) {
			set(target, icon);
			return;
		}
		target.setAttached(ATTACHMENT_TYPE, icons.add(icon));
	}

	public static void removeAttachment(Entity target) {
		target.removeAttached(ATTACHMENT_TYPE);
	}

	public static void removeIf(Entity target, Predicate<SpottingIcon> removeIf) {
		final SpottingIcons icons = target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY);
		if (icons.isEmpty()) return;
		target.setAttached(ATTACHMENT_TYPE, icons.removeIf(removeIf));
	}

	public static boolean anyMatch(Entity target, Predicate<SpottingIcon> predicate) {
		return target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY).anyMatch(predicate);
	}

	public static boolean allMatch(Entity target, Predicate<SpottingIcon> predicate) {
		return target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY).allMatch(predicate);
	}

	public static boolean noneMatch(Entity target, Predicate<SpottingIcon> predicate) {
		return target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY).noneMatch(predicate);
	}

	public static boolean has(Entity target) {
		return !target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY).isEmpty();
	}

	public static SpottingIcons get(Entity target) {
		return target.getAttachedOrElse(ATTACHMENT_TYPE, EMPTY);
	}

	public SpottingIcons add(SpottingIcon icon) {
		final List<SpottingIcon> newIcons = new ArrayList<>(this.icons);
		newIcons.add(icon);
		return new SpottingIcons(newIcons);
	}

	public SpottingIcons removeIf(Predicate<SpottingIcon> removeIf) {
		final List<SpottingIcon> newIcons = new ArrayList<>(this.icons);
		newIcons.removeIf(removeIf);
		return new SpottingIcons(newIcons);
	}

	public boolean anyMatch(Predicate<SpottingIcon> predicate) {
		return this.icons.stream().anyMatch(predicate);
	}

	public boolean allMatch(Predicate<SpottingIcon> predicate) {
		return this.icons.stream().allMatch(predicate);
	}

	public boolean noneMatch(Predicate<SpottingIcon> predicate) {
		return this.icons.stream().noneMatch(predicate);
	}

	public boolean isEmpty() {
		return this.icons.isEmpty();
	}

	@Override
	public Iterator<SpottingIcon> iterator() {
		return this.icons().iterator();
	}
}
