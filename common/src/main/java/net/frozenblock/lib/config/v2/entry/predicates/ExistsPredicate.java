/*
 * Copyright (C) 2026 FrozenBlock
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

package net.frozenblock.lib.config.v2.entry.predicates;

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.config.v2.registry.ConfigV2Registry;
import net.frozenblock.lib.config.v2.registry.ID;
import net.minecraft.world.level.storage.loot.ValidationContext;

public class ExistsPredicate implements ConfigPredicate {
	public static final MapCodec<ExistsPredicate> CODEC = ID.CODEC.fieldOf("entry").xmap(ExistsPredicate::new, predicate -> predicate.id);
	private final ID id;
	private Boolean exists = null;

	public ExistsPredicate(ID id) {
		this.id = id;
	}

	@Override
	public Boolean get() {
		if (this.exists == null) this.exists = ConfigV2Registry.getEntry(this.id) != null;
		return this.exists;
	}

	@Override
	public MapCodec<ExistsPredicate> codec() {
		return CODEC;
	}

	@Override
	public void validate(ValidationContext context) {}
}
