import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("maven-publish")
}

repositories {
}

dependencies {
    compileOnly(project(":game-history"))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
        }
    }

    withType<ShadowJar> {
        delete("build/resources")

        archiveClassifier.set("")
    }

    register<Copy>("copyToServerWindows") {
        from(shadowJar)
//        into("D:\\서버\\1.8.9 테스트 서버\\update")
        into("D:\\서버\\1.8.9 LeafServer S6\\.plugin_build")
//        into("D:\\서버\\1.8.9 LeafServer S6\\replay.1\\update")
//        into("D:\\서버\\1.8.9 LeafServer S6\\city.ability.pf.1\\update")
    }
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.s8u.kr/repository/maven-pooleaf/")
            credentials {
                username = System.getenv("NEXUS_USERNAME")
                password = System.getenv("NEXUS_PASSWORD")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            artifact(tasks["shadowJar"])
        }
    }
}