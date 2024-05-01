/*
 * Copyright (C) 2024 FrozenBlock
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

package net.frozenblock.lib.config.api.sync.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.frozenblock.lib.config.api.sync.SyncBehavior;

/**
 * Provides the attributes of a syncable config entry.
 * See {@link net.frozenblock.lib.config.frozenlib_config.FrozenLibConfig} and {@link net.frozenblock.lib.config.frozenlib_config.gui.FrozenLibConfigGui} for an example.
 * @since 1.5.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EntrySyncData {

	/**
	 * Used to identify config entries so their attributes can impact GUIs.
	 * Without this, it isn't possible to find the field needed in order to access their unique attributes for syncing.
	 */
	 String value() default "";

	/**
	 * Used to determine the behavior of a config entry on a server/LAN world.
	 */
	 SyncBehavior behavior() default SyncBehavior.SYNCABLE;

}
