/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.wind.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.FrozenMain;
import net.frozenblock.lib.wind.api.WindManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class OverrideWindCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("overridewind").requires(source -> source.hasPermission(2))
				.then(Commands.argument("vec", Vec3Argument.vec3()).executes(context -> setWind(context.getSource(), Vec3Argument.getVec3(context, "vec"))))
				.then(Commands.argument("enabled", BoolArgumentType.bool()).executes(context -> setWind(context.getSource(), BoolArgumentType.getBool(context, "enabled"))))
		);
	}

	private static int setWind(CommandSourceStack source, boolean bl) {
		WindManager.overrideWind = bl;
		FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
		byteBuf.writeLong(WindManager.time);
		byteBuf.writeDouble(WindManager.cloudX);
		byteBuf.writeDouble(WindManager.cloudY);
		byteBuf.writeDouble(WindManager.cloudZ);
		byteBuf.writeLong(WindManager.seed);
		byteBuf.writeBoolean(bl);
		byteBuf.writeDouble(WindManager.commandWind.x());
		byteBuf.writeDouble(WindManager.commandWind.y());
		byteBuf.writeDouble(WindManager.commandWind.z());
		for (ServerPlayer player : PlayerLookup.all(source.getServer())) {
			ServerPlayNetworking.send(player, FrozenMain.WIND_SYNC_PACKET, byteBuf);
		}
		source.sendSuccess(Component.translatable("commands.wind.toggle.success", bl), true);
		return 1;
	}

	private static int setWind(CommandSourceStack source, Vec3 vec3) {
		WindManager.overrideWind = true;
		WindManager.windX = vec3.x();
		WindManager.windY = vec3.y();
		WindManager.windZ = vec3.z();
		WindManager.commandWind = new Vec3(WindManager.windX, WindManager.windY, WindManager.windZ);
		FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
		byteBuf.writeLong(WindManager.time);
		byteBuf.writeDouble(WindManager.cloudX);
		byteBuf.writeDouble(WindManager.cloudY);
		byteBuf.writeDouble(WindManager.cloudZ);
		byteBuf.writeLong(WindManager.seed);
		byteBuf.writeBoolean(true);
		byteBuf.writeDouble(WindManager.commandWind.x());
		byteBuf.writeDouble(WindManager.commandWind.y());
		byteBuf.writeDouble(WindManager.commandWind.z());
		for (ServerPlayer player : PlayerLookup.all(source.getServer())) {
			ServerPlayNetworking.send(player, FrozenMain.WIND_SYNC_PACKET, byteBuf);
		}
		source.sendSuccess(Component.translatable("commands.wind.success", vec3.x(), vec3.y(), vec3.z()), true);
		return 1;
	}

}
