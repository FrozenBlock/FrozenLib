/*
 * Copyright (C) 2024-2025 FrozenBlock
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

package net.frozenblock.lib.datafix.api;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

/**
 * A {@link DataFix} specialized for fixing the name of an entity.
 */
public abstract class FrozenEntityRenameFix extends DataFix {

    private final String name;

    public FrozenEntityRenameFix(Schema outputSchema, String name) {
        super(outputSchema, false);
        this.name = name;
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<Pair<String, String>> type = DSL.named(References.ENTITY_NAME.typeName(), NamespacedSchema.namespacedString());
        if (!Objects.equals(this.getInputSchema().getType(References.ENTITY_NAME), type)) {
            throw new IllegalStateException("Unexpected entity name type.");
        }
		return this.fixTypeEverywhere(this.name, type, dynamicOps -> pair -> pair.mapSecond(this::fixEntity));
    }

    protected abstract String fixEntity(String string);

    public static DataFix create(Schema outputSchema, String name, Function<String, String> function) {
        return new FrozenEntityRenameFix(outputSchema, name) {
            @Override
            protected String fixEntity(String string) {
                return function.apply(string);
            }
        };
    }
}
