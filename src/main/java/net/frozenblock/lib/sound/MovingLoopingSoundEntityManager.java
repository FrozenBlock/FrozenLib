package net.frozenblock.lib.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.FrozenMain;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MovingLoopingSoundEntityManager {
    ArrayList<SoundLoopNBT> sounds = new ArrayList<>();
    public LivingEntity entity;
    public int ticksToCheck;

    public MovingLoopingSoundEntityManager(LivingEntity entity) {
        this.entity = entity;
    }

    public void load(CompoundTag nbt) {
        nbt.putInt("frozenSoundTicksToCheck", this.ticksToCheck);
        if (nbt.contains("frozenSounds", 9)) {
            this.sounds.clear();
            DataResult<List<SoundLoopNBT>> var10000 =
                    SoundLoopNBT.CODEC.listOf()
                            .parse(new Dynamic<>(NbtOps.INSTANCE,
                                    nbt.getList("frozenSounds", 10)));
            Logger var10001 = FrozenMain.LOGGER4;
            Objects.requireNonNull(var10001);
            Optional<List<SoundLoopNBT>> list =
                    var10000.resultOrPartial(var10001::error);
            if (list.isPresent()) {
                List<SoundLoopNBT> allSounds = list.get();
                this.sounds.addAll(allSounds);
            }
        }
    }

    public void save(CompoundTag nbt) {
        this.ticksToCheck = nbt.getInt("frozenSoundTicksToCheck");
        DataResult<Tag> var10000 = SoundLoopNBT.CODEC.listOf()
                .encodeStart(NbtOps.INSTANCE, this.sounds);
        Logger var10001 = FrozenMain.LOGGER4;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial(var10001::error)
                .ifPresent((cursorsNbt) -> nbt.put("frozenSounds", cursorsNbt));
    }

    public void addSound(ResourceLocation soundID, SoundSource category,
                         float volume, float pitch,
                         ResourceLocation restrictionId) {
        this.sounds.add(new SoundLoopNBT(soundID, category, volume, pitch,
                restrictionId));
    }

    public ArrayList<SoundLoopNBT> getSounds() {
        return this.sounds;
    }

    public void tick() {
        if (this.ticksToCheck > 0) {
            --this.ticksToCheck;
        } else {
            this.ticksToCheck = 20;
            ArrayList<SoundLoopNBT> soundsToRemove = new ArrayList<>();
            for (SoundLoopNBT nbt : this.getSounds()) {
                if (!FrozenSoundPredicates.getPredicate(nbt.restrictionID)
                        .test(this.entity)) {
                    soundsToRemove.add(nbt);
                }
            }
            this.sounds.removeAll(soundsToRemove);
        }
    }

    public static class SoundLoopNBT {
        public final ResourceLocation soundEventID;
        public final String categoryOrdinal;
        public final float volume;
        public final float pitch;
        public final ResourceLocation restrictionID;

        public static final Codec<SoundLoopNBT> CODEC =
                RecordCodecBuilder.create((instance) -> instance.group(
                        ResourceLocation.CODEC.fieldOf("soundEventID")
                                .forGetter(SoundLoopNBT::getSoundEventID),
                        Codec.STRING.fieldOf("categoryOrdinal")
                                .forGetter(SoundLoopNBT::getOrdinal),
                        Codec.FLOAT.fieldOf("volume")
                                .forGetter(SoundLoopNBT::getVolume),
                        Codec.FLOAT.fieldOf("pitch")
                                .forGetter(SoundLoopNBT::getPitch),
                        ResourceLocation.CODEC.fieldOf("restrictionID")
                                .forGetter(SoundLoopNBT::getRestrictionID)
                ).apply(instance, SoundLoopNBT::new));

        public SoundLoopNBT(ResourceLocation soundEventID, String ordinal,
                            float vol, float pitch,
                            ResourceLocation restrictionID) {
            this.soundEventID = soundEventID;
            this.categoryOrdinal = ordinal;
            this.volume = vol;
            this.pitch = pitch;
            this.restrictionID = restrictionID;
        }

        public SoundLoopNBT(ResourceLocation soundEventID, SoundSource category,
                            float vol, float pitch,
                            ResourceLocation restrictionID) {
            this.soundEventID = soundEventID;
            this.categoryOrdinal = category.toString();
            this.volume = vol;
            this.pitch = pitch;
            this.restrictionID = restrictionID;
        }

        public ResourceLocation getSoundEventID() {
            return this.soundEventID;
        }

        public String getOrdinal() {
            return this.categoryOrdinal;
        }

        public float getVolume() {
            return this.volume;
        }

        public float getPitch() {
            return this.pitch;
        }

        public ResourceLocation getRestrictionID() {
            return this.restrictionID;
        }
    }
}