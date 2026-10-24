package com.eeeab.eeeabsmobs.server.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Cross-loader DeferredRegister backed by Architectury API.
 * Preserves the call-shape and Entry methods (.get(), .holder(), .getId()) across all Init classes.
 */
public final class FabricDeferredRegister<T> {
    private final DeferredRegister<T> delegate;
    private final Registry<T> registry;
    private final String modId;

    @SuppressWarnings("unchecked")
    private FabricDeferredRegister(Registry<T> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
        ResourceKey<Registry<T>> key = (ResourceKey<Registry<T>>) (ResourceKey<?>) registry.key();
        this.delegate = DeferredRegister.create(modId, key);
    }

    public static <T> FabricDeferredRegister<T> create(Registry<T> registry, String modId) {
        return new FabricDeferredRegister<>(registry, modId);
    }

    public <V extends T> Entry<V> register(String name, Supplier<V> supplier) {
        ResourceLocation id = new ResourceLocation(modId, name);
        RegistrySupplier<V> regSupplier = delegate.register(name, supplier);
        return new Entry<>(regSupplier, registry, id);
    }

    public void register() {
        delegate.register();
    }

    public void register(Object unusedModEventBus) {
        delegate.register();
    }

    public static final class Entry<T> implements Supplier<T> {
        private final RegistrySupplier<T> supplier;
        private final Registry<?> registry;
        private final ResourceLocation id;

        private Entry(RegistrySupplier<T> supplier, Registry<?> registry, ResourceLocation id) {
            this.supplier = supplier;
            this.registry = registry;
            this.id = id;
        }

        @Override
        public T get() {
            return supplier.get();
        }

        @SuppressWarnings("unchecked")
        public Holder<T> holder() {
            T val = supplier.get();
            return (Holder<T>) ((Registry<Object>) registry).wrapAsHolder(val);
        }

        public ResourceLocation getId() {
            return id;
        }
    }
}
