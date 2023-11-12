package net.frozenblock.lib.recipe.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class FrozenRecipeCodecs {

	public static final Codec<Holder<Item>> ITEM_NON_AIR_CODEC = ExtraCodecs.validate(
		BuiltInRegistries.ITEM.holderByNameCodec(), holder -> holder.value() == Items.AIR ? DataResult.error(() -> "Item must not be minecraft:air") : DataResult.success(holder)
	);
}
