/*
 * Copyright 2023 FrozenBlock
 * This file is part of FrozenLib.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
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
