import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.5"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "dev.aurelium"
version = project.property("projectVersion") as String

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots/")
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://mvn.lumine.io/repository/maven-public/")
}

dependencies {
    implementation("net.objecthunter:exp4j:0.4.8")
    implementation("com.github.Archy-X:Polyglot:1.2.1") {
        exclude("org.yaml", "snakeyaml")
    }
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:3.0.2")
    compileOnly("dev.aurelium:auraskills-api-bukkit:2.3.7")
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7-SNAPSHOT")
    compileOnly("io.lumine:Mythic-Dist:5.6.1")
}

tasks.withType<ShadowJar> {
    val projectVersion: String by project
    archiveFileName.set("AuraMobs-${projectVersion}.jar")

    relocate("co.aikar.commands", "dev.aurelium.auramobs.acf")
    relocate("co.aikar.locales", "dev.aurelium.auramobs.locales")
    relocate("com.archyx.polyglot", "dev.aurelium.auramobs.polyglot")
    relocate("org.bstats", "dev.aurelium.auramobs.bstats")
    relocate("net.kyori", "dev.aurelium.auramobs.kyori")
    relocate("net.objecthunter.exp4j", "dev.aurelium.auramobs.exp4j")
    relocate("org.spongepowered.configurate", "dev.aurelium.auramobs.configurate")
    relocate("io.leangen.geantyref", "dev.aurelium.auramobs.geantyref")
}

val compiler = javaToolchains.compilerFor {
    languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    val projectVersion = project.version.toString()

    processResources {
        filesMatching("plugin.yml") {
            expand("projectVersion" to projectVersion)
        }
    }

    build {
        dependsOn(shadowJar)
    }

    javadoc {
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
        options.compilerArgs.add("-Xlint:deprecation")
        options.isFork = true
        options.forkOptions.executable = compiler.map { it.executablePath }.get().toString()
    }

    runServer {
        minecraftVersion("1.21.11")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.Archy-X"
            artifactId = "AuraMobs"
            version = project.property("projectVersion") as String

            from(components["java"])
        }
    }
}
