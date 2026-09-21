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
val mod_version: String by project
val subproject_prefix: String by project
val minecraft_version: String by project
val maven_group: String by project
val archives_base_name: String by project

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

val release = findProperty("releaseType") == "stable"

group = maven_group

tasks.jar {
    archiveClassifier.set("neoforge")
}

repositories {
    maven("https://maven.neoforged.net/releases") { name = "NeoForged" }
    if (!neoforgeSnapshotMaven.isNullOrBlank()) {
        maven(neoforgeSnapshotMaven) { name = "NeoForge Snapshots" }
    }
    flatDir {
        dirs("libs")
    }
}

neoforge {
    dependOn(project(":$subproject_prefix-common"))
    accessWidener(project(":$subproject_prefix-common"))
}

neoForge {
    accessTransformers {} // Required for transitive AW to apply!
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

    // Fresult
    relocApi("com.personthecat:fresult:$fresult_version")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.42")?.let { annotationProcessor(it) }

    // Kotlin for NeoForge
    //implementation("thedarkcolour:kotlinforforge-neoforge:$kotlinforforge_version")

    // Cloth Config
    implementation("me.shedaniel.cloth:cloth-config-neoforge:$cloth_config_version") {
        exclude(group = "net.neoforged")
    }

    implementation(project(":neoforge-locator"))
    "jarJar"(project(":neoforge-locator"))
}

val githubActions: Boolean = System.getenv("GITHUB_ACTIONS") == "true"
val licenseChecks: Boolean = githubActions

tasks {
    license {
        if (licenseChecks) {
            rule(rootProject.file("codeformat/QUILT_MODIFIED_HEADER"))
            rule(rootProject.file("codeformat/HEADER"))

            include("**//*.java")
            include("**//*.kt")
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
}

shadow {
    addShadowVariantIntoJavaComponent.set(false)
}

// tasks reading the `jar` archive file (whose content is actually overwritten by shadowJar,
// see above) must be ordered after shadowJar so they see the final shaded content
tasks.withType<org.gradle.api.publish.tasks.GenerateModuleMetadata>().configureEach {
    dependsOn(tasks.named("shadowJar"))
}
tasks.withType<org.gradle.api.publish.maven.tasks.AbstractPublishToMaven>().configureEach {
    dependsOn(tasks.named("shadowJar"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

val sourcesJar: Jar by tasks
val javadocJar: Jar by tasks

artifacts {
    archives(sourcesJar)
    archives(javadocJar)
}

val dev by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = true
}

tasks {
    artifacts {
        archives(jar)
        archives(sourcesJar)
        add("dev", jar)
    }
}

val changelogText = run {
    val split = rootProject.file("CHANGELOG.md").readText().split("-----------------")
    check(split.size == 2) { "Malformed changelog" }
    split[1].trim()
}

upload {
    maven {
        name.set("$mod_id-neoforge")
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
