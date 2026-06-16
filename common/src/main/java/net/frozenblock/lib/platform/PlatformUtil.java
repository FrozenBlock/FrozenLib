package net.frozenblock.lib.platform;

import net.frozenblock.lib.FrozenLibLogUtils;

import java.util.ServiceLoader;

public class PlatformUtil {
    // This code is used to load a service for the current environment. Your implementation of the service must be defined
    // manually by including a text file in META-INF/services named with the fully qualified class name of the service.
    // Inside the file you should write the fully qualified class name of the implementation to load for the platform. For
    // example our file on Forge points to ForgePlatformHelper while Fabric points to FabricPlatformHelper.
    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz, PlatformUtil.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        FrozenLibLogUtils.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
