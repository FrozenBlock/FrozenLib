package net.frozenblock.lib.datafix.mixin.client;

import com.mojang.datafixers.DataFixer;
import net.frozenblock.lib.datafix.impl.DataFixerHoldingGameInstance;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@ClientOnly
@Mixin(Minecraft.class)
public class MinecraftMixin implements DataFixerHoldingGameInstance {

	@Shadow
	@Final
	private DataFixer fixerUpper;

	@Unique
	@Override
	public DataFixer frozenLib$getDataFixer() {
		return this.fixerUpper;
	}
}
