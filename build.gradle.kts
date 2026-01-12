import groovy.xml.XmlSlurper
import org.codehaus.groovy.runtime.ResourceGroovyMethods
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.kohsuke.github.GHReleaseBuilder
import org.kohsuke.github.GitHub
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.net.URI
import java.nio.file.Files
import java.util.*

buildscript {
	repositories {
		gradlePluginPortal()
	}
	dependencies {
		classpath("org.kohsuke:github-api:+")
	}
}

plugins {
	id("net.fabricmc.fabric-loom") version("1.14-SNAPSHOT")
	id("org.ajoberstar.grgit") version("+")
	id("org.quiltmc.gradle.licenser") version("+")
	id("com.modrinth.minotaur") version("+")
    id("com.gradleup.shadow") version("+")
    `maven-publish`
    eclipse
    idea
    `java-library`
    java
    kotlin("jvm") version("2.3.0")
    checkstyle
}

checkstyle {
    configFile = rootProject.file("checkstyle.xml")
    toolVersion = "10.20.2"
}

val minecraft_version: String by project
val loader_version: String by project
val min_loader_version: String by project

val mod_version: String by project
val mod_loader: String by project
val maven_group: String by project
val archives_base_name: String by project

val fabric_api_version: String by project
val fabric_kotlin_version: String by project
val toml4j_version: String by project
val jankson_version: String by project
val xjs_data_version: String by project
val xjs_compat_version: String by project
val fresult_version: String by project

val modmenu_version: String by project
val cloth_config_version: String by project
val copperpipes_version: String by project
val terrablender_version: String by project

val githubActions: Boolean = System.getenv("GITHUB_ACTIONS") == "true"
val licenseChecks: Boolean = githubActions

base {
    archivesName.set(archives_base_name)
}

version = getModVersion()
group = maven_group

val release = findProperty("releaseType")?.equals("stable")

val testmod by sourceSets.registering {
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
    compileClasspath += sourceSets.main.get().compileClasspath
}

val datagen by sourceSets.registering {
    compileClasspath += sourceSets.main.get().compileClasspath
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
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
            ideConfigGenerated(true)
        }
        named("server") {
            ideConfigGenerated(true)
        }
    }

    accessWidenerPath = file("src/main/resources/frozenlib.classtweaker")
	interfaceInjection {
		// When enabled, injected interfaces from dependecies will be applied.
		enableDependencyInterfaceInjection = true
	}
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/generated")
        }
    }
}

loom {
    runs {
        register("datagen") {
            client()
            name("Data Generation")
            source(datagen.get())
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/main/generated")}")
            //vmArg("-Dfabric-api.datagen.strict-validation")
            vmArg("-Dfabric-api.datagen.modid=frozenlib")

            ideConfigGenerated(true)
            runDir = "build/datagen"
        }

        named("client") {
            ideConfigGenerated(true)
        }
        named("server") {
            ideConfigGenerated(true)
        }
    }
}

val includeImplementation: Configuration by configurations.creating

configurations {
    include {
        extendsFrom(includeImplementation)
    }
    implementation {
        extendsFrom(includeImplementation)
    }
}

val api by sourceSets.registering {
    java {
        compileClasspath += sourceSets.main.get().compileClasspath
    }
}

val relocImplementation: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

val relocApi: Configuration by configurations.creating {
    configurations.api.get().extendsFrom(this)
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
    maven("https://api.modrinth.com/maven") {
        name = "Modrinth"

        content {
            includeGroup("maven.modrinth")
        }
    }
    maven("https://maven.terraformersmc.com") {
        content {
            includeGroup("com.terraformersmc")
        }
    }
    maven("https://maven.shedaniel.me/")
    maven("https://maven.minecraftforge.net")
    maven("https://maven.parchmentmc.org")
    maven("https://maven.quiltmc.org/repository/release") {
        name = "Quilt"
    }
    maven("https://maven.frozenblock.net/release") {
        name = "FrozenBlock"
    }

    flatDir {
        dirs("libs")
    }
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:$minecraft_version")
    implementation("net.fabricmc:fabric-loader:$loader_version")
	testImplementation("net.fabricmc:fabric-loader-junit:$loader_version")

    // Fabric API. This is technically optional, but you probably want it anyway.
    implementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")

    // Fabric Language Kotlin. Required to use the Kotlin language.
    implementation("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")

    // Mod Menu
    api("com.terraformersmc:modmenu:${modmenu_version}")

    // Cloth Config
    api("me.shedaniel.cloth:cloth-config-fabric:$cloth_config_version") {
        exclude(group = "net.fabricmc.fabric-api")
        exclude(group = "com.terraformersmc")
    }

	// TerraBlender
    //compileOnlyApi("com.github.glitchfiend:TerraBlender-fabric:${terrablender_version}")

    // Toml
    api("com.moandjiezana.toml:toml4j:$toml4j_version")//?.let { include(it) }

    // Jankson
    relocApi("blue.endless:jankson:1.2.3-mod-SNAPSHOT")

    // ExJson
    //relocModApi("org.exjson:xjs-data:$xjs_data_version")
    relocApi("org.exjson:xjs-data:0.14-infinity-compat-SNAPSHOT")
    relocApi("org.exjson:xjs-compat:$xjs_compat_version")
    relocApi("com.personthecat:fresult:$fresult_version")
    compileOnly("org.projectlombok:lombok:1.18.42")?.let { annotationProcessor(it) }

    "testmodImplementation"(sourceSets.main.get().output)
}

tasks {
    processResources {
        val properties = HashMap<String, Any>()
        properties["version"] = project.version
        properties["minecraft_version"] = "~26.1-"//minecraft_version

        properties["fabric_loader_version"] = ">=$min_loader_version"
        properties["fabric_api_version"] = ">=$fabric_api_version"
        properties["fabric_kotlin_version"] = fabric_kotlin_version

        properties.forEach { (a, b) -> inputs.property(a, b) }

        filesMatching("fabric.mod.json") {
            expand(properties)
        }
    }

    license {
        if (licenseChecks) {
            rule(file("codeformat/QUILT_MODIFIED_HEADER"))
            rule(file("codeformat/HEADER"))

            include("**//*.java")
            include("**//*.kt")
        }
    }

    test {
        useJUnitPlatform()
    }

    shadowJar {
        configurations = listOf(relocImplementation, relocApi)
        enableAutoRelocation = true
        relocationPrefix = "net.frozenblock.lib.shadow"
        archiveClassifier = ""
        dependencies {
            exclude {
                it.moduleGroup.contains("fabric")
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

    register("javadocJar", Jar::class) {
        dependsOn(javadoc)
        archiveClassifier = "javadoc"
        from(javadoc.get().destinationDir!!)
    }

    register("sourcesJar", Jar::class) {
        dependsOn(classes)
        archiveClassifier = "sources"
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
            apiVersion = KotlinVersion.KOTLIN_2_1
            languageVersion = KotlinVersion.KOTLIN_2_1
        }
    }

    withType(Test::class) {
        maxParallelForks = Runtime.getRuntime().availableProcessors().div(2)
    }
}

val build: Task by tasks
val applyLicenses: Task by tasks
val test: Task by tasks
val runClient: Task by tasks

val jar: Jar by tasks
val sourcesJar: Jar by tasks
val javadocJar: Jar by tasks

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25

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

fun getModVersion(): String {
    var version = "$mod_version-mc$minecraft_version"

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
        archives(jar)
        archives(sourcesJar)
        add("dev", jar)
    }
}

val env: MutableMap<String, String> = System.getenv()

publishing {
    val mavenUrl = env["MAVEN_URL"]
    val mavenUsername = env["MAVEN_USERNAME"]
    val mavenPassword = env["MAVEN_PASSWORD"]

    //val release = mavenUrl?.contains("release")
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
                    val xml = ResourceGroovyMethods.getText(
                        URI.create("$mavenUrl/${publishGroup.replace('.', '/')}/$snapshotPublishVersion/$publishVersion.pom").toURL()
                    )
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
val release_type: String by extra
val changelog_file: String by extra

val modrinth_version = makeModrinthVersion(mod_version)
val display_name = makeName(mod_version)
val changelog_text = getChangelog(file(changelog_file))

fun makeName(version: String): String {
    return "$version (${minecraft_version})"
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

modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    projectId = modrinth_id
    versionNumber = modrinth_version
    versionName = display_name
    versionType = release_type
    changelog = changelog_text
    uploadFile = tasks.jar.get()
    gameVersions = listOf(minecraft_version)
    loaders = listOf("fabric", "quilt")
    /*
    additionalFiles = listOf(
        tasks.remapSourcesJar.get(),
        javadocJar
    )
     */

    dependencies {
        required.project("fabric-api")
        optional.project("cloth-config")
        optional.project("modmenu")
    }
}

val github by tasks.register("github") {
    dependsOn(tasks.jar)
    dependsOn(sourcesJar)
    dependsOn(javadocJar)

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
        ghRelease.uploadAsset(tasks.jar.get().archiveFile.get().asFile, "application/java-archive")
        ghRelease.uploadAsset(sourcesJar.archiveFile.get().asFile, "application/java-archive")
        ghRelease.uploadAsset(javadocJar.outputs.files.singleFile, "application/java-archive")
    }
}

val publishMod by tasks.register("publishMod") {
    dependsOn(tasks.publish)
    dependsOn(github)
    dependsOn(tasks.modrinth)
}
