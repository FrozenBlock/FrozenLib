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
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenLibConstants;
import net.frozenblock.lib.spotting_icons.impl.SpottingIconPacket;
import net.frozenblock.lib.spotting_icons.impl.SpottingIconRemovePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

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
		} else {
			this.ticksToCheck = 20;
			if (this.icon != null) {
				if (this.entity.level().isClientSide) {
					this.clientHasIconResource = hasTexture(this.icon.texture());
				}
				if (!SpottingIconPredicate.getPredicate(this.icon.restrictionID).test(this.entity)) {
					this.removeIcon();
				}
			}
		}
	}

	public void setIcon(ResourceLocation texture, float startFade, float endFade, ResourceLocation restrictionID) {
		this.icon = new SpottingIcon(texture, startFade, endFade, restrictionID);
		if (!this.entity.level().isClientSide) {
			CustomPacketPayload packet = new SpottingIconPacket(this.entity.getId(), texture, startFade, endFade, restrictionID);
			for (ServerPlayer player : PlayerLookup.tracking(this.entity)) {
				ServerPlayNetworking.send(player, packet);
			}
		} else {
			this.clientHasIconResource = hasTexture(this.icon.texture());
		}
		SpottingIconPredicate.getPredicate(this.icon.restrictionID).onAdded(this.entity);
	}

	public void removeIcon() {
		SpottingIconPredicate.getPredicate(this.icon.restrictionID).onRemoved(this.entity);
		this.icon = null;
		if (!this.entity.level().isClientSide) {
			CustomPacketPayload packet = new SpottingIconRemovePacket(this.entity.getId());
			for (ServerPlayer player : PlayerLookup.tracking(this.entity)) {
				ServerPlayNetworking.send(player, packet);
			}
		}
	}

	public void sendIconPacket(ServerPlayer player) {
		if (this.icon != null) {
			FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
			byteBuf.writeVarInt(this.entity.getId());
			byteBuf.writeResourceLocation(this.icon.texture);
			byteBuf.writeFloat(this.icon.startFadeDist);
			byteBuf.writeFloat(this.icon.endFadeDist);
			byteBuf.writeResourceLocation(this.icon.restrictionID);
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
	}

	@Environment(EnvType.CLIENT)
	private static boolean hasTexture(ResourceLocation resourceLocation) {
		return Minecraft.getInstance().getResourceManager().getResource(resourceLocation).isPresent();
	}

	public void load(@NotNull CompoundTag nbt) {
		this.ticksToCheck = nbt.getIntOr("frozenlib_spotting_icon_predicate_cooldown", 0);
		if (nbt.contains("frozenlib_spotting_icons")) {
			this.icon = null;
			SpottingIcon.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompoundOrEmpty("frozenlib_spotting_icons")))
				.resultOrPartial(FrozenLibConstants.LOGGER::error)
				.ifPresent(spottingIcon -> this.icon = spottingIcon);
		}
	}

	public void save(@NotNull CompoundTag nbt) {
		nbt.putInt("frozenlib_spotting_icon_predicate_cooldown", this.ticksToCheck);
		if (this.icon != null) {
			SpottingIcon.CODEC.encodeStart(NbtOps.INSTANCE, this.icon)
				.resultOrPartial(FrozenLibConstants.LOGGER::error)
				.ifPresent(spottingIcon -> nbt.put("frozenlib_spotting_icons", spottingIcon));
		} else if (nbt.contains("frozenlib_spotting_icons")) {
			nbt.remove("frozenlib_spotting_icons");
		}
	}

	public record SpottingIcon(ResourceLocation texture, float startFadeDist, float endFadeDist, ResourceLocation restrictionID) {
			public static final Codec<SpottingIcon> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				ResourceLocation.CODEC.fieldOf("texture").forGetter(SpottingIcon::texture),
				Codec.FLOAT.fieldOf("startFadeDist").forGetter(SpottingIcon::startFadeDist),
				Codec.FLOAT.fieldOf("endFadeDist").forGetter(SpottingIcon::endFadeDist),
				ResourceLocation.CODEC.fieldOf("restrictionID").forGetter(SpottingIcon::restrictionID)
			).apply(instance, SpottingIcon::new));
	}
}
