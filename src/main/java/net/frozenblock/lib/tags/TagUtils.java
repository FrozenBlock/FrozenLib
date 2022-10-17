package net.frozenblock.lib.tags;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.fabric.api.tag.convention.v1.TagUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

/**
 * Contains methods related to {@link TagKey}s.
 */
public final class TagUtils {

    @Nullable
    public static <T> T getRandomEntry(TagKey<T> tag) {
        return getRandomEntry(RandomSource.create(), tag);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T getRandomEntry(RandomSource random, TagKey<T> tag) {
        Optional<? extends Registry<?>> maybeRegistry = Registry.REGISTRY.getOptional(tag.registry().location());
        Objects.requireNonNull(random);
        Objects.requireNonNull(tag);

        if (maybeRegistry.isPresent()) {
            Registry<T> registry = (Registry<T>) maybeRegistry.get();
            if (tag.isFor(registry.key())) {
                ArrayList<T> entries = new ArrayList<>();
                for (Holder<T> entry : registry.getTagOrEmpty(tag)) {
                    var optionalKey = entry.unwrapKey();
                    if (optionalKey.isPresent()) {
                        var key = optionalKey.get();
                        registry.getOptional(key).ifPresent(entries::add);
                    }
                }
                if (!entries.isEmpty()) {
                    return entries.get(random.nextInt(entries.size()));
                }
            }
        }
        return null;
    }

    public static <T> boolean isIn(TagKey<T> tagKey, T entry) {
        return TagUtil.isIn(tagKey, entry);
    }

    public static <T> boolean isIn(@Nullable RegistryAccess registryAccess, TagKey<T> tagKey, T entry) {
        return TagUtil.isIn(registryAccess, tagKey, entry);
    }

    private TagUtils() {
    }
}
