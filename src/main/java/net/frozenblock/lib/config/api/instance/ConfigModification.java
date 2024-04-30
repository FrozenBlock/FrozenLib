/*
 * Copyright 2023 FrozenBlock
 * Copyright 2023 FrozenBlock
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
 *
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 FrozenBlock
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2022 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2021-2023 QuiltMC
 * ;;match_from: \/\*\r?\n \* Copyright (\(c\) )?2022-2023 QuiltMC
 * ;;match_from: \/\/\/ Q[Uu][Ii][Ll][Tt]
 */

package net.frozenblock.lib.config.api.instance;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.FrozenLogUtils;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.impl.network.ConfigSyncModification;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper class for modifying configs
 * @param modification The consumer for applying modifications
 * @param <T> The type of the config class
 */
public record ConfigModification<T>(Consumer<T> modification) {

    public static <T> T modifyConfig(Config<T> config, T original, boolean excludeNonSync) {
        try {
			// clone
			T instance = config.configClass().getConstructor().newInstance();
			copyInto(original, instance);

			// modify
			var list = ConfigRegistry.getModificationsForConfig(config)
				.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue())
				.toList();

			config.setSynced(false);
			for (Map.Entry<ConfigModification<T>, Integer> modification : list) {
				var consumer = modification.getKey().modification;
				if (consumer instanceof ConfigSyncModification || !excludeNonSync) {
					modification.getKey().modification.accept(instance);
				}
			}

			return instance;
		} catch (Exception e) {
			FrozenLogUtils.logError("Failed to modify config, returning original.", true, e);
			return original;
		}
    }

    public static <T> void copyInto(@NotNull T source, T destination, boolean isSyncModification) {
        Class<?> clazz = source.getClass();
        while (!clazz.equals(Object.class)) {
            for (Field field : clazz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);
				if (isSyncModification && !ConfigSyncModification.isSyncable(field)) continue;
                try {
                    field.set(destination, field.get(source));
                } catch (IllegalAccessException e) {
					FrozenLogUtils.logError("Failed to copy field " + field.getName(), true, e);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

	public static <T> void copyInto(@NotNull T source, T destination) {
		copyInto(source, destination, false);
	}

	@Environment(EnvType.CLIENT)
	public enum EntryPermissionType {
		CAN_MODIFY(true, Optional.empty(), Optional.empty()),
		LOCKED_FOR_UNKNOWN_REASON(false, Optional.of(Component.translatable("tooltip.frozenlib.locked_due_to_unknown_reason")), Optional.of(Component.translatable("tooltip.frozenlib.locked_due_to_unknown_reason"))),
		LOCKED_DUE_TO_SERVER(false, Optional.of(Component.translatable("tooltip.frozenlib.locked_due_to_server")), Optional.of(Component.translatable("tooltip.frozenlib.locked_due_to_server_lan"))),
		LOCKED_DUE_TO_SYNC(false, Optional.of(Component.translatable("tooltip.frozenlib.locked_due_to_sync")), Optional.of(Component.translatable("tooltip.frozenlib.locked_due_to_sync_lan")));

		public final boolean canModify;
		public final Optional<Component> tooltip;
		public final Optional<Component> lanTooltip;

		EntryPermissionType(boolean canModify, Optional<Component> tooltip, Optional<Component> lanTooltip) {
			this.canModify = canModify;
			this.tooltip = tooltip;
			this.lanTooltip = lanTooltip;
		}
	}
}
