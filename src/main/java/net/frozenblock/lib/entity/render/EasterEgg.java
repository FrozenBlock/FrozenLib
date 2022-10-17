package net.frozenblock.lib.entity.render;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import net.frozenblock.lib.registry.FrozenRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class EasterEgg<T extends LivingEntity> {

    private final EntityType<T> type;
    private final ResourceLocation texture;
    private final Condition condition;

    public EasterEgg(EntityType<T> type, ResourceLocation texture, Condition condition) {
        this.type = type;
        this.texture = texture;
        this.condition = condition;
    }

    public EntityType<T> getType() {
        return this.type;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public Condition getCondition() {
        return this.condition;
    }

    public static <E extends LivingEntity> EasterEgg<E> register(ResourceLocation key, EntityType<E> type, ResourceLocation texture, String...names) {
        return register(key, type, texture, false, names);
    }

    public static <E extends LivingEntity> EasterEgg<E> register(ResourceLocation key, EntityType<E> type, ResourceLocation texture, boolean caseSensitive, String...names) {
        return register(key, type, texture, (entity, renderer, model) -> {
            String entityName = ChatFormatting.stripFormatting(entity.getName().getString());
            AtomicBoolean isNameCorrect = new AtomicBoolean(false);
            if (names.length == 0) {
                return true;
            } else {
                Arrays.stream(names).toList().forEach(name -> {
                    if (entityName != null) {
                        if (caseSensitive) {
                            if (entityName.equalsIgnoreCase(name)) {
                                isNameCorrect.set(true);
                            }
                        } else {
                            if (entityName.equals(name)) {
                                isNameCorrect.set(true);
                            }
                        }
                    }
                });
            }
            return isNameCorrect.get();
        });
    }

    public static <E extends LivingEntity> EasterEgg<E> register(ResourceLocation key, EntityType<E> type, ResourceLocation texture, Condition condition) {
        return Registry.register(FrozenRegistry.EASTER_EGG, key, new EasterEgg<>(type, texture, condition));
    }

    @FunctionalInterface
    public interface Condition {
        boolean condition(LivingEntity entity, LivingEntityRenderer<?, ?> renderer, EntityModel<?> model);
    }
}
