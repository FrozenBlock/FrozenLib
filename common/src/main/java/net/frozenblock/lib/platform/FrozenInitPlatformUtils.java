package net.frozenblock.lib.platform;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.platform.service.RegistryHelper;
import static net.frozenblock.lib.platform.PlatformUtil.load;

@UtilityClass
public class FrozenInitPlatformUtils {

	public static final RegistryHelper REGISTRY = load(RegistryHelper.class);
}
