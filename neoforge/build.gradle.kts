import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("multiloader-loader")
    id("net.neoforged.moddev")
    id("com.gradleup.shadow")
    kotlin("jvm")
    checkstyle
}

checkstyle {
    configFile = rootProject.file("checkstyle.xml")
    toolVersion = "10.20.2"
}

val mod_id: String by project
val mod_version: String by project
val minecraft_version: String by project
val maven_group: String by project
val archives_base_name: String by project

val neoforge_version: String by project
val neoforge_loader_version_range: String by project

val toml4j_version: String by project
val jankson_version: String by project
val xjs_data_version: String by project
val xjs_compat_version: String by project
val fresult_version: String by project

val cloth_config_version: String by project
val kotlinforforge_version: String by project

val neoforgeSnapshotMaven = findProperty("neoforge_snapshot_maven") as String?

base {
    archivesName.set("$archives_base_name-neoforge")
}

group = maven_group

if (!neoforgeSnapshotMaven.isNullOrBlank()) {
    repositories {
        maven(neoforgeSnapshotMaven) { name = "NeoForge Snapshots" }
    }
}

repositories {
    maven("https://maven.shedaniel.me/")
}

neoForge {
    version = neoforge_version
    val at = rootProject.file("common/src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
    runs {
        configureEach {
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
            ideName = "NeoForge ${name.replaceFirstChar { it.uppercase() }} (${project.path})"
        }
        create("client") {
            client()
            gameDirectory.set(project.mkdir(project.file("runs/client")))
        }
        create("server") {
            server()
            project.file("runs/server").parentFile?.mkdirs()
            gameDirectory.set(project.mkdir(project.file("runs/server")))
        }
    }
    mods {
        create(mod_id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

val loaderAttribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)
val loaderVariants = setOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements")
configurations.all {
    if (name in loaderVariants) {
        attributes {
            attribute(loaderAttribute, "neoforge")
        }
    }
}
sourceSets.configureEach {
    listOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName).forEach { variant ->
        configurations.named(variant) {
            attributes {
                attribute(loaderAttribute, "neoforge")
            }
        }
    }
}

val relocImplementation: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

val relocApi: Configuration by configurations.creating {
    configurations.api.get().extendsFrom(this)
}

dependencies {
    // Toml
    api("com.moandjiezana.toml:toml4j:$toml4j_version")

    // Jankson
    relocApi("blue.endless:jankson:1.2.3-mod-SNAPSHOT")

    // ExJson
    relocApi("org.exjson:xjs-data:0.14-infinity-compat-SNAPSHOT")
    relocApi("org.exjson:xjs-compat:$xjs_compat_version")
    relocApi("com.personthecat:fresult:$fresult_version")
    compileOnly("org.projectlombok:lombok:1.18.42")?.let { annotationProcessor(it) }

    // Kotlin for NeoForge
    implementation("thedarkcolour:kotlinforforge-neoforge:$kotlinforforge_version")

    // Cloth Config (NeoForge edition)
    compileOnly("me.shedaniel.cloth:cloth-config-neoforge:$cloth_config_version") {
        exclude(group = "net.neoforged")
    }
}

tasks {
    shadowJar {
        configurations = listOf(relocImplementation, relocApi)
        enableAutoRelocation = true
        relocationPrefix = "net.frozenblock.lib.shadow"
        archiveClassifier = ""
        dependencies {
            exclude {
                it.moduleGroup.contains("neoforged")
            }
            exclude("META-INF/maven/**", "META-INF/proguard/**", "META-INF/LICENSE*")
            exclude {
                it.moduleGroup.contains("google") || it.moduleGroup.contains("mojang")
                    || it.moduleGroup.contains("checkerframework") || it.moduleGroup.contains("slf4j")
                    || it.moduleGroup.contains("unimi") || it.moduleGroup.contains("javax")
                    || it.moduleGroup.contains("intellij") || it.moduleGroup.contains("jetbrains")
            }
        }
        relocate("blue.endless.jankson", "net.frozenblock.lib.shadow.blue.endless.jankson")
    }

    named<Jar>("sourcesJar") {
        from(sourceSets.main.get().allSource)
    }

    withType(JavaCompile::class) {
        options.encoding = "UTF-8"
        options.release = 25
        options.isFork = true
        options.isIncremental = true
    }

    withType(KotlinCompile::class) {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_25
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}
