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

package net.frozenblock.lib.platform.api.registry;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.frozenblock.lib.platform.ModLoader;

public class OptionalDeferred<A, B extends A, T extends DeferredHolder<A, B>> implements Supplier<Optional<T>> {
	private static final OptionalDeferred<?, ?, ?> EMPTY = new OptionalDeferred<>();
	private final Optional<T> holder;

	private OptionalDeferred() {
		this.holder = Optional.empty();
	}

	private OptionalDeferred(Optional<T> holder) {
		this.holder = holder;
	}

	private OptionalDeferred(Supplier<T> holder) {
		this.holder = Optional.ofNullable(holder.get());
	}

	public static <A, B extends A, T extends DeferredHolder<A, B>> OptionalDeferred<A, B, T> empty() {
		return (OptionalDeferred<A, B, T>) EMPTY;
	}

	public static <A, B extends A, T extends DeferredHolder<A, B>> OptionalDeferred<A, B, T> of(Supplier<T> holder) {
		return new OptionalDeferred<>(holder);
	}

	public static <A, B extends A, T extends DeferredHolder<A, B>> OptionalDeferred<A, B, T> modDependent(String modId, Supplier<T> holder) {
		return ModLoader.isModLoaded(modId) ? of(holder) : empty();
	}

	@Override
	public Optional<T> get() {
		return this.holder;
	}

	public boolean isPresent() {
		return holder.isPresent();
	}

	public boolean isEmpty() {
		return holder.isEmpty();
	}

	public void ifPresent(Consumer<? super T> action) {
		this.holder.ifPresent(action);
	}

	public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
		this.holder.ifPresentOrElse(action, emptyAction);
	}

	public Optional<T> filter(Predicate<? super T> predicate) {
		return this.holder.filter(predicate);
	}

	public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
		return this.holder.map(mapper);
	}

	public <U> Optional<U> flatMap(Function<? super T, ? extends Optional<? extends U>> mapper) {
		return this.holder.flatMap(mapper);
	}

	public Optional<T> or(Supplier<? extends Optional<? extends T>> supplier) {
		return this.holder.or(supplier);
	}

	public Stream<T> stream() {
		return this.holder.stream();
	}

	public T orElse(T other) {
		return this.holder.orElse(other);
	}

	public T orElseGet(Supplier<? extends T> supplier) {
		return this.holder.orElseGet(supplier);
	}

	public T orElseThrow() {
		return this.holder.orElseThrow();
	}

	public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		return this.holder.orElseThrow(exceptionSupplier);
	}

	@Override
	public int hashCode() {
		return this.holder.hashCode();
	}

	@Override
	public String toString() {
		return this.holder.map(value -> "OptionalDeferred[" + value + "]").orElse("OptionalDeferred.empty");
	}
}
