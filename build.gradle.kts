plugins {
    id("com.possible-triangle.core")
    id("com.possible-triangle.common") apply(false)
    id("com.possible-triangle.fabric") apply(false)
    id("com.possible-triangle.neoforge") apply(false)
    id("net.mehvahdjukaar.candlelight") version("+") apply(false)
    id("dev.mixinmcp.decompile") version("+") apply(false)

    id("org.ajoberstar.grgit") version("+") apply(false)
    id("org.quiltmc.gradle.licenser") version("+") apply(false)
    id("me.modmuss50.mod-publish-plugin") version("+") apply(false)
    id("com.gradleup.shadow") version("+") apply(false)
    checkstyle
}

checkstyle {
    configFile = rootProject.file("checkstyle.xml")
    toolVersion = "10.20.2"
}

// From Polytone, what does this do?
// MehVahd told me this replaces parts of neo's mods.toml.
// We should test and see if this takes things from Fabric's and puts it into Neo's automatically.
mod {
    additional.add("mod_description")
    additional.add("mod_credits")
    additional.add("mod_license")
    additional.add("mod_homepage")
    additional.add("mod_authors")
    additional.add("mod_github")
}

// TODO: HOW THE HELL DO SUBPROJECTS WORK
subprojects {
    apply(plugin = "com.possible-triangle.core")
    apply(plugin = "net.mehvahdjukaar.candlelight")
    apply(plugin = "dev.mixinmcp.decompile")

    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(listOf("-Xmaxerrs", "4000"))
        options.release.set(25)
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    dependencies {
        compileOnly("net.mehvahdjukaar:candlelight:1.1.0")
    }

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(25)
        withSourcesJar()
        withJavadocJar()
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
                maven {
                    name = "Sponge"
                    url = uri("https://repo.spongepowered.org/repository/maven-public")
                }
            }
            filter { includeGroupAndSubgroups("org.spongepowered") }
        }
        maven {
            name = "BlameJared"
            url = uri("https://maven.blamejared.com")
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
        maven { url = uri("https://registry.somethingcatchy.net/repository/maven-releases/") }
    }
}
