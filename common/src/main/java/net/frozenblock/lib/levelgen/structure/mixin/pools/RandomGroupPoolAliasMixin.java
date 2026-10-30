/*
 * Copyright (C) 2024-2026 FrozenBlock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.lib.levelgen.structure.mixin.pools;

import java.util.List;
import net.frozenblock.lib.FrozenLibEarlyConstants;
import net.frozenblock.lib.levelgen.structure.api.pools.RandomGroupPoolAliasEvents;
import net.frozenblock.lib.platform.ModLoader;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.RandomGroupPoolAlias;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomGroupPoolAlias.class)
public class RandomGroupPoolAliasMixin {

	@Shadow
	@Final
	@Mutable
	private WeightedList<List<PoolAliasBinding>> groups;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void frozenLib$modifyRandomGroupPoolAlias(WeightedList<List<PoolAliasBinding>> groups, CallbackInfo info) {
		if (FrozenLibEarlyConstants.IS_DATAGEN && ModLoader.isDevelopmentEnvironment()) return;

		final RandomGroupPoolAliasEvents.Context context = RandomGroupPoolAliasEvents.Context.of(this.groups);
		RandomGroupPoolAliasEvents.MODIFY.invoker().modify(context);

		if (context.isModified()) this.groups = context.buildGroups();
	}
}
