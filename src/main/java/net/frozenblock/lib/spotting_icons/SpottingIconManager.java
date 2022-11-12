/*
 * Copyright 2022 FrozenBlock
 * This file is part of FrozenLib.
 *
 * FrozenLib is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * FrozenLib is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with FrozenLib. If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.spotting_icons;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

public class SpottingIconManager {
    public LivingEntity entity;
    public int ticksToCheck;
	public SpottingIcon icon;

    public SpottingIconManager(LivingEntity entity) {
        this.entity = entity;
    }

    public void load(CompoundTag nbt) {
        nbt.putInt("frozenSpottingIconTicksToCheck", this.ticksToCheck);
		if (nbt.contains("frozenSpottingIcons")) {
			this.icon = null;
			DataResult<SpottingIcon> var10000 = SpottingIcon.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("frozenSpottingIcons")));
			Logger var10001 = FrozenMain.LOGGER4;
			Objects.requireNonNull(var10001);
			Optional<SpottingIcon> icon = var10000.resultOrPartial(var10001::error);
			icon.ifPresent(spottingIcon -> this.icon = spottingIcon);
		}
    }

    public void save(CompoundTag nbt) {
        this.ticksToCheck = nbt.getInt("frozenSpottingIconTicksToCheck");
		if (this.icon != null) {
			DataResult<Tag> var10000 = SpottingIcon.CODEC.encodeStart(NbtOps.INSTANCE, this.icon);
			Logger var10001 = FrozenMain.LOGGER4;
			Objects.requireNonNull(var10001);
			var10000.resultOrPartial(var10001::error).ifPresent((iconNBT) -> nbt.put("frozenSpottingIcons", iconNBT));
		} else if (nbt.contains("frozenSpottingIcons")) {
			nbt.remove("frozenSpottingIcons");
		}
    }

    public void tick() {
        if (this.ticksToCheck > 0) {
            --this.ticksToCheck;
        } else {
            this.ticksToCheck = 20;
            if (this.icon != null) {
				final SpottingIconPredicate.IconPredicate<Entity> predicate = SpottingIconPredicate.getPredicate(this.icon.restrictionID);
				if (!predicate.test(this.entity)) {
					this.removeIcon();
				}
			}
        }
    }

	public void setIcon(ResourceLocation texture, float startFade, float endFade, ResourceLocation restrictionID) {
		this.icon = new SpottingIcon(texture, startFade, endFade, restrictionID);
		if (!this.entity.level.isClientSide) {
			FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
			byteBuf.writeVarInt(this.entity.getId());
			byteBuf.writeResourceLocation(texture);
			byteBuf.writeFloat(startFade);
			byteBuf.writeFloat(endFade);
			byteBuf.writeResourceLocation(restrictionID);
			for (ServerPlayer player : PlayerLookup.tracking(this.entity)) {
				ServerPlayNetworking.send(player, FrozenMain.SPOTTING_ICON_PACKET, byteBuf);
			}
		}
		SpottingIconPredicate.getPredicate(this.icon.restrictionID).onAdded(this.entity);
	}

	public void removeIcon() {
		SpottingIconPredicate.getPredicate(this.icon.restrictionID).onRemoved(this.entity);
		this.icon = null;
		if (!this.entity.level.isClientSide) {
			FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
			byteBuf.writeVarInt(this.entity.getId());
			for (ServerPlayer player : PlayerLookup.tracking(this.entity)) {
				ServerPlayNetworking.send(player, FrozenMain.SPOTTING_ICON_REMOVE_PACKET, byteBuf);
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
			ServerPlayNetworking.send(player, FrozenMain.SPOTTING_ICON_PACKET, byteBuf);
		}
	}

    public static class SpottingIcon {
        public final ResourceLocation texture;
        public final float startFadeDist;
        public final float endFadeDist;
        public final ResourceLocation restrictionID;

        public static final Codec<SpottingIcon> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(SpottingIcon::getTexture),
                Codec.FLOAT.fieldOf("startFadeDist").forGetter(SpottingIcon::getStartFadeDist),
                Codec.FLOAT.fieldOf("endFadeDist").forGetter(SpottingIcon::getEndFadeDist),
                ResourceLocation.CODEC.fieldOf("restrictionID").forGetter(SpottingIcon::getRestrictionID)
        ).apply(instance, SpottingIcon::new));

        public SpottingIcon(ResourceLocation texture, float startFadeDist, float endFadeDist, ResourceLocation restrictionID) {
            this.texture = texture;
            this.startFadeDist = startFadeDist;
            this.endFadeDist = endFadeDist;
            this.restrictionID = restrictionID;
        }

        public ResourceLocation getTexture() {
            return this.texture;
        }

        public float getStartFadeDist() {
            return this.startFadeDist;
        }

        public float getEndFadeDist() {
            return this.endFadeDist;
        }

        public ResourceLocation getRestrictionID() {
            return this.restrictionID;
        }
    }
}
