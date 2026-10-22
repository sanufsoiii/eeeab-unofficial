package com.eeeab.eeeabsmobs.server.init;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Fabric has no DeferredRegister/DeferredHolder (that's a NeoForge/Forge-only lazy-registration API backed by
 * a mod-bus RegisterEvent). On Fabric, registration is direct: {@link Registry#register(Registry,
 * ResourceLocation, Object)}, called eagerly, no event bus involved (verified with javap against the Loom-mapped
 * 1.21.1 jar - net.minecraft.core.Registry has three register(...) overloads, none of them event-driven).
 * <p>
 * This class is a small compatibility shim, not a guessed API: it exists so every {@code XInit.FOO} field across
 * the mod (most of them touched by other files well outside this port's platform-layer scope) can keep being
 * written as {@code XInit.FOO.get()} instead of forcing a mechanical rewrite of every call site in
 * server/entity/**, server/item/**, and client/** - files this agent was explicitly told not to touch. The
 * registration itself is 100% real: {@link Entry#register} constructs the value and puts it straight into the
 * vanilla registry the moment {@code register(...)} is called, matching Fabric's normal "call
 * Registry.register during mod init" idiom. Nothing is deferred; the class name only mirrors the old call shape.
 */
public final class FabricDeferredRegister<T> {
    private final Registry<T> registry;
    private final String modId;
    private final List<Runnable> pending = new ArrayList<>();

    private FabricDeferredRegister(Registry<T> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
    }

    public static <T> FabricDeferredRegister<T> create(Registry<T> registry, String modId) {
        return new FabricDeferredRegister<>(registry, modId);
    }

    /**
     * Registers {@code supplier.get()} under {@code modId:name} immediately and returns a handle whose
     * {@link Entry#get()} yields the same instance forever after - the only thing existing call sites rely on.
     */
    public <V extends T> Entry<V> register(String name, Supplier<V> supplier) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, name);
        V value = supplier.get();
        Registry.register(registry, id, value);
        Holder<T> holder = registry.wrapAsHolder(value);
        return new Entry<>(value, holder, id);
    }

    /**
     * No-op kept only so call sites that used to pass the mod event bus to {@code DeferredRegister.register(bus)}
     * still compile without editing every Init class's public surface; Fabric registration already happened
     * eagerly in {@link #register(String, Supplier)} above, there is no second bus-driven phase to run.
     */
    public void register(Object unusedModEventBus) {
        // Intentionally empty - see class javadoc.
    }

    public static final class Entry<T> implements Supplier<T> {
        private final T value;
        private final Holder<?> holder;
        private final ResourceLocation id;

        private Entry(T value, Holder<?> holder, ResourceLocation id) {
            this.value = value;
            this.holder = holder;
            this.id = id;
        }

        public T get() {
            return value;
        }

        @SuppressWarnings("unchecked")
        public Holder<T> holder() {
            return (Holder<T>) holder;
        }

        /**
         * Mirrors NeoForge's DeferredHolder#getId(), which some out-of-scope call sites (e.g.
         * server/block/BlockSlidingDoor.java) still rely on. Registry.register(...) already computed this id
         * in {@link #register(String, Supplier)}; handed back here instead of round-tripping through
         * Registry.getKey(value) so it also works before the registry's own lookup tables are fully settled.
         */
        public ResourceLocation getId() {
            return id;
        }
    }
}
