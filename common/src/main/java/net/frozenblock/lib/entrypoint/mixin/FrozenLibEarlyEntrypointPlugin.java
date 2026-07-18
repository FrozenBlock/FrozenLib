package net.frozenblock.lib.entrypoint.mixin;

import java.util.List;
import java.util.Set;
import net.frozenblock.lib.entrypoint.impl.FrozenLibEntrypointInjector;
import net.frozenblock.lib.entrypoint.impl.FrozenLibEntrypoints;
import net.frozenblock.lib.platform.ModLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class FrozenLibEarlyEntrypointPlugin implements IMixinConfigPlugin {

	@Override
	public void onLoad(String mixinPackage) {
		if (ModLoader.isFabric()) {
			FrozenLibEntrypoints.collect();
			FrozenLibEntrypointInjector.inject();
		}
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
