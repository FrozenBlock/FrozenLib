import com.matthewprenger.cursegradle.CurseArtifact
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import groovy.xml.XmlSlurper
import org.codehaus.groovy.runtime.ResourceGroovyMethods
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.net.URL
import java.nio.file.Files
import java.util.Properties

buildscript {
	repositories {
		gradlePluginPortal()
	}
	dependencies {
		classpath("org.kohsuke:github-api:1.313")
	}
}

plugins {
	id("fabric-loom") version("+")
	id("io.github.juuxel.loom-quiltflower") version("+")
	id("org.ajoberstar.grgit") version("+")
	id("org.quiltmc.gradle.licenser") version("+")
	id("com.modrinth.minotaur") version("+")
	id("com.matthewprenger.cursegradle") version("+")
    `maven-publish`
    eclipse
    idea
    `java-library`
    java
}

public val minecraft_version: String by project
public val quilt_mappings: String by project
public val parchment_mappings: String by project
public val loader_version: String by project

public val mod_version: String by project
public val mod_loader: String by project
public val maven_group: String by project
public val archives_base_name: String by project

public val fabric_version: String by project
public val fabric_asm_version: String by project

public val modmenu_version: String by project
public val cloth_config_version: String by project
public val copperpipes_version: String by project
public val terrablender_version: String by project

public val sodium_version: String by project
public val iris_version: String by project
public val indium_version: String by project
public val sodium_extra_version: String by project
public val reeses_sodium_options_version: String by project
public val lithium_version: String by project
public val fastanim_version: String by project
public val ferritecore_version: String by project
public val lazydfu_version: String by project
public val starlight_version: String by project
public val entityculling_version: String by project
public val memoryleakfix_version: String by project
public val no_unused_chunks_version: String by project
public val ksyxis_version: String by project

base {
    archivesName.set(archives_base_name)
}

version = getVersion()
group = maven_group

public val release = findProperty("releaseType")?.equals("stable")

public val testmod by sourceSets.registering {
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
        defaultRefmapName.set("mixins.frozenlib.refmap.json")
    }

    accessWidenerPath.set(file("src/main/resources/frozenlib.accesswidener"))
	interfaceInjection {
		// When enabled, injected interfaces from dependecies will be applied.
		enableDependencyInterfaceInjection.set(true)
	}
}

val includeModImplementation by configurations.creating
val includeImplementation by configurations.creating

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

public val api by sourceSets.registering {
    java {
        compileClasspath += sourceSets.main.get().compileClasspath
    }
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
    maven {
        setUrl("https://maven.flashyreese.me/releases")
    }
    maven {
        setUrl("https://maven.flashyreese.me/snapshots")
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
    minecraft("com.mojang:minecraft:${minecraft_version}")
	mappings(loom.layered {
		// please annoy treetrain if this doesnt work
		mappings("org.quiltmc:quilt-mappings:${minecraft_version}+build.${quilt_mappings}:intermediary-v2")
        parchment("org.parchmentmc.data:parchment-1.19.2:${parchment_mappings}@zip")
		officialMojangMappings {
			nameSyntheticMembers = false
		}
	})
    modImplementation("net.fabricmc:fabric-loader:${loader_version}")
	testImplementation("net.fabricmc:fabric-loader-junit:${loader_version}")
    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")

    // Mod Menu
    modImplementation("com.terraformersmc:modmenu:${modmenu_version}")

    // Cloth Config
    modImplementation("me.shedaniel.cloth:cloth-config-fabric:${cloth_config_version}") {
        exclude(group = "net.fabricmc.fabric-api")
        exclude(group = "com.terraformersmc")
    }

	// TerraBlender
	modCompileOnly("curse.maven:terrablender-fabric-565956:4205731")

    "testmodImplementation"(sourceSets.main.get().output)

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
    }
}

quiltflower {
    quiltflowerVersion.set("1.9.0")
}

tasks {
    processResources {
        val properties = HashMap<String, Any>()
        properties["version"] = project.version
        properties["minecraft_version"] = minecraft_version

        properties.forEach { (a, b) -> inputs.property(a, b) }

        filesMatching("fabric.mod.json") {
            expand(properties)
        }
    }

    test {
        useJUnitPlatform()
    }

    license {
        rule(file("codeformat/QUILT_MODIFIED_HEADER"))
        rule(file("codeformat/HEADER"))

        include("**//*.java")
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

    withType(JavaCompile::class) {
        options.setEncoding("UTF-8")
        // Minecraft 1.18 (1.18-pre2) upwards uses Java 17.
        options.release.set(17)
        options.isFork = true
        options.isIncremental = true
    }

    withType(Test::class) {
        maxParallelForks = Runtime.getRuntime().availableProcessors().div(2)
    }
}

public val build: Task by tasks
public val applyLicenses: Task by tasks
public val test: Task by tasks
public val runClient: Task by tasks

public val remapJar: Task by tasks
public val sourcesJar: Task by tasks
public val javadocJar: Task by tasks

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
            rename { "${it}_${base.archivesName}" }
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

public val dev by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

tasks {
    artifacts {
        archives(remapJar)
        archives(sourcesJar)
        add("dev", jar)
    }
}

if (!(release == true || System.getenv("GITHUB_ACTIONS") == "true")) {
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

        if (publish) {
            create<MavenPublication>("mavenJava") {
                from(components["java"])

                artifact(javadocJar)

                pom {
                    groupId = publishGroup
                    artifactId = rootProject.name.trim(' ')
                    version = publishVersion
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

public val modrinth_id: String by extra
public val curseforge_id: String by extra
public val release_type: String by extra
public val curseforge_minecraft_version: String by extra
public val changelog_file: String by extra

public val modrinth_version = makeModrinthVersion(mod_version)
public val display_name = makeName(mod_version)
public val changelog_text = getChangelog(file(changelog_file))

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
        mainArtifact(file("build/libs/${tasks.remapJar.get().archiveBaseName.get()}-${version}.jar"), closureOf<CurseArtifact> {
            displayName = display_name
        })
        afterEvaluate {
            uploadTask.dependsOn(remapJar)
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
    uploadFile.set(file("build/libs/${tasks.remapJar.get().archiveBaseName.get()}-${version}.jar"))
    gameVersions.set(listOf(minecraft_version))
    loaders.set(listOf("fabric", "quilt"))
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
        val github = org.kohsuke.github.GitHub.connectUsingOAuth(token)
        val repository = github.getRepository(repoVar)

        val releaseBuilder = org.kohsuke.github.GHReleaseBuilder(repository, makeModrinthVersion(mod_version))
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
