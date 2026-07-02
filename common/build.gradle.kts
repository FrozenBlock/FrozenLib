plugins {
    id("flib-multiloader-common")
    id("dev.architectury.loom-no-remap")
    id("org.quiltmc.gradle.licenser")
    kotlin("jvm")
}

val minecraft_version: String by project
val loader_version: String by project
val asm_version: String by project

val cloth_config_version: String by project

val toml4j_version: String by project
val jankson_version: String by project
val xjs_data_version: String by project
val xjs_compat_version: String by project
val fresult_version: String by project

val githubActions: Boolean = System.getenv("GITHUB_ACTIONS") == "true"
val licenseChecks: Boolean = githubActions

val applyLicenses: Task by tasks

loom {
    accessWidenerPath = file("src/main/resources/frozenlib.classtweaker")
}

tasks {
    license {
        if (licenseChecks) {
            rule(rootProject.file("codeformat/QUILT_MODIFIED_HEADER"))
            rule(rootProject.file("codeformat/HEADER"))

            include("**//*.java")
            include("**//*.kt")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")

    // only for @Environment
    api("net.fabricmc:fabric-loader:${loader_version}")

    compileOnlyApi("org.ow2.asm:asm:${asm_version}")
    compileOnlyApi("org.ow2.asm:asm-tree:${asm_version}")
    compileOnlyApi("org.ow2.asm:asm-commons:${asm_version}")
    compileOnlyApi("org.ow2.asm:asm-util:${asm_version}")

    compileOnlyApi("org.spongepowered:mixin:0.8.5")
    compileOnlyApi("io.github.llamalad7:mixinextras-common:0.5.3")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.3")

    compileOnly("me.shedaniel.cloth:cloth-config:$cloth_config_version")

    // Toml
    api("com.moandjiezana.toml:toml4j:${toml4j_version}")

    compileOnlyApi("blue.endless:jankson:1.2.3-mod-SNAPSHOT")

    compileOnly("org.exjson:xjs-data:0.14-infinity-compat-SNAPSHOT")
    compileOnly("org.exjson:xjs-compat:$xjs_compat_version")
    compileOnly("com.personthecat:fresult:$fresult_version")

    compileOnly("org.projectlombok:lombok:1.18.42")?.let { annotationProcessor(it) }
}

sourceSets {
    main {
        resources {
            srcDir("src/main/generated")
        }
    }
}

val mergeCommonResources by tasks.registering(Sync::class) {
    from(sourceSets.main.get().resources.srcDirs)
    into(layout.buildDirectory.dir("merged-resources"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

configurations {
    create("commonJava") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    create("commonResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

artifacts {
    add("commonJava", sourceSets.main.get().java.sourceDirectories.singleFile)
    add("commonResources", mergeCommonResources.map { it.destinationDir }) {
        builtBy(mergeCommonResources)
    }
}

val loaderAttribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)
listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { variant ->
    configurations.named(variant) {
        attributes {
            attribute(loaderAttribute, "common")
        }
    }
}
sourceSets.configureEach {
    listOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName).forEach { variant ->
        configurations.named(variant) {
            attributes {
                attribute(loaderAttribute, "common")
            }
        }
    }
}
