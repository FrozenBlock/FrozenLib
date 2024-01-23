/*
 * Copyright 2023-2024 The Quilt Project
 * Copyright 2023-2024 FrozenBlock
 * Modified to work on Fabric
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.qsl.frozenblock.core.registry.mixin;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.Event;
import net.frozenblock.lib.event.api.FrozenEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEventStorage;
import org.quiltmc.qsl.frozenblock.core.registry.api.event.RegistryEvents;
import org.quiltmc.qsl.frozenblock.core.registry.impl.event.MutableRegistryEntryContextImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Stores and invokes registry events.
 * Handles applying and creating sync data.
 */
@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<V> implements Registry<V>, RegistryEventStorage<V> {
	@Unique
	private MutableRegistryEntryContextImpl<V> frozenLib_quilt$entryContext;

	@Unique
	private Event<RegistryEvents.EntryAdded<V>> frozenLib_quilt$entryAddedEvent;


	// HACK TODO for some reason initializing this like normal doesnt work. i dont care to figure out why - glitch
	@Inject(method = "<init>(Lnet/minecraft/resources/ResourceKey;Lcom/mojang/serialization/Lifecycle;Z)V", at = @At("TAIL"))
	private void hackBecauseMixinHatesMe(ResourceKey<? extends Registry<V>> key, Lifecycle lifecycle, boolean useIntrusiveHolders, CallbackInfo ci) {
		this.frozenLib_quilt$entryContext = new MutableRegistryEntryContextImpl<>(this);
		this.frozenLib_quilt$entryAddedEvent = FrozenEvents.createEnvironmentEvent(RegistryEvents.EntryAdded.class,
				callbacks -> context -> {
					for (var callback : callbacks) {
						callback.onAdded(context);
					}
				});
	}


	@ModifyVariable(
		method = "registerMapping(ILnet/minecraft/resources/ResourceKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/core/Holder$Reference;",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;",
			ordinal = 0
		)
	)
	private Object quilt$eagerFillReference(Object object, int id, ResourceKey<V> key, V value, Lifecycle lifecycle) {
		if (object instanceof Holder.Reference reference) {
			reference.bindValue(value);
		}
		return object;
	}

	/**
	 * Invokes the entry add event.
	 */
	@SuppressWarnings({"ConstantConditions", "unchecked"})
	@Inject(
			method = "registerMapping(ILnet/minecraft/resources/ResourceKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/core/Holder$Reference;",
			at = @At("RETURN")
	)
	private void quilt$invokeEntryAddEvent(int rawId, ResourceKey<V> key, V entry, Lifecycle lifecycle, CallbackInfoReturnable<Holder<V>> cir) {
		this.frozenLib_quilt$entryContext.set(key.location(), entry, rawId);
		RegistryEventStorage.as((MappedRegistry<V>) (Object) this).frozenLib_quilt$getEntryAddedEvent().invoker().onAdded(this.frozenLib_quilt$entryContext);
	}

	@Override
	public Event<RegistryEvents.EntryAdded<V>> frozenLib_quilt$getEntryAddedEvent() {
		return this.frozenLib_quilt$entryAddedEvent;
	}
}
