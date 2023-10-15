import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import groovy.xml.XmlSlurper
import org.codehaus.groovy.runtime.ResourceGroovyMethods
import org.kohsuke.github.GHReleaseBuilder
import org.kohsuke.github.GitHub
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.net.URL
import java.nio.file.Files
import java.util.*

buildscript {
	repositories {
		gradlePluginPortal()
	}
	dependencies {
		classpath("org.kohsuke:github-api:1.316")
	}
}

plugins {
	id("fabric-loom") version("+")
	id("org.ajoberstar.grgit") version("+")
	id("org.quiltmc.gradle.licenser") version("+")
	id("com.modrinth.minotaur") version("+")
	id("com.matthewprenger.cursegradle") version("+")
    id("com.github.johnrengelman.shadow") version("+")
    `maven-publish`
    eclipse
    idea
    `java-library`
    java
    kotlin("jvm") version("1.9.10")
}

val minecraft_version: String by project
val quilt_mappings: String by project
val parchment_mappings: String by project
val loader_version: String by project

val mod_version: String by project
val mod_loader: String by project
val maven_group: String by project
val archives_base_name: String by project

val fabric_version: String by project
val fabric_kotlin_version: String by project
val mixin_extras_version: String by project
val fabric_asm_version: String by project
val toml4j_version: String by project
val jankson_version: String by project

val modmenu_version: String by project
val cloth_config_version: String by project
val copperpipes_version: String by project
val terrablender_version: String by project

val sodium_version: String by project
val iris_version: String by project
val indium_version: String by project
val sodium_extra_version: String by project
val reeses_sodium_options_version: String by project
val lithium_version: String by project
val fastanim_version: String by project
val ferritecore_version: String by project
val lazydfu_version: String by project
val starlight_version: String by project
val entityculling_version: String by project
val memoryleakfix_version: String by project
val no_unused_chunks_version: String by project
val ksyxis_version: String by project

val githubActions: Boolean = System.getenv("GITHUB_ACTIONS") == "true"

base {
    archivesName.set(archives_base_name)
}

version = getVersion()
group = maven_group

val release = findProperty("releaseType")?.equals("stable")

val testmod by sourceSets.registering {
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    compileClasspath += sourceSets.main.get().compileClasspath
}

loom {
    runtimeOnlyLog4j.set(true)

    runs {
        register("testmodClient") {
            client()
            ideConfigGenerated(project.rootProject == project)
            name("Testmod Client")
            source(testmod.get())
        }
        register("testmodServer") {
            server()
            ideConfigGenerated(project.rootProject == project)
            name("Testmod Server")
            source(testmod.get())
        }

        named("client") {
            ideConfigGenerated(false)
        }
        named("server") {
            ideConfigGenerated(false)
        }
    }

    mixin {
        defaultRefmapName = "mixins.frozenlib.refmap.json"
    }

    accessWidenerPath = file("src/main/resources/frozenlib.accesswidener")
	interfaceInjection {
		// When enabled, injected interfaces from dependecies will be applied.
		enableDependencyInterfaceInjection = true
	}
}

val includeModImplementation: Configuration by configurations.creating
val includeImplementation: Configuration by configurations.creating

configurations {
    include {
        extendsFrom(includeImplementation)
        extendsFrom(includeModImplementation)
    }
    implementation {
        extendsFrom(includeImplementation)
    }
    modImplementation {
        extendsFrom(includeModImplementation)
    }
}

val api by sourceSets.registering {
    java {
        compileClasspath += sourceSets.main.get().compileClasspath
    }
}

val relocModApi: Configuration by configurations.creating {
    configurations.modApi.get().extendsFrom(this)
}

sourceSets {
    main {
        java {
            compileClasspath += api.get().output
            runtimeClasspath += api.get().output
        }
    }
}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    maven {
        setUrl("https://jitpack.io")
    }
    maven {
        setName("Modrinth")
        setUrl("https://api.modrinth.com/maven")

        content {
            includeGroup("maven.modrinth")
        }
    }
    maven {
        setUrl("https://maven.terraformersmc.com")

        content {
            includeGroup("com.terraformersmc")
        }
    }
    maven {
        setUrl("https://maven.shedaniel.me/")
    }
    maven {
        setUrl("https://cursemaven.com")

        content {
            includeGroup("curse.maven")
        }
    }
    /*maven {
        setName("Siphalor"s Maven")
        setUrl("https://maven.siphalor.de")
    }*/
    /*maven {
        setUrl("https://maven.flashyreese.me/releases")
    }
    maven {
        setUrl("https://maven.flashyreese.me/snapshots")
    }*/
    maven {
        setUrl("https://maven.minecraftforge.net")
    }
    maven {
        setUrl("https://maven.parchmentmc.org")
    }
    maven {
        setName("Quilt")
        setUrl("https://maven.quiltmc.org/repository/release")
    }

    flatDir {
        dirs("libs")
    }
    mavenCentral()
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:$minecraft_version")
	mappings(loom.layered {
		// please annoy treetrain if this doesnt work
		mappings("org.quiltmc:quilt-mappings:$quilt_mappings:intermediary-v2")
        parchment("org.parchmentmc.data:parchment-$parchment_mappings@zip")
		officialMojangMappings {
			nameSyntheticMembers = false
		}
	})
    modImplementation("net.fabricmc:fabric-loader:$loader_version")
	testImplementation("net.fabricmc:fabric-loader-junit:$loader_version")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")

    // Fabric Language Kotlin. Required to use the Kotlin language.
    modImplementation("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")

    // Mod Menu
    modCompileOnlyApi("com.terraformersmc:modmenu:${modmenu_version}")

    // Cloth Config
    modCompileOnlyApi("me.shedaniel.cloth:cloth-config-fabric:${cloth_config_version}") {
        exclude(group = "net.fabricmc.fabric-api")
        exclude(group = "com.terraformersmc")
    }

	// TerraBlender
    modCompileOnlyApi("com.github.glitchfiend:TerraBlender-fabric:${terrablender_version}")

    // MixinExtras
    modApi("com.github.llamalad7.mixinextras:mixinextras-fabric:$mixin_extras_version")?.let { annotationProcessor(it); include(it) }

    // Toml
    modApi("com.moandjiezana.toml:toml4j:$toml4j_version")//?.let { include(it) }

    // Jankson
    relocModApi("blue.endless:jankson:$jankson_version")

    "testmodImplementation"(sourceSets.main.get().output)

/*
    // only affects runClient, does not affect gradlew build. add -PuseThirdPartyMods=false to not use these
    if (findProperty("useThirdPartyMods") != "false") {
        modRuntimeOnly("maven.modrinth:ferrite-core:${ferritecore_version}")
        modRuntimeOnly("maven.modrinth:lazydfu:${lazydfu_version}")
        //modRuntimeOnly("maven.modrinth:starlight:${starlight_version}")
        modRuntimeOnly("maven.modrinth:lithium:${lithium_version}")

        // Sodium Related

        /*modRuntimeOnly "maven.modrinth:iris:${iris_version}"
        modRuntimeOnly "maven.modrinth:indium:${indium_version}"
        modRuntimeOnly("me.flashyreese.mods:reeses-sodium-options:${reeses_sodium_options_version}") {
            exclude group: "net.coderbot.iris_mc1_19", module: "iris"
        }
        modRuntimeOnly "me.flashyreese.mods:sodium-extra-fabric:${sodium_extra_version}"
        modRuntimeOnly "io.github.douira:glsl-transformer:0.27.0"
        modRuntimeOnly "net.caffeinemc:mixin-config:1.0.0+1.17"*/

        modRuntimeOnly("maven.modrinth:entityculling:${entityculling_version}")
        //modRuntimeOnly("maven.modrinth:c2me-fabric:0.2.0+alpha.8.32+1.19.1-rc3")
        modRuntimeOnly("maven.modrinth:ksyxis:${ksyxis_version}")
        //modRuntimeOnly("maven.modrinth:iris:1.19.x-v1.2.5")
        modRuntimeOnly("maven.modrinth:memoryleakfix:${memoryleakfix_version}")
        modRuntimeOnly("maven.modrinth:no-unused-chunks:${no_unused_chunks_version}")
    }*/
}

tasks {
    processResources {
        val properties = HashMap<String, Any>()
        properties["version"] = project.version
        properties["minecraft_version"] = "~$minecraft_version-"
        properties["fabric_kotlin_version"] = fabric_kotlin_version

        properties.forEach { (a, b) -> inputs.property(a, b) }

        filesMatching("fabric.mod.json") {
            expand(properties)
        }
    }

    test {
        useJUnitPlatform()

        license {
            rule(file("codeformat/QUILT_MODIFIED_HEADER"))
            rule(file("codeformat/HEADER"))

            include("**//*.java")
            include("**//*.kt")
        }
    }

    shadowJar {
        configurations = listOf(relocModApi)
        isEnableRelocation = true
        relocationPrefix = "net.frozenblock.lib.shadow"

        //relocate("blue.endless.jankson", "net.frozenblock.lib.config.api.jankson")
    }

    register("javadocJar", Jar::class) {
        dependsOn(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc.get().destinationDir)
    }

    register("sourcesJar", Jar::class) {
        dependsOn(classes)
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    remapJar {
        dependsOn(shadowJar)
        input.set(shadowJar.get().archiveFile)
    }

    withType(JavaCompile::class) {
        options.encoding = "UTF-8"
        options.release.set(17)
        options.isFork = true
        options.isIncremental = true
    }

    withType(Test::class) {
        maxParallelForks = Runtime.getRuntime().availableProcessors().div(2)
    }
}

val build: Task by tasks
val applyLicenses: Task by tasks
val test: Task by tasks
val runClient: Task by tasks

val remapJar: Task by tasks
val sourcesJar: Task by tasks
val javadocJar: Task by tasks

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

tasks {
    jar {
        from("LICENSE") {
            rename { "${it}_${base.archivesName.get()}" }
        }
    }
}

artifacts {
    archives(sourcesJar)
    archives(javadocJar)
}

fun getVersion(): String {
    var version = "$mod_version-$mod_loader+$minecraft_version"

    if (release != null && !release) {
        version += "-unstable"
    }

    return version
}

val dev by configurations.creating {
    isCanBeResolved = true // maybe do false? idk?
    isCanBeConsumed = true
}

tasks {
    artifacts {
        archives(remapJar)
        archives(sourcesJar)
        add("dev", jar)
    }
}

if (!(release == true || githubActions)) {
	build.dependsOn(applyLicenses)
}

val env = System.getenv()

publishing {
    val mavenUrl = env["MAVEN_URL"]
    val mavenUsername = env["MAVEN_USERNAME"]
    val mavenPassword = env["MAVEN_PASSWORD"]

    val release = mavenUrl?.contains("release")
    val snapshot = mavenUrl?.contains("snapshot")

    val publishingValid = rootProject == project && !mavenUrl.isNullOrEmpty() && !mavenUsername.isNullOrEmpty() && !mavenPassword.isNullOrEmpty()

    val publishVersion = makeModrinthVersion(mod_version)
    val snapshotPublishVersion = publishVersion + if (snapshot == true) "-SNAPSHOT" else ""

    val publishGroup = rootProject.group.toString().trim(' ')

    val hash = if (grgit.branch != null && grgit.branch.current() != null) grgit.branch.current().fullName else ""

    publications {
        var publish = true
        try {
            if (publishingValid) {
                try {
                    val xml = ResourceGroovyMethods.getText(URL("$mavenUrl/${publishGroup.replace('.', '/')}/$snapshotPublishVersion/$publishVersion.pom"))
                    val metadata = XmlSlurper().parseText(xml)

                    if (metadata.getProperty("hash").equals(hash)) {
                        publish = false
                    }
                } catch (ignored: FileNotFoundException) {
                    // No existing version was published, so we can publish
                }
            } else {
                publish = false
            }
        } catch (e: Exception) {
            publish = false
            println("Unable to publish to maven. The maven server may be offline.")
        }

        if (publish) {
            create<MavenPublication>("mavenJava") {
                from(components["java"])

                artifact(javadocJar)

                pom {
                    groupId = publishGroup
                    artifactId = rootProject.base.archivesName.get().lowercase()
                    version = snapshotPublishVersion
                    withXml {
                        asNode().appendNode("properties").appendNode("hash", hash)
                    }
                }
            }
        }
    }
    repositories {

        if (publishingValid) {
            maven {
                url = uri(mavenUrl!!)

                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        } else {
            mavenLocal()
        }
    }
}

extra {
    val properties = Properties()
    properties.load(FileInputStream(file("gradle/publishing.properties")))
    properties.forEach { (a, b) ->
        project.extra[a as String] = b as String
    }
}

val modrinth_id: String by extra
val curseforge_id: String by extra
val release_type: String by extra
val curseforge_minecraft_version: String by extra
val changelog_file: String by extra

val modrinth_version = makeModrinthVersion(mod_version)
val display_name = makeName(mod_version)
val changelog_text = getChangelog(file(changelog_file))

fun makeName(version: String): String {
    return "${version} (${minecraft_version})"
}

fun makeModrinthVersion(version: String): String {
    return "$version-mc${minecraft_version}"
}

fun getChangelog(changelogFile: File): String {
    val text = Files.readString(changelogFile.toPath())
    val split = text.split("-----------------")
    if (split.size != 2)
        throw IllegalStateException("Malformed changelog")
    return split[1].trim()
}

fun getBranch(): String {
    val env = System.getenv()
    var branch = env["GITHUB_REF"]
    if (branch != null && branch != "") {
        return branch.substring(branch.lastIndexOf("/") + 1)
    }

    if (grgit == null) {
        return "unknown"
    }

    branch = grgit.branch.current().name
    return branch.substring(branch.lastIndexOf("/") + 1)
}

curseforge {
    val token = System.getenv("CURSEFORGE_TOKEN")
    apiKey = if (token == null || token.isEmpty()) "unset" else token
    val gameVersion = if (curseforge_minecraft_version != "null") curseforge_minecraft_version else minecraft_version
    project(closureOf<CurseProject> {
        id = curseforge_id
        changelog = changelog_text
        releaseType = release_type
        addGameVersion("Fabric")
        addGameVersion("Quilt")
        addGameVersion(gameVersion)
        relations(closureOf<CurseRelation> {
            requiredDependency("fabric-api")
            optionalDependency("cloth-config")
        })
        mainArtifact(remapJar, closureOf<CurseArtifact> {
            displayName = display_name
        })
        addArtifact(tasks.remapSourcesJar.get())
        addArtifact(javadocJar)

        afterEvaluate {
            uploadTask.dependsOn(remapJar)
            uploadTask.dependsOn(tasks.remapSourcesJar.get())
            uploadTask.dependsOn(javadocJar)
        }
    })
    curseGradleOptions.forgeGradleIntegration = false
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set(modrinth_id)
    versionNumber.set(modrinth_version)
    versionName.set(display_name)
    versionType.set(release_type)
    changelog.set(changelog_text)
    uploadFile.set(remapJar)
    gameVersions.set(listOf(minecraft_version))
    loaders.set(listOf("fabric", "quilt"))
    additionalFiles.set(
        listOf(
            tasks.remapSourcesJar.get(),
            javadocJar
        )
    )
    dependencies {
        required.project("fabric-api")
        optional.project("cloth-config")
    }
}

val github by tasks.register("github") {
    dependsOn(remapJar)
    val env = System.getenv()
    val token = env["GITHUB_TOKEN"]
    val repoVar = env["GITHUB_REPOSITORY"]
    onlyIf {
        token != null && token != ""
    }

    doLast {
        val github = GitHub.connectUsingOAuth(token)
        val repository = github.getRepository(repoVar)

        val releaseBuilder = GHReleaseBuilder(repository, makeModrinthVersion(mod_version))
        releaseBuilder.name(makeName(mod_version))
        releaseBuilder.body(changelog_text)
        releaseBuilder.commitish(getBranch())
        releaseBuilder.prerelease(release_type != "release")

        val ghRelease = releaseBuilder.create()
        ghRelease.uploadAsset(tasks.remapJar.get().archiveFile.get().asFile, "application/java-archive")
        ghRelease.uploadAsset(tasks.remapSourcesJar.get().archiveFile.get().asFile, "application/java-archive")
        ghRelease.uploadAsset(javadocJar.outputs.files.singleFile, "application/java-archive")
    }
}

val publishMod by tasks.register("publishMod") {
    dependsOn(tasks.publish)
    dependsOn(github)
    dependsOn(tasks.curseforge)
    dependsOn(tasks.modrinth)
}
