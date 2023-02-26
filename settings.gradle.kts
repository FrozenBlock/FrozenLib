pluginManagement {
    repositories {
        maven {
            name = "Quilt"
            setUrl("https://maven.quiltmc.org/repository/release/")
        }
		maven {
			name = "Quilt Snapshot"
			setUrl("https://maven.quiltmc.org/repository/snapshot/")
		}
        maven {
            name = "Fabric"
            setUrl("https://maven.fabricmc.net/")
        }
        maven {
            name = "Forge"
            setUrl("https://files.minecraftforge.net/maven/")
        }
        maven {
            name = "Jitpack"
            setUrl("https://jitpack.io/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "FrozenLib"
