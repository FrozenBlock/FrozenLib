package net.frozenblock.lib.config.newconfig.registry;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/**
 * A namespaced identifier consisting of a namespace and a path.
 * Similar to Minecraft's {@link Identifier} class, but simpler and without validation.
 */
public record ID(String namespace, String path) {
	public static final Codec<ID> CODEC = Codec.STRING.xmap(ID::parse, ID::toString);
	public static final StreamCodec<ByteBuf, ID> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(ID::parse, ID::toString);
	public static final char NAMESPACE_SEPARATOR = Identifier.NAMESPACE_SEPARATOR;

	/**
	 * Creates a new ID with the given namespace and path.
	 * Both values must be non-null and non-empty.
	 */
	public ID {
		if (namespace == null || namespace.isEmpty()) throw new IllegalArgumentException("Namespace must be non-null and non-empty");
		if (path == null || path.isEmpty()) throw new IllegalArgumentException("Path must be non-null and non-empty");
	}

	public ID(Identifier identifier) {
		this(identifier.getNamespace(), identifier.getPath());
	}

	/**
	 * Convenience factory
	 */
	public static ID of(String namespace, String path) {
		return new ID(namespace, path);
	}

	public static ID of(Identifier identifier) {
		return new ID(identifier);
	}

	/**
	 * Parses strings of the form "namespace:path". If no namespace is provided, the provided defaultNamespace is used.
	 */
	public static ID parse(String combined) {
		if (combined == null) throw new IllegalArgumentException("combined must not be null");

		final int namespaceSeparatorIndex = combined.indexOf(NAMESPACE_SEPARATOR);
		if (namespaceSeparatorIndex < 0) throw new IllegalArgumentException("No namespace specified in ID: " + combined);

		final String namespace = combined.substring(0, namespaceSeparatorIndex);
		final String path = combined.substring(namespaceSeparatorIndex + 1);
		return new ID(namespace, path);
	}

	public ID withSuffix(String suffix) {
		return new ID(this.namespace, this.path + suffix);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ID id = (ID) o;
		return namespace.equals(id.namespace) && path.equals(id.path);
	}

	@Override
	public String toString() {
		return namespace + ':' + path;
	}
}
