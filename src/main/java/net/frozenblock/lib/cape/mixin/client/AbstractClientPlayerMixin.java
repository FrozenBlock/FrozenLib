package net.frozenblock.lib.cape.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.cape.client.impl.AbstractClientPlayerCapeInterface;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin implements AbstractClientPlayerCapeInterface {

	@Unique
	private ResourceLocation frozenLib$cape;

	@Override
	public void frozenLib$setCape(ResourceLocation cape) {
		frozenLib$cape = cape;
	}

	@Override
	public ResourceLocation frozenLib$getCape() {
		return this.frozenLib$cape;
	}
}
