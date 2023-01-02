/*
 * Copyright 2023 QuiltMC
 * Copyright 2023 FrozenBlock
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

package org.quiltmc.qsl.frozenblock.worldgen.surface_rule.impl;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a {@linkplain net.minecraft.world.level.levelgen.SurfaceRules.SequenceRuleSource} that is easily reloadable when needed.
 * <p>
 * Modified to work on Fabric
 */
@ApiStatus.Internal
public class ReloadableSequenceMaterialRule implements SurfaceRules.RuleSource {
    static final KeyDispatchDataCodec<ReloadableSequenceMaterialRule> RULE_CODEC = KeyDispatchDataCodec.of(
            SurfaceRules.RuleSource.CODEC
                    .listOf()
                    .xmap(ReloadableSequenceMaterialRule::new, ReloadableSequenceMaterialRule::sequence)
                    .fieldOf("sequence")
    );

    private final List<SurfaceRules.RuleSource> sequence;

    public ReloadableSequenceMaterialRule(List<SurfaceRules.RuleSource> sequence) {
        this.sequence = new ArrayList<>(sequence);
    }

    public ReloadableSequenceMaterialRule() {
        this.sequence = new ArrayList<>();
    }

    public List<SurfaceRules.RuleSource> sequence() {
        return this.sequence;
    }

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
        return RULE_CODEC;
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        if (this.sequence.size() == 1) {
            return this.sequence.get(0).apply(context);
        } else {
            ImmutableList.Builder<SurfaceRules.SurfaceRule> builder = ImmutableList.builder();

            for (var materialRule : this.sequence) {
                builder.add(materialRule.apply(context));
            }

            return new SurfaceRules.SequenceRule(builder.build());
        }
    }
}
