pluginManagement {
    repositories {
        maven("https://maven.quiltmc.org/repository/release") {
            name = "Quilt"
        }
        maven("https://maven.fabricmc.net") {
            name = "Fabric"
        }
        maven("https://jitpack.io") {
            name = "Jitpack"
        }
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "FrozenLib"
