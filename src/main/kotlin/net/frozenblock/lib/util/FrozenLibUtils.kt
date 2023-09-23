package net.frozenblock.lib.util

import net.minecraft.resources.ResourceLocation
import java.net.URI

fun String.uri(): URI = URI.create(this)

fun vanillaId(path: String): ResourceLocation = ResourceLocation(path)
