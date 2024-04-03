package net.frozenblock.lib.config.api.instance.xjs;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Either;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import personthecat.fresult.Result;
import xjs.core.JsonContainer;
import xjs.core.JsonObject;
import xjs.core.JsonValue;
import xjs.core.PathFilter;

import java.util.*;
import java.util.stream.Collectors;

/*
 Source: https://github.com/PersonTheCat/CatLib
 License: GNU GPL-3.0
 */
/**
 * An object representing every accessor in a JSON object leading to a value.
 *
 * <p>In other words, this object is a container holding keys and indices which
 * point to a value at some arbitrary depth in a JSON array or object.
 */
public class JsonPath implements Iterable<Either<String, Integer>> {

	private final List<Either<String, Integer>> path;
	private final String raw;

	public JsonPath(final List<Either<String, Integer>> path) {
		this.path = path;
		this.raw = serialize(path);
	}

	public JsonPath(final List<Either<String, Integer>> path, final String raw) {
		this.path = path;
		this.raw = raw;
	}

	/**
	 * Creates a new JSON path builder, used for programmatically generating new
	 * JSON path representations.
	 *
	 * @return A new {@link JsonPathBuilder} for constructing JSON paths.
	 */
	public static JsonPathBuilder builder() {
		return new JsonPathBuilder();
	}

	/**
	 * A lightweight, immutable alternative to {@link JsonPathBuilder}, specifically
	 * intended for tracking paths over time in scenarios where an actual {@link JsonPath}
	 * may not be needed.
	 *
	 * <p>For example, an application performing analysis on a body of JSON data
	 * might "track" the current path using one of these objects. If for some reason
	 * a specific path needs to be saved, the dev might call {@link Stub#capture()}
	 * to generate a proper {@link JsonPath}, which can be reflected on at a later
	 * time.
	 *
	 * <p>This is equivalent to using a regular {@link JsonPathBuilder}, while being
	 * modestly less expensive in that context. However, because it is immutable, it
	 * may be repeatedly passed into various other methods without the threat of any
	 * accidental mutations further down the stack.
	 *
	 * @return {@link Stub#EMPTY}, for building raw JSON paths.
	 */
	public static Stub stub() {
		return Stub.EMPTY;
	}

	/**
	 * Deserializes the given raw path into a collection of keys and indices.
	 *
	 * @throws CommandSyntaxException If the path is formatted incorrectly.
	 * @param raw The raw JSON path being deserialized.
	 * @return An object representing every accessor leading to a JSON value.
	 */
	public static JsonPath parse(final String raw) throws CommandSyntaxException {
		return parse(new StringReader(raw));
	}

	/**
	 * Deserializes the given raw path into a collection of keys and indices.
	 *
	 * @throws CommandSyntaxException If the path is formatted incorrectly.
	 * @param reader A reader exposing the raw JSON path being deserialized.
	 * @return An object representing every accessor leading to a JSON value.
	 */
	public static JsonPath parse(final StringReader reader) throws CommandSyntaxException {
		final List<Either<String, Integer>> path = new ArrayList<>();
		final int begin = reader.getCursor();

		while(reader.canRead() && reader.peek() != ' ') {
			final char c = reader.read();
			if (c == '.') {
				checkDot(reader, begin);
			} else if (inKey(c)) {
				path.add(Either.left(c + readKey(reader)));
			} else if (c == '[') {
				checkDot(reader, begin);
				path.add(Either.right(reader.readInt()));
				reader.expect(']');
			} else {
				throw cmdSyntax(reader, "Invalid character");
			}
		}
		return new JsonPath(path, reader.getString().substring(begin, reader.getCursor()));
	}

	private static String readKey(final StringReader reader) {
		final int start = reader.getCursor();
		while (reader.canRead() && inKey(reader.peek())) {
			reader.skip();
		}
		return reader.getString().substring(start, reader.getCursor());
	}

	private static boolean inKey(final char c) {
		return c != '.' && c != ' ' && c != '[';
	}

	private static void checkDot(final StringReader reader, final int begin) throws CommandSyntaxException {
		final int cursor = reader.getCursor();
		final char last = reader.getString().charAt(cursor - 2);
		if (cursor - 1 == begin || last == '.') {
			throw cmdSyntax(reader, "Unexpected accessor");
		}
	}

	/**
	 * Variant of {@link #parse(String)} which returns instead of throwing
	 * an exception.
	 *
	 * @param raw The raw JSON path being deserialized.
	 * @return An object representing every accessor leading to a JSON value.
	 */
	public static Result<JsonPath, CommandSyntaxException> tryParse(final String raw) {
		return Result.of(() -> parse(raw)).ifErr(Result::IGNORE);
	}

	/**
	 * Generates a new JsonPath from a string containing only keys.
	 *
	 * <p>This method is intended as optimization in cases where no
	 * arrays are needed.
	 *
	 * @param raw The raw JSON path containing <b>keys only</b>.
	 * @return A new object representing this path.
	 */
	public static JsonPath objectOnly(final String raw) {
		final List<Either<String, Integer>> path = new ArrayList<>();
		for (final String key : raw.split("\\.")) {
			path.add(Either.left(key));
		}
		return new JsonPath(path, raw);
	}

	/**
	 * Converts the given JSON path data into a raw string.
	 *
	 * @param path The parsed JSON path being serialized.
	 * @return A string representing the equivalent path.
	 */
	public static String serialize(final Collection<Either<String, Integer>> path) {
		final StringBuilder sb = new StringBuilder();
		for (final Either<String, Integer> either : path) {
			either.ifLeft(s -> {
				sb.append('.');
				sb.append(s);
			});
			either.ifRight(i -> {
				sb.append('[');
				sb.append(i);
				sb.append(']');
			});
		}
		final String s = sb.toString();
		return s.startsWith(".") ? s.substring(1) : s;
	}

	/**
	 * Generates a list of every possible JSON path in this object.
	 *
	 * @param json The json containing the expected paths.
	 * @return A list of objects representing these paths.
	 */
	public static List<JsonPath> getAllPaths(final JsonObject json) {
		return toPaths(json.getPaths());
	}

	/**
	 * Generates a list of every used JSON path in this object.
	 *
	 * @param json The json containing the expected paths.
	 * @return A list of objects representing these paths.
	 */
	public static List<JsonPath> getUsedPaths(final JsonObject json) {
		return toPaths(json.getPaths(PathFilter.USED));
	}

	/**
	 * Generates a list of every unused JSON path in this object.
	 *
	 * @param json The json containing the expected paths.
	 * @return A list of objects representing these paths.
	 */
	public static List<JsonPath> getUnusedPaths(final JsonObject json) {
		return toPaths(json.getPaths(PathFilter.UNUSED));
	}

	private static List<JsonPath> toPaths(final List<String> raw) {
		return raw.stream()
			.map(JsonPath::parseUnchecked)
			.collect(Collectors.toList());
	}

	private static JsonPath parseUnchecked(final String path) {
		try {
			return parse(path);
		} catch (final CommandSyntaxException e) {
			throw new IllegalStateException("JSON lib returned unusable path", e);
		}
	}

	public JsonContainer getLastContainer(final JsonObject json) {
		return XjsUtils.getLastContainer(json, this);
	}

	public Optional<JsonValue> getValue(final JsonObject json) {
		return XjsUtils.getValueFromPath(json, this);
	}

	public void setValue(final JsonObject json, final @Nullable JsonValue value) {
		XjsUtils.setValueFromPath(json, this, value);
	}

	public JsonPath getClosestMatch(final JsonObject json) {
		return XjsUtils.getClosestMatch(json, this);
	}

	public int getLastAvailable(final JsonObject json) {
		return XjsUtils.getLastAvailable(json, this);
	}

	public JsonPathBuilder toBuilder() {
		return new JsonPathBuilder(new ArrayList<>(this.path), new StringBuilder(this.raw));
	}

	public Stub beginTracking() {
		return new Stub(this.raw);
	}

	public Collection<Either<String, Integer>> asCollection() {
		return Collections.unmodifiableCollection(this.path);
	}

	public String asRawPath() {
		return this.raw;
	}

	public boolean isEmpty() {
		return this.path.isEmpty();
	}

	public int size() {
		return this.path.size();
	}

	public Either<String, Integer> get(final int index) {
		return this.path.get(index);
	}

	public int indexOf(final String key) {
		return this.path.indexOf(Either.left(key));
	}

	public int lastIndexOf(final String key) {
		return this.path.lastIndexOf(Either.left(key));
	}

	public List<Either<String, Integer>> subList(final int s, final int e) {
		return this.path.subList(s, e);
	}

	public JsonPath subPath(final String key) {
		final int index = this.indexOf(key);
		return index < 0 ? this : this.subPath(index, this.size());
	}

	public JsonPath subPath(final int s, final int e) {
		return new JsonPath(this.subList(s, e));
	}

	public JsonPath append(final JsonPath path) {
		return this.append(path, 0, path.size());
	}

	public JsonPath append(final JsonPath path, final int startInclusive) {
		return this.append(path, startInclusive, path.size());
	}

	public JsonPath append(final JsonPath path, final int startInclusive, final int endExclusive) {
		return this.toBuilder().append(path, startInclusive, endExclusive).build();
	}

	@NotNull
	@Override
	public Iterator<Either<String, Integer>> iterator() {
		return this.path.iterator();
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof JsonPath) {
			return this.path.equals(((JsonPath) o).path);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

	@Override
	public String toString() {
		return this.raw;
	}

	/**
	 * A builder used for manually constructing JSON paths in-code.
	 */
	public static class JsonPathBuilder {

		private final List<Either<String, Integer>> path;
		private final StringBuilder raw;

		private JsonPathBuilder() {
			this(new ArrayList<>(), new StringBuilder());
		}

		private JsonPathBuilder(final List<Either<String, Integer>> path, final StringBuilder raw) {
			this.path = path;
			this.raw = raw;
		}

		public JsonPathBuilder key(final String key) {
			this.path.add(Either.left(key));
			if (this.raw.length() > 0) {
				this.raw.append('.');
			}
			this.raw.append(key);
			return this;
		}

		public JsonPathBuilder index(final int index) {
			this.path.add(Either.right(index));
			this.raw.append('[').append(index).append(']');
			return this;
		}

		public JsonPathBuilder up(final int count) {
			JsonPathBuilder builder = this;
			for (int i = 0; i < count; i++) {
				builder = builder.up();
			}
			return builder;
		}

		public JsonPathBuilder up() {
			if (this.path.isEmpty()) {
				return this;
			} else if (this.path.size() == 1) {
				return new JsonPathBuilder();
			}
			this.path.remove(this.path.size() - 1);
			final int dot = this.raw.lastIndexOf(".");
			final int bracket = this.raw.lastIndexOf("[");
			this.raw.delete(Math.max(dot, bracket), this.raw.length());
			return this;
		}

		public JsonPathBuilder append(final JsonPath path, final int startInclusive, final int endExclusive) {
			for (int i = startInclusive; i < endExclusive; i++) {
				path.get(i).ifLeft(this::key).ifRight(this::index);
			}
			return this;
		}

		public JsonPath build() {
			return new JsonPath(this.path, this.raw.toString());
		}

		@Override
		public int hashCode() {
			return this.path.hashCode();
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof JsonPathBuilder) {
				return this.path.equals(((JsonPathBuilder) o).path);
			}
			return false;
		}
	}

	/**
	 * A lightweight, immutable builder designed for appending paths over time, wherein
	 * the more expensive {@link JsonPath} is typically unneeded.
	 */
	public static class Stub {

		private static final Stub EMPTY = new Stub("");

		private final String path;

		private Stub(final String path) {
			this.path = path;
		}

		public Stub key(final String key) {
			if (this.path.isEmpty()) {
				return new Stub(key);
			}
			return new Stub(this.path + "." + key);
		}

		public Stub index(final int index) {
			return new Stub(this.path + "[" + index + "]");
		}

		public JsonPath capture() {
			try {
				return parse(this.path);
			} catch (final CommandSyntaxException e) {
				throw new IllegalArgumentException("Invalid characters in stub: " + this.path);
			}
		}

		@Override
		public int hashCode() {
			return this.path.hashCode();
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof Stub) {
				return this.path.equals(((Stub) o).path);
			}
			return false;
		}
	}

	/**
	 * Shorthand for a simple {@link CommandSyntaxException}.
	 *
	 * @param reader The reader being used to parse an argument.
	 * @param msg The error message to display.
	 * @return A new {@link CommandSyntaxException}.
	 */
	private static CommandSyntaxException cmdSyntax(final StringReader reader, final String msg) {
		final int cursor = reader.getCursor();
		final String input = reader.getString().substring(0, cursor);
		final Message m = new LiteralMessage(msg);
		return new CommandSyntaxException(new SimpleCommandExceptionType(m), m, input, cursor);
	}
}
