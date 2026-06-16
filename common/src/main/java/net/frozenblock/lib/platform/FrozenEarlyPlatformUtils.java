package net.frozenblock.lib.platform;

import lombok.experimental.UtilityClass;
import net.frozenblock.lib.platform.service.LoaderHelper;
import static net.frozenblock.lib.platform.PlatformUtil.load;

@UtilityClass
public class FrozenEarlyPlatformUtils {

	public static final LoaderHelper LOADER = load(LoaderHelper.class);
}
