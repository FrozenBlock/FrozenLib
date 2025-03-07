/*
 * Copyright 2024-2025 The Quilt Project
 * Copyright 2024-2025 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.qsl.frozenblock.misc.datafixerupper.api;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TypeTemplate;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import java.util.Map;
import java.util.function.Supplier;
import org.jetbrains.annotations.Range;

/**
 * Represents an empty {@link Schema}, having no parent and containing no type definitions.
 * <p>
 * Modified to work on Fabric
 */
public final class EmptySchema extends FirstSchema {
    /**
     * Constructs an empty schema.
     *
     * @param versionKey the data version key
     */
    public EmptySchema(@Range(from = 0, to = Integer.MAX_VALUE) int versionKey) {
        super(versionKey);
    }

    // Ensure the schema stays empty.
    @Override
    public void registerType(boolean recursive, DSL.TypeReference type, Supplier<TypeTemplate> template) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Map<String, Type<?>> buildTypes() {
        return Object2ObjectMaps.emptyMap();
    }
}
