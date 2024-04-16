import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.aurelium"
version = project.property("projectVersion") as String

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    implementation("net.objecthunter:exp4j:0.4.8")
    implementation("com.github.Osiris-Team:Dream-Yaml:6.9")
    implementation("com.github.Archy-X:Polyglot:d6debe617a")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    compileOnly("dev.aurelium:auraskills-api-bukkit:2.0.0-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7-SNAPSHOT")
}

tasks.withType<ShadowJar> {
    val projectVersion: String by project
    archiveFileName.set("AuraMobs-${projectVersion}.jar")

    relocate("co.aikar.commands", "dev.aurelium.auramobs.acf")
    relocate("co.aikar.locales", "dev.aurelium.auramobs.locales")
    relocate("com.archyx.polyglot", "dev.aurelium.auramobs.polyglot")
}

java.sourceCompatibility = JavaVersion.VERSION_17

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand("projectVersion" to project.version)
        }
    }
    build {
        dependsOn(shadowJar)
    }
    jar {
        enabled = false
    }
    javadoc {
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
    options.isFork = true
    options.forkOptions.executable = "javac"
}