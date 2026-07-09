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

    repositories {
        // Standard repositories
        mavenLocal()
        mavenCentral()

        flatDir {
            dirs("mods")
        }

        maven { url = uri("https://jitpack.io") }

        maven { url = uri("https://maven.neoforged.net/releases") }
        maven { url = uri("https://maven.architectury.dev") }
        maven { url = uri("https://maven.parchmentmc.org") }

        maven { url = uri("https://maven.createmod.net") } // Create Mod, Ponder, Flywheel
        maven { url = uri("https://maven.blamejared.com") } // JEI, Vazkii's Mods
        maven { url = uri("https://maven.ladysnake.org/releases") } // Ladysnake mods
        maven { url = uri("https://maven.tterrag.com/") } // Flywheel, EnderIO
        maven { url = uri("https://mvn.devos.one/releases/") } // Registrate, Porting Lib (releases)
        maven { url = uri("https://mvn.devos.one/snapshots/") } // Registrate, Porting Lib (snapshots)
        maven { url = uri("https://maven.terraformersmc.com/") } // TerraformersMC mods
        maven { url = uri("https://maven.saps.dev/releases") } // FTB Mods
        maven { url = uri("https://dl.cloudsmith.io/public/tslat/sbl/maven/") }
        maven { url = uri("https://maven.theillusivec4.top/") } // Curios API
        maven { url = uri("https://maven.squiddev.cc") } // CC: Tweaked
        maven { url = uri("https://maven.su5ed.dev/releases") } // SU5ED mods
        maven { url = uri("https://harleyoconnor.com/maven") } // Dynamic Trees
        maven { url = uri("https://maven.misterpemodder.com/libs-release/") } // ShulkerBoxTooltip
        maven { url = uri("https://maven.firstdarkdev.xyz/snapshots") } // FirstDarkDev (snapshots)
        maven { url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven") } // Fuzss' Mod Resources
        maven { url = uri("https://maven.jamieswhiteshirt.com/libs-release") } // Jamie's Mods
    }
}
