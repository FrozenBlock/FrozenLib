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

package net.frozenblock.lib.platform;

import lombok.experimental.UtilityClass;
import static net.frozenblock.lib.platform.PlatformUtil.load;
import net.frozenblock.lib.platform.service.CompostableRegistryHelper;
import net.frozenblock.lib.platform.service.CreativeTabHelper;
import net.frozenblock.lib.platform.service.DataAttachmentHelper;
import net.frozenblock.lib.platform.service.DefaultAttributeRegistryHelper;
import net.frozenblock.lib.platform.service.EventHelper;
import net.frozenblock.lib.platform.service.FlammableBlockRegistryHelper;
import net.frozenblock.lib.platform.service.GameHelper;
import net.frozenblock.lib.platform.service.NetworkingHelper;
import net.frozenblock.lib.platform.service.RegistryHelper;
import net.frozenblock.lib.platform.service.ResourceLoaderHelper;
import net.frozenblock.lib.platform.service.StrippableBlockRegistryHelper;

@UtilityClass
public class FrozenLibInitPlatformUtils {
	public static final GameHelper GAME = load(GameHelper.class);
	public static final RegistryHelper REGISTRY = load(RegistryHelper.class);
	public static final DataAttachmentHelper DATA_ATTACHMENT = load(DataAttachmentHelper.class);
	public static final DefaultAttributeRegistryHelper DEFAULT_ATTRIBUTE_REGISTRY = load(DefaultAttributeRegistryHelper.class);
	public static final EventHelper EVENT = load(EventHelper.class);
	public static final NetworkingHelper NETWORKING = load(NetworkingHelper.class);
	public static final ResourceLoaderHelper RESOURCE_LOADER = load(ResourceLoaderHelper.class);
	public static final FlammableBlockRegistryHelper FLAMMABLE_BLOCK_REGISTRY = load(FlammableBlockRegistryHelper.class);
	public static final CompostableRegistryHelper COMPOSTABLE_REGISTRY = load(CompostableRegistryHelper.class);
	public static final StrippableBlockRegistryHelper STRIPPABLE_BLOCK_REGISTRY = load(StrippableBlockRegistryHelper.class);
	public static final CreativeTabHelper CREATIVE_TAB = load(CreativeTabHelper.class);
}
