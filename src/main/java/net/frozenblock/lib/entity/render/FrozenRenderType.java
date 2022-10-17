package net.frozenblock.lib.entity.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.BiFunction;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class FrozenRenderType {

    public static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE_FIXED = Util.memoize(
            ((identifier, affectsOutline) -> {
                RenderType.CompositeState multiPhaseParameters = RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(identifier, false, false))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                        .setOverlayState(RenderStateShard.OVERLAY)
                        .createCompositeState(affectsOutline);
                return create(
                        "entity_translucent_emissive_fixed_frozenlib",
                        DefaultVertexFormat.NEW_ENTITY,
                        VertexFormat.Mode.QUADS,
                        256,
                        true,
                        true,
                        multiPhaseParameters
                );
            })
    );

    public static RenderType entityTranslucentEmissiveFixed(ResourceLocation resourceLocation) {
        return ENTITY_TRANSLUCENT_EMISSIVE_FIXED.apply(resourceLocation, true);
    }

    /*@Nullable
    public static ShaderInstance renderTypeTranslucentCutoutShader;

    public static final RenderStateShard.ShaderStateShard RENDERTYPE_TRANSLUCENT_CUTOUT_SHADER = new RenderStateShard.ShaderStateShard(
            WilderWildClient::getRenderTypeTranslucentCutoutShader
    );

    @Nullable
    public static ShaderInstance getRenderTypeTranslucentCutoutShader() {
        return renderTypeTranslucentCutoutShader;
    }

    public static final RenderType TRANSLUCENT_CUTOUT = create(
            "translucent_cutout_wilderwild", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, true, RenderType.translucentState(RENDERTYPE_TRANSLUCENT_CUTOUT_SHADER)
    );

    public static RenderType translucentCutout() {
        return TRANSLUCENT_CUTOUT;
    }*/

    public static RenderType.CompositeRenderType create(
            String name,
            VertexFormat vertexFormat,
            VertexFormat.Mode drawMode,
            int expectedBufferSize,
            boolean hasCrumbling,
            boolean translucent,
            RenderType.CompositeState phases
    ) {
        return new RenderType.CompositeRenderType(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, phases);
    }
}
