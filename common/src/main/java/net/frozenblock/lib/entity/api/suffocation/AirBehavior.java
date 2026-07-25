package net.frozenblock.lib.entity.api.suffocation;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum AirBehavior implements StringRepresentable {
	NONE("none"),
	DISPLAY_ONLY("display_only"),
	DRAIN("drain");
	public static final Codec<AirBehavior> CODEC = StringRepresentable.fromEnum(AirBehavior::values);
	private final String name;

	AirBehavior(String name) {
		this.name = name;
	}

	public boolean usesVanillaAir() {
		return this != NONE;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}
