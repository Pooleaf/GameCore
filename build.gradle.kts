plugins {
    kotlin("jvm") version "1.8.10"
    idea
}

allprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()

        maven("https://repo.s8u.kr/repository/maven-minecraft/") // Bukkit
        maven("https://repo.s8u.kr/repository/maven-pooleaf/") // Core
        maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
        maven("https://repo.glaremasters.me/repository/concuncan/") // SWM
        maven("https://maven.citizensnpcs.co/repo") // Citizens
    }

    dependencies {
        // Platform
        compileOnly("io.papermc:paper-api:1.8.8")

        // Pooleaf
        compileOnly("net.pooleaf:core:1.54.0")
        compileOnly("net.pooleaf:permission:1.0.0")

        // Bukkit Library
        compileOnly("com.grinderwolf:slimeworldmanager-api:2.2.1")
        compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
        compileOnly("com.arcaniax:HeadDatabase-API:1.3.1")

        compileOnly("net.citizensnpcs:citizens-main:2.0.30-SNAPSHOT") {
            exclude(group = "*", module = "*")
        }
    }
}