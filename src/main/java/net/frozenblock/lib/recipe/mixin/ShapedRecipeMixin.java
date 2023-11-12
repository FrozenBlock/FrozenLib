package net.frozenblock.lib.recipe.mixin;

import net.frozenblock.lib.recipe.api.ShapedRecipeBuilderExtension;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ShapedRecipe.class)
public class ShapedRecipeMixin implements ShapedRecipeBuilderExtension {

	@Unique
	@Nullable
	private CompoundTag tag;

	@Override
	public ShapedRecipeBuilder frozenLib$tag(@Nullable CompoundTag tag) {
		this.tag = tag;
		return null;
	}

	@Override
	public @Nullable CompoundTag frozenLib$getTag() {
		return this.tag;
	}
}
