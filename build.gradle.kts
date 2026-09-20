import com.possible_triangle.gradle.ModVersionProperties
import com.possible_triangle.gradle.upload.FrozenBlockVersionStrategy
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.kohsuke.github.GHReleaseBuilder
import org.kohsuke.github.GitHub

plugins {
    id("net.frozenblock.triangle.core") version("+")
    id("net.frozenblock.triangle.common") version("+") apply(false)
    id("net.frozenblock.triangle.fabric") version("+") apply(false)
    id("net.frozenblock.triangle.neoforge") version("+") apply(false)
    id("net.frozenblock.candlelight") version("+") apply(false)

    id("org.quiltmc.gradle.licenser") version("+") apply(false)
    id("com.gradleup.shadow") version("+") apply(false)
    checkstyle
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.kohsuke:github-api:1.326")
    }
}

checkstyle {
    configFile = rootProject.file("checkstyle.xml")
    toolVersion = "10.20.2"
}

val mod_id: String by project
val mod_name: String by project
val mod_version: String by project
val subproject_prefix: String by project
val license: String by project
val mod_url: String by project
val source_url: String by project
val issues_url: String by project
val protocol_version: String by project
val min_fabric_loader_version: String by project
val minecraft_version: String by project

val fabric_api_version: String by project
val fabric_kotlin_version: String by project
val frozenlib_version: String by project

mod {
    additional.add("mod_id", mod_id)
    additional.add("mod_version", mod_version)
    additional.add("mod_name", mod_name)
    additional.add("mod_license", license)
    additional.add("mod_url", mod_url)
    additional.add("source_url", source_url)
    additional.add("issues_url", issues_url)
    additional.add("fabric_loader_version", ">=$min_fabric_loader_version")
    additional.add("fabric_kotlin_version", fabric_kotlin_version)
    additional.add("fabric_api_version", ">=$fabric_api_version")
    additional.add("minecraft_version", "~$minecraft_version-")
}

val changelogText = run {
    val split = file("CHANGELOG.md").readText().split("-----------------")
    check(split.size == 2) { "Malformed changelog" }
    split[1].trim()
}

fun mainJarTask(project: Project) =
    if (project.tasks.names.contains("shadowJar")) project.tasks.named("shadowJar")
    else project.tasks.named("jar")

val githubRelease by tasks.registering {
    val fabricJar = mainJarTask(project(":$subproject_prefix-fabric"))
    val neoforgeJar = mainJarTask(project(":$subproject_prefix-neoforge"))
    dependsOn(fabricJar, neoforgeJar)

    val token = env["GITHUB_TOKEN"]
    val repository = mod.repository.get()
    val tag = project(":$subproject_prefix-fabric").version.toString()
    val releaseTitle = "$mod_name $tag"
    val isPrerelease = mod.releaseType.get() != "release"
    val commitish = env["GITHUB_SHA"]

    onlyIf { !token.isNullOrEmpty() }

    doLast {
        val github = GitHub.connectUsingOAuth(token)
        val repo = github.getRepository(repository)

        repo.getReleaseByTagName(tag)?.delete()

        val releaseBuilder = GHReleaseBuilder(repo, tag)
        releaseBuilder.name(releaseTitle)
        releaseBuilder.body(changelogText)
        releaseBuilder.prerelease(isPrerelease)
        if (commitish != null) releaseBuilder.commitish(commitish)

        val release = releaseBuilder.create()
        release.uploadAsset(fabricJar.get().outputs.files.singleFile, "application/java-archive")
        release.uploadAsset(neoforgeJar.get().outputs.files.singleFile, "application/java-archive")
    }
}

val publishMod by tasks.registering {
    dependsOn(tasks.named("upload"))
    dependsOn(githubRelease)
}

subprojects {
    apply(plugin = "net.frozenblock.triangle.core")
    apply(plugin = "net.frozenblock.candlelight")

    val mavenUrl = env["MAVEN_URL"]
    val mavenUsername = env["MAVEN_USERNAME"]
    val mavenPassword = env["MAVEN_PASSWORD"]

    if (mavenUrl != null && mavenUsername != null && mavenPassword != null) {
        upload {
            maven {
                repositories {
                    maven(mavenUrl) {
                        name = "FrozenBlock"
                        credentials {
                            username = mavenUsername
                            password = mavenPassword
                        }
                    }
                }
            }
        }
    }

    mod {
        versionStrategy = FrozenBlockVersionStrategy()
    }

    version = mod.versionStrategy.get().artifactVersion(mod as ModVersionProperties)

    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(listOf("-Xmaxerrs", "4000"))
        options.release.set(25)
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    dependencies {
        compileOnly("net.frozenblock:candlelight:+")
    }

    repositories {
        maven("https://maven.frozenblock.net/release") {
            name = "FrozenBlock"
        }
        maven("https://maven.quiltmc.org/repository/release") {
            name = "Quilt"
        }
        maven("https://maven.shedaniel.me/")
        exclusiveContent {
            forRepository {
                maven("https://repo.spongepowered.org/repository/maven-public") {
                    name = "Sponge"
                }
            }
            filter { includeGroupAndSubgroups("org.spongepowered") }
        }
        maven("https://maven.blamejared.com") {
            name = "BlameJared"
        }
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://thedarkcolour.github.io/KotlinForForge/") {
            name = "KotlinForForge"
            content {
                includeGroup("thedarkcolour")
            }
        }
        exclusiveContent {
            forRepository {
                maven("https://api.modrinth.com/maven") {
                    name = "Modrinth"
                }
            }
            filter {
                includeGroup("maven.modrinth")
            }
        }
        maven("https://maven.frozenblock.net/snapshot") { // Candlelight & Triangle
            name = "FrozenBlock Snapshot"
        }
    }

    tasks {
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

    afterEvaluate {
        tasks.findByName("curseforge")?.dependsOn("shadowJar")
        tasks.findByName("modrinth")?.dependsOn("shadowJar")
    }
}
