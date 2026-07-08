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

package net.frozenblock.lib.platform.event;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.frozenblock.lib.event.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;

/**
 * A {@link Event} backed by a real NeoForge {@code IEventBus} dispatch.
 *
 * <p>NeoForge's {@code IEventBus} dispatches by concrete {@link net.neoforged.bus.api.Event} subclass, while
 * FrozenLib's listener-callback-interface model (mirroring Fabric's {@code Event<T>}) is
 * generic over an arbitrary functional interface {@code T}. To bridge the two without
 * forcing every FrozenLib event listener interface to extend NeoForge's {@code Event},
 * each instance subscribes its own identity-filtered listener to one shared internal
 * {@link BridgeEvent} class, and {@link #invoker()} returns a dynamic proxy that posts a
 * {@code BridgeEvent} through the real bus on every call.
 *
 * @param <T> the listener callback type
 */
public class NeoEvent<T> implements Event<T> {
	private final Class<T> listenerType;
	private final Function<T[], T> invokerFactory;
	private final List<T> listeners = new ArrayList<>();
	private volatile T cachedInvoker;
	private volatile boolean bridgeRegistered;

	public NeoEvent(Class<T> listenerType, Function<T[], T> invokerFactory) {
		this.listenerType = listenerType;
		this.invokerFactory = invokerFactory;
	}

	private synchronized void registerBridge() {
		if (this.bridgeRegistered) return;

		IEventBus bus = FrozenLibEventBus.get();
		if (bus == null) return; // still too early — try again on next invocation

		bus.addListener(BridgeEvent.class, event -> {
			if (event.source != this) return;
			this.dispatch(event);
		});

		this.bridgeRegistered = true;
	}

	@Override
	public synchronized void register(T listener) {
		this.listeners.add(listener);
		this.cachedInvoker = null;
	}

	@Override
	public synchronized void unregister(T listener) {
		this.listeners.remove(listener);
		this.cachedInvoker = null;
	}

	@Override
	public synchronized boolean isRegistered(T listener) {
		return this.listeners.contains(listener);
	}

	@Override
	public synchronized void clearCallbacks() {
		this.listeners.clear();
		this.cachedInvoker = null;
	}

	@Override
	public T invoker() {
		T local = this.cachedInvoker;
		if (local == null) {
			synchronized (this) {
				local = this.cachedInvoker;
				if (local == null) {
					local = this.createInvoker();
					this.cachedInvoker = local;
				}
			}
		}
		return local;
	}

	@SuppressWarnings("unchecked")
	private T createInvoker() {
		return (T) Proxy.newProxyInstance(
			this.listenerType.getClassLoader(),
			new Class<?>[]{this.listenerType},
			(ignoredProxy, method, args) -> {
				final BridgeEvent event = new BridgeEvent(this, method, args);
				final IEventBus bus = FrozenLibEventBus.get();
				if (bus != null) {
					this.registerBridge();
					bus.post(event);
				} else {
					this.dispatch(event);
				}
				return event.result;
			}
		);
	}

	@SuppressWarnings("unchecked")
	private void dispatch(BridgeEvent event) {
		T[] snapshot;
		synchronized (this) {
			snapshot = this.listeners.toArray((T[]) Array.newInstance(this.listenerType, this.listeners.size()));
		}
		T arrayInvoker = this.invokerFactory.apply(snapshot);
		try {
			event.result = event.invokedMethod.invoke(arrayInvoker, event.invokedArgs);
		} catch (ReflectiveOperationException ex) {
			throw new RuntimeException("Failed to dispatch FrozenLib NeoForge event", ex);
		}
	}

	/** Internal bridge event posted on FrozenLib's mod event bus to drive real IEventBus traversal. */
	private static final class BridgeEvent extends net.neoforged.bus.api.Event implements IModBusEvent {
		private final NeoEvent<?> source;
		private final Method invokedMethod;
		private final Object[] invokedArgs;
		private Object result;

		private BridgeEvent(NeoEvent<?> source, Method invokedMethod, Object[] invokedArgs) {
			this.source = source;
			this.invokedMethod = invokedMethod;
			this.invokedArgs = invokedArgs;
		}
	}
}
