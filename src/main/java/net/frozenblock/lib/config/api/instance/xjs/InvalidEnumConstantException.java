package net.frozenblock.lib.config.api.instance.xjs;

import org.jetbrains.annotations.NotNull;
import java.util.Arrays;

/*
 Source: https://github.com/PersonTheCat/CatLib
 License: GNU GPL-3.0
 */
public class InvalidEnumConstantException extends RuntimeException {
	public InvalidEnumConstantException(final String name, final Class<? extends Enum<?>> clazz) {
		super(createMessage(name, clazz));
	}

	private static String createMessage(final String name, final Class<? extends Enum<?>> clazz) {
		final String values = Arrays.toString(clazz.getEnumConstants());
		return f("{} \"{}\" does not exist. Valid options are: {}", clazz.getSimpleName(), name, values);
	}

	/**
	 * Interpolates strings by replacing instances of <code>{}</code> in order.
	 *
	 * @param s The template string being formatted.
	 * @param args A list of arguments to interpolate into this string.
	 * @return A formatted, interpolated string.
	 */
	@NotNull
	static String f(final String s, final Object... args) {
		int begin = 0, si = 0, oi = 0;
		final StringBuilder sb = new StringBuilder();
		while (true) {
			si = s.indexOf("{}", si);
			if (si >= 0) {
				sb.append(s, begin, si);
				sb.append(args[oi++]);
				begin = si = si + 2;
			} else {
				break;
			}
		}
		sb.append(s.substring(begin));
		return sb.toString();
	}
}
