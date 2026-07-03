package net.frozenblock.lib.loot.mixin.neoforge;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import net.frozenblock.lib.loot.impl.NeoLootUtil;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.world.level.storage.loot.LootDataType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleJsonResourceReloadListener.class)
public class SimpleJsonResourceReloadListenerMixin {

	@Definition(id = "fileToId", method = "Lnet/minecraft/resources/FileToIdConverter;fileToId(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/resources/Identifier;")
	@Expression("? = ?.fileToId(?)")
	@Inject(
		method = "scanDirectory(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/FileToIdConverter;Lcom/mojang/serialization/DynamicOps;Lcom/mojang/serialization/Codec;Ljava/util/Map;)V",
		at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER)
	)
	private static <T> void frozenLib$fillSourceMap(
		ResourceManager manager,
		FileToIdConverter lister,
		DynamicOps<JsonElement> ops,
		Codec<T> codec,
		Map<Identifier, T> result,
		CallbackInfo ci,
		@Local(name = "entry") Map.Entry<Identifier, Resource> entry,
		@Local(name = "id") Identifier id
	) {
		if (!LootDataType.TABLE.registryKey().identifier().getPath().equals(lister.prefix())) return;

		NeoLootUtil.SOURCES.get().put(id, NeoLootUtil.determineSource(entry.getValue()));
	}
}
