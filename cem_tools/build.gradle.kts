import edu.unc.cem.util.WriteVersionInfo

plugins {
    java
    application
}

version = "2.1.1"

repositories {
    mavenCentral()
}

dependencies {
}

tasks {
    register("generateSources") {
        WriteVersionInfo.writeVersionInfo(file("src/main/java/edu/unc/cem/util/VersionInfo.java"), project.version.toString())
    }

    compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"

        dependsOn("generateSources")
    }
}

application {
    // Define the main class for the application.
    mainClass.set("edu.unc.cem.correct.CemCorrect")
}
