package net.frozenblock.lib.wind.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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
		dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder)Commands.literal("wind").requires(source -> source.hasPermission(2))).executes(context -> setWind((CommandSourceStack)context.getSource(), ((CommandSourceStack)context.getSource()).getPosition())).then(((Commands.argument("vec", Vec3Argument.vec3()).executes(context -> setWind(context.getSource(), Vec3Argument.getVec3(context, "vec")))))));
	}

	private static int setWind(CommandSourceStack source, Vec3 vec3) {
		WindManager.overrideWind = true;
		WindManager.windX = vec3.x();
		WindManager.windY = vec3.y();
		WindManager.windZ = vec3.z();
		FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
		byteBuf.writeLong(WindManager.time);
		byteBuf.writeDouble(WindManager.cloudX);
		byteBuf.writeDouble(WindManager.cloudY);
		byteBuf.writeDouble(WindManager.cloudZ);
		byteBuf.writeBoolean(true);
		for (ServerPlayer player : PlayerLookup.all(source.getServer())) {
			ServerPlayNetworking.send(player, FrozenMain.WIND_SYNC_PACKET, byteBuf);
		}
		source.sendSuccess(Component.translatable("commands.wind.success", vec3.x(), vec3.y(), vec3.z()), true);
		return 1;
	}

}
