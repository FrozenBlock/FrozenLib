import com.possible_triangle.gradle.features.enableKotlin

plugins {
    id("net.frozenblock.triangle.common")
    id("org.quiltmc.gradle.licenser")
    checkstyle
}

checkstyle {
    configFile = rootProject.file("checkstyle.xml")
    toolVersion = "10.20.2"
}

val minecraft_version: String by project
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

common {
    accessWidener()
    enableKotlin()
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
    compileOnlyApi("org.ow2.asm:asm:${asm_version}")
    compileOnlyApi("org.ow2.asm:asm-tree:${asm_version}")
    compileOnlyApi("org.ow2.asm:asm-commons:${asm_version}")
    compileOnlyApi("org.ow2.asm:asm-util:${asm_version}")

    compileOnlyApi("net.fabricmc:sponge-mixin:0.17.3+mixin.0.8.7")
    compileOnlyApi("io.github.llamalad7:mixinextras-common:0.5.3")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.3")

    compileOnly("me.shedaniel.cloth:cloth-config:$cloth_config_version")

    // Toml
    api("com.moandjiezana.toml:toml4j:${toml4j_version}")

    compileOnlyApi("blue.endless:jankson:1.2.3-mod-SNAPSHOT")

    compileOnlyApi("org.exjson:xjs-data:0.14-infinity-compat-SNAPSHOT")
    compileOnlyApi("org.exjson:xjs-compat:$xjs_compat_version")
    compileOnlyApi("com.personthecat:fresult:$fresult_version")

    compileOnly("org.projectlombok:lombok:1.18.42")?.let { annotationProcessor(it) }
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

upload.maven {
    name.set("frozenlib-common")
}
