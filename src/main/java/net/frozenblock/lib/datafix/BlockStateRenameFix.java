package net.frozenblock.lib.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.datafix.fixes.References;

public class BlockStateRenameFix extends DataFix {

    private final String name;
    private final String blockId;
    private final String oldState;
    private final String defaultValue;
    private final String newState;

    public BlockStateRenameFix(Schema outputSchema, String name, String blockId, String oldState, String defaultValue, String newState) {
        super(outputSchema, false);
        this.name = name;
        this.blockId = blockId;
        this.oldState = oldState;
        this.defaultValue = defaultValue;
        this.newState = newState;
    }

    private Dynamic<?> fix(Dynamic<?> dynamic) {
        Optional<String> optional = dynamic.get("Name").asString().result();
        return optional.equals(Optional.of(this.blockId)) ? dynamic.update("Properties", dynamicx -> {
            String string = dynamicx.get(this.oldState).asString(this.defaultValue);
            return dynamicx.remove(this.oldState).set(this.newState, dynamicx.createString(string));
        }) : dynamic;
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped(
                this.name, this.getInputSchema().getType(References.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), this::fix)
        );
    }
}
