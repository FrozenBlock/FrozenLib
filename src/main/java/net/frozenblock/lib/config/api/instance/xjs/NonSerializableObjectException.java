package net.frozenblock.lib.config.api.instance.xjs;

/*
 Source: https://github.com/PersonTheCat/CatLib
 License: GNU GPL-3.0
 */
public class NonSerializableObjectException extends Exception {
	private NonSerializableObjectException(final String msg) {
		super(msg);
	}

	public static NonSerializableObjectException unsupportedKey(final Object key) {
		return new NonSerializableObjectException("Cannot serialize map of type " + key.getClass() + ". Keys must be strings.");
	}

	public static NonSerializableObjectException defaultRequired() {
		return new NonSerializableObjectException("Cannot serialize object. Generic types must have defaults.");
	}
}
