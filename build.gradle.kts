plugins {
    kotlin("jvm") version "1.8.10"
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
        compileOnly("io.papermc:paper:1.8.8")
        compileOnly("net.pooleaf:core:0.0.47")

        compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")

        compileOnly("com.grinderwolf:slimeworldmanager-api:2.2.1")

        compileOnly("net.citizensnpcs:citizens-main:2.0.30-SNAPSHOT") {
            exclude(group = "*", module = "*")
        }
    }
}