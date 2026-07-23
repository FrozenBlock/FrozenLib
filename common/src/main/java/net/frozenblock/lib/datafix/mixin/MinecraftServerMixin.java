package net.frozenblock.lib.datafix.mixin;

import com.mojang.datafixers.DataFixer;
import net.frozenblock.lib.datafix.impl.DataFixerHoldingGameInstance;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements DataFixerHoldingGameInstance {

	@Shadow
	@Final
	private DataFixer fixerUpper;

	@Unique
	@Override
	public DataFixer frozenLib$getDataFixer() {
		return this.fixerUpper;
	}
}
