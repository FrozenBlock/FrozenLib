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

package net.frozenblock.lib.config.api.instance.xjs;

/*
 Source: https://github.com/PersonTheCat/CatLib
 License: GNU GPL-3.0
 */
public class NonSerializableObjectException extends Exception {
	public NonSerializableObjectException(final String msg) {
		super(msg);
	}

	public static NonSerializableObjectException unsupportedKey(final Object key) {
		return new NonSerializableObjectException("Cannot serialize map of type " + key.getClass() + ". Keys must be strings.");
	}

	public static NonSerializableObjectException defaultRequired() {
		return new NonSerializableObjectException("Cannot serialize object. Generic types must have defaults.");
	}
}
