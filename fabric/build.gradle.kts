import groovy.xml.XmlSlurper
import org.codehaus.groovy.runtime.ResourceGroovyMethods
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.net.URI
import java.nio.file.Files
import java.util.*

plugins {
    id("com.possible-triangle.fabric") version("1.4-CUSTOM-SNAPSHOT")
    id("org.ajoberstar.grgit")
    id("org.quiltmc.gradle.licenser")
    id("me.modmuss50.mod-publish-plugin")
    id("com.gradleup.shadow")
    checkstyle
}

checkstyle {
    configFile = rootProject.file("checkstyle.xml")
    toolVersion = "10.20.2"
}

withKotlin()

val minecraft_version: String by project
val fabric_loader_version: String by project
val min_fabric_loader_version: String by project

val mod_version: String by project
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

fabric {
    dependOn(project(":flib-common"))
    accessWidener(project(":flib-common"))
    dataGen {
        owner = project(":flib-common")
        splitSourceSet("datagen")
    }
}

loom {
    runtimeOnlyLog4j.set(true)

    interfaceInjection {
        enableDependencyInterfaceInjection.set(true)
    }

    runs {
        register("testmodClient") {
            client()
            configName = "Testmod Client"
            ideConfigGenerated(true)
            preferGradleTask = true
            source(testmod.get())
        }
        register("testmodServer") {
            server()
            configName = "Testmod Server"
            ideConfigGenerated(true)
            preferGradleTask = true
            source(testmod.get())
        }

        named("client") {
            name("Fabric Client")
            ideConfigGenerated(true)
            preferGradleTask = true
        }
        named("server") {
            name("Fabric Server")
            ideConfigGenerated(true)
            preferGradleTask = true
        }
    }

    accessWidenerPath = rootProject.file("common/src/main/resources/frozenlib.classtweaker")
    interfaceInjection {
        enableDependencyInterfaceInjection = true
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
    maven("https://maven.minecraftforge.net")
    maven("https://maven.parchmentmc.org")
    flatDir {
        dirs("libs")
    }
    mavenCentral()
}

val loaderAttribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)
val loaderVariants = setOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements", "includeInternal", "modCompileClasspath")
configurations.all {
    if (name in loaderVariants) {
        attributes {
            attribute(loaderAttribute, "fabric")
        }
    }
}
sourceSets.configureEach {
    listOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName).forEach { variant ->
        configurations.named(variant) {
            attributes {
                attribute(loaderAttribute, "fabric")
            }
        }
    }
}

dependencies {
    // To change the versions see the gradle.properties file
    implementation("net.fabricmc:fabric-loader:$fabric_loader_version")
    testImplementation("net.fabricmc:fabric-loader-junit:$fabric_loader_version")

    // Fabric API. This is technically optional, but you probably want it anyway.
    implementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")

    // Fabric Language Kotlin. Required to use the Kotlin language.
    implementation("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")

    // Mod Menu
    compileOnly("com.terraformersmc:modmenu:${modmenu_version}")

    // Cloth Config
    compileOnly("me.shedaniel.cloth:cloth-config-fabric:$cloth_config_version") {
        exclude(group = "net.fabricmc.fabric-api")
        exclude(group = "com.terraformersmc")
    }

    // Toml
    api("com.moandjiezana.toml:toml4j:$toml4j_version")

    // Jankson
    relocApi("blue.endless:jankson:1.2.3-mod-SNAPSHOT")

    // ExJson
    relocApi("org.exjson:xjs-data:0.14-infinity-compat-SNAPSHOT")
    relocApi("org.exjson:xjs-compat:$xjs_compat_version")
    relocApi("com.personthecat:fresult:$fresult_version")
    compileOnly("org.projectlombok:lombok:1.18.42")?.let { annotationProcessor(it) }

    "testmodImplementation"(sourceSets.main.get().output)
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    processResources {
        val properties = HashMap<String, Any>()
        properties["version"] = project.version
        properties["minecraft_version"] = "~26.2-"//minecraft_version

        properties["fabric_loader_version"] = ">=$min_fabric_loader_version"
        properties["fabric_api_version"] = ">=$fabric_api_version"
        properties["fabric_kotlin_version"] = fabric_kotlin_version

        properties.forEach { (a, b) -> inputs.property(a, b) }

        filesMatching("fabric.mod.json") {
            expand(properties)
        }
    }

    license {
        if (licenseChecks) {
            rule(rootProject.file("codeformat/QUILT_MODIFIED_HEADER"))
            rule(rootProject.file("codeformat/HEADER"))

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

    named<Jar>("sourcesJar") {
        from(sourceSets.main.get().allSource)
    }

    named<Jar>("javadocJar") {
        // configured by java { withJavadocJar() } in multiloader-common
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
            //apiVersion = KotlinVersion.KOTLIN_2_1
            //languageVersion = KotlinVersion.KOTLIN_2_1
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

val env: MutableMap<String, String> = System.getenv()

publishing {
    val mavenUrl = env["MAVEN_URL"]
    val mavenUsername = env["MAVEN_USERNAME"]
    val mavenPassword = env["MAVEN_PASSWORD"]

    //val release = mavenUrl?.contains("release")
    val snapshot = mavenUrl?.contains("snapshot")

    val publishingValid = project.name == "flib-fabric" && !mavenUrl.isNullOrEmpty() && !mavenUsername.isNullOrEmpty() && !mavenPassword.isNullOrEmpty()

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
    properties.load(FileInputStream(rootProject.file("gradle/publishing.properties")))
    properties.forEach { (a, b) ->
        project.extra[a as String] = b as String
    }
}

val modrinth_id: String by extra
val curseforge_id: String by extra
val release_type: String by extra
val changelog_file: String by extra

val modrinth_version = makeModrinthVersion(mod_version)
val display_name = makeName(mod_version)
val changelog_text = getChangelog(rootProject.file(changelog_file))

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

publishMods {
    version.set(modrinth_version)
    file.set(jar.archiveFile)
    changelog.set(changelog_text)
    type.set(STABLE)
    modLoaders.add("fabric")
    //additionalFiles.from(sourcesJar.archiveFile, javadocJar.archiveFile)

    curseforge {
        version.set(modrinth_version)
        projectId.set(curseforge_id)
        projectSlug.set("frozenlib")
        accessToken.set(providers.environmentVariable("CURSEFORGE_TOKEN"))
        minecraftVersions.add(minecraft_version)
        requires("fabric-api")
        optional("modmenu")
        optional("cloth-config")
    }
    modrinth {
        version.set(modrinth_version)
        projectId.set(modrinth_id)
        accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
        minecraftVersions.add(minecraft_version)
        requires("fabric-api")
        optional("modmenu")
        optional("cloth-config")
    }
    github {
        version.set(modrinth_version)
        repository.set("FrozenBlock/FrozenLib")
        accessToken.set(providers.environmentVariable("GITHUB_TOKEN"))
        commitish.set(getBranch())
        additionalFiles.from(sourcesJar.archiveFile.get().asFile, javadocJar.archiveFile.get().asFile)
    }
}

val publishMod by tasks.register("publishMod") {
//    dependsOn(tasks.publish)
//    dependsOn(tasks.publishMods)
}
