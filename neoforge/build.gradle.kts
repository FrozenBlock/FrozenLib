import com.possible_triangle.gradle.ModVersionProperties

plugins {
    id("net.frozenblock.triangle.neoforge")
    id("com.gradleup.shadow")
    id("org.quiltmc.gradle.licenser")
    checkstyle
}

checkstyle {
    configFile = rootProject.file("checkstyle.xml")
    toolVersion = "10.20.2"
}

withKotlin()

val mod_id: String by project
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
    archivesName.set(archives_base_name)
}

group = maven_group

repositories {
    maven("https://maven.neoforged.net/releases") { name = "NeoForged" }
    if (!neoforgeSnapshotMaven.isNullOrBlank()) {
        maven(neoforgeSnapshotMaven) { name = "NeoForge Snapshots" }
    }
}

neoforge {
    dependOn(project(":flib-common"))
    accessWidener(project(":flib-common"))
}

val githubActions: Boolean = System.getenv("GITHUB_ACTIONS") == "true"
val licenseChecks: Boolean = githubActions

val applyLicenses: Task by tasks

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

val relocImplementation: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

val relocApi: Configuration by configurations.creating {
    configurations.api.get().extendsFrom(this)
}

dependencies {
    //"neoForge"("net.neoforged:neoforge:$neoforge_version")

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
    //implementation("thedarkcolour:kotlinforforge-neoforge:$kotlinforforge_version")

    // Cloth Config (NeoForge edition)
    implementation("me.shedaniel.cloth:cloth-config-neoforge:$cloth_config_version") {
        exclude(group = "net.neoforged")
    }

    "jarJar"(project(":neoforge-locator"))
}

tasks {
    processResources {
        val properties = HashMap<String, Any>()

        properties.forEach { (a, b) -> inputs.property(a, b) }

        filesMatching("META-INF/neoforge.mods.toml") {
            expand(properties)
        }
    }

    shadowJar {
        dependsOn(named("jar"))
        configurations = listOf(relocImplementation, relocApi)
        enableAutoRelocation = true
        relocationPrefix = "net.frozenblock.lib.shadow"
        archiveClassifier = ""
        archiveFileName.set("$archives_base_name-${mod.versionStrategy.get().artifactVersion(mod as ModVersionProperties)}-neoforge.jar")
        from(named("jarJar"))
        dependencies {
            exclude {
                it.moduleGroup.contains("neoforged")
            }
            exclude {
                it.moduleGroup.contains("google") || it.moduleGroup.contains("mojang")
                    || it.moduleGroup.contains("checkerframework") || it.moduleGroup.contains("slf4j")
                    || it.moduleGroup.contains("unimi") || it.moduleGroup.contains("javax")
                    || it.moduleGroup.contains("intellij") || it.moduleGroup.contains("jetbrains")
            }
        }
        exclude("META-INF/maven/**", "META-INF/proguard/**", "META-INF/LICENSE*")

        relocate("blue.endless.jankson", "net.frozenblock.lib.shadow.blue.endless.jankson")
        relocate("tools.jackson", "net.frozenblock.lib.shadow.tools.jackson")
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
}

shadow {
    addShadowVariantIntoJavaComponent.set(false)
}

// make shadowJar used when something tries to use jar
tasks.withType<GenerateModuleMetadata>().configureEach {
    dependsOn(tasks.named("shadowJar"))
}
tasks.withType<AbstractPublishToMaven>().configureEach {
    dependsOn(tasks.named("shadowJar"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

val changelogText = run {
    val split = rootProject.file("CHANGELOG.md").readText().split("-----------------")
    check(split.size == 2) { "Malformed changelog" }
    split[1].trim()
}

upload {
    maven {
        name.set("frozenlib-neoforge")
    }

    forEach {
        changelog.set(changelogText)
    }

    curseforge {
        dependencies {
            optional("cloth-config")
        }
    }

    modrinth {
        dependencies {
            optional("cloth-config")
        }
    }
}
