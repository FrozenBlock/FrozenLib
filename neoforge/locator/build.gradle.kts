plugins {
    java
}

val neoforge_version: String by project

repositories {
    maven("https://maven.neoforged.net/releases") { name = "NeoForged" }
}

dependencies {
    compileOnly("net.neoforged.fancymodloader:loader:11.0.13")
    compileOnly(project(":flib-common"))
}

upload {
    maven {
        group.set(null as String?)
    }
}
