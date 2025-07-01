package net.frozenblock.lib.resource_pack.mixin;

import com.google.common.collect.ImmutableSet;
import net.frozenblock.lib.resource_pack.impl.PackRepositoryInterface;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import java.util.Set;

@Mixin(PackRepository.class)
public class PackRepositoryMixin implements PackRepositoryInterface {

	@Final
	@Shadow
	@Mutable
	private Set<RepositorySource> sources;

	@Unique
	@Override
	public void frozenLib$addRepositorySource(RepositorySource repositorySource) {
		this.sources = ImmutableSet.<RepositorySource>builder().addAll(this.sources).add(repositorySource).build();
	}
}
