package net.frozenblock.lib.block.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class FrozenWallHangingSignBlock extends WallHangingSignBlock {

	public final ResourceLocation lootTable;

	public FrozenWallHangingSignBlock(Properties settings, WoodType signType, ResourceLocation lootTable) {
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
