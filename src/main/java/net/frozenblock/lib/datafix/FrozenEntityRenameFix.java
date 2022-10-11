package net.frozenblock.lib.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.Objects;
import java.util.function.Function;

public abstract class FrozenEntityRenameFix extends DataFix {

    private final String name;

    public FrozenEntityRenameFix(Schema outputSchema, String name) {
        super(outputSchema, false);
        this.name = name;
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<Pair<String, String>> type =
                DSL.named(References.ENTITY_NAME.typeName(),
                        NamespacedSchema.namespacedString());
        if (!Objects.equals(
                this.getInputSchema().getType(References.ENTITY_NAME), type)) {
            throw new IllegalStateException(
                    "entity name type is not what was expected.");
        } else {
            return this.fixTypeEverywhere(this.name, type,
                    dynamicOps -> pair -> pair.mapSecond(this::fixEntity));
        }
    }

    protected abstract String fixEntity(String string);

    public static DataFix create(Schema outputSchema, String name,
                                 Function<String, String> function) {
        return new FrozenEntityRenameFix(outputSchema, name) {
            @Override
            protected String fixEntity(String string) {
                return function.apply(string);
            }
        };
    }
}
