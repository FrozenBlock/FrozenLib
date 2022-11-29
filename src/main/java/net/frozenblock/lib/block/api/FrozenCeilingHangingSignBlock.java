package net.frozenblock.lib.block.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class FrozenCeilingHangingSignBlock extends CeilingHangingSignBlock {

	public final ResourceLocation lootTable;

	public FrozenCeilingHangingSignBlock(BlockBehaviour.Properties settings, WoodType signType, ResourceLocation lootTable) {
		super(settings, signType);
		this.lootTable = lootTable;
	}

	@Override
	@NotNull
	public ResourceLocation getLootTable() {
		if (!Objects.equals(this.drops, this.lootTable)) {
			this.drops = this.lootTable;
		}

		assert this.drops != null;
		return this.drops;
	}
}
