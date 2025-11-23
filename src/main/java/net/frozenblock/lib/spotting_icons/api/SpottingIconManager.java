/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.spotting_icons.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.Unpooled;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.spotting_icons.impl.SpottingIconPacket;
import net.frozenblock.lib.spotting_icons.impl.SpottingIconRemovePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SpottingIconManager {
	public Entity entity;
	public int ticksToCheck;
	public SpottingIcon icon;
	public boolean clientHasIconResource;

	public SpottingIconManager(Entity entity) {
		this.entity = entity;
	}

	public void tick() {
		if (this.ticksToCheck > 0) {
			--this.ticksToCheck;
			return;
		}

		this.ticksToCheck = 20;
		if (this.icon == null) return;

		if (this.entity.level().isClientSide()) this.clientHasIconResource = hasTexture(this.icon.texture());
		if (!SpottingIconPredicate.getPredicate(this.icon.restrictionID).test(this.entity)) this.removeIcon();
	}

	public void setIcon(Identifier texture, float startFade, float endFade, Identifier restrictionID) {
		this.icon = new SpottingIcon(texture, startFade, endFade, restrictionID);
		if (!this.entity.level().isClientSide()) {
			final CustomPacketPayload packet = new SpottingIconPacket(this.entity.getId(), texture, startFade, endFade, restrictionID);
			for (ServerPlayer player : PlayerLookup.tracking(this.entity)) ServerPlayNetworking.send(player, packet);
		} else {
			this.clientHasIconResource = hasTexture(this.icon.texture());
		}
		SpottingIconPredicate.getPredicate(this.icon.restrictionID).onAdded(this.entity);
	}

	public void removeIcon() {
		SpottingIconPredicate.getPredicate(this.icon.restrictionID).onRemoved(this.entity);
		this.icon = null;
		if (this.entity.level().isClientSide()) return;

		final CustomPacketPayload packet = new SpottingIconRemovePacket(this.entity.getId());
		for (ServerPlayer player : PlayerLookup.tracking(this.entity)) ServerPlayNetworking.send(player, packet);
	}

	public void sendIconPacket(ServerPlayer player) {
		if (this.icon == null) return;
		final FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
		byteBuf.writeVarInt(this.entity.getId());
		byteBuf.writeIdentifier(this.icon.texture);
		byteBuf.writeFloat(this.icon.startFadeDist);
		byteBuf.writeFloat(this.icon.endFadeDist);
		byteBuf.writeIdentifier(this.icon.restrictionID);
		ServerPlayNetworking.send(
			player,
			new SpottingIconPacket(
				this.entity.getId(),
				this.icon.texture,
				this.icon.startFadeDist(),
				this.icon.endFadeDist(),
				this.icon.restrictionID()
			)
		);
	}

	@Environment(EnvType.CLIENT)
	private static boolean hasTexture(Identifier texture) {
		return Minecraft.getInstance().getResourceManager().getResource(texture).isPresent();
	}

	public void load(ValueInput input) {
		this.ticksToCheck = input.getIntOr("frozenlib_spotting_icon_predicate_cooldown", 0);
		this.icon = null;
		final Optional<SpottingIcon> icon = input.read("frozenlib_spotting_icons", SpottingIcon.CODEC);
		icon.ifPresent(spottingIcon -> this.icon = spottingIcon);
	}

	public void save(ValueOutput output) {
		output.putInt("frozenlib_spotting_icon_predicate_cooldown", this.ticksToCheck);
		if (this.icon != null) {
			output.store("frozenlib_spotting_icons", SpottingIcon.CODEC, this.icon);
		} else {
			output.discard("frozenlib_spotting_icons");
		}
	}

	public record SpottingIcon(Identifier texture, float startFadeDist, float endFadeDist, Identifier restrictionID) {
		public static final Codec<SpottingIcon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("texture").forGetter(SpottingIcon::texture),
			Codec.FLOAT.fieldOf("startFadeDist").forGetter(SpottingIcon::startFadeDist),
			Codec.FLOAT.fieldOf("endFadeDist").forGetter(SpottingIcon::endFadeDist),
			Identifier.CODEC.fieldOf("restrictionID").forGetter(SpottingIcon::restrictionID)
		).apply(instance, SpottingIcon::new));
	}
}
