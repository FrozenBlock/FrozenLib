package net.frozenblock.lib.entities.behavior.api;

import com.mojang.serialization.Codec;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

public final class FrozenMemoryTypes {
	private FrozenMemoryTypes() {
		throw new UnsupportedOperationException("FrozenMemoryTypes contains only static declarations.");
	}

	public static final MemoryModuleType<BlockPos> BLOCK_TARGET = register("block_target");

	public static void init() {
	}

	private static <U> MemoryModuleType<U> register(String identifier, @NotNull Codec<U> codec) {
		return Registry.register(Registry.MEMORY_MODULE_TYPE, FrozenMain.id(identifier), new MemoryModuleType<>(Optional.of(codec)));
	}

	private static <U> MemoryModuleType<U> register(String identifier) {
		return Registry.register(Registry.MEMORY_MODULE_TYPE, FrozenMain.id(identifier), new MemoryModuleType<>(Optional.empty()));
	}
}
