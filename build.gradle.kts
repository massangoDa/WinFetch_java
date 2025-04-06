plugins {
    id ("java")
    id ("application")
    id("com.gradleup.shadow") version "9.0.0-beta12"
}

application {
    mainClass = "jp.massango.winfetch.Main"
}

group = "jp.massango.winfetch"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(files("./libs/forms_rt.jar"))
    implementation("com.formdev:flatlaf:3.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("com.google.guava:guava:33.4.6-jre")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    manifest {
        attributes(
            "Main-Class" to "jp.massango.winfetch.Main"
        )
    }
}