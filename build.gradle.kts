import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.4.32"
}
group = "me.jacobtread.pond"
version = "0.0.1"


repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit"))
    // https://mvnrepository.com/artifact/com.fifesoft/rsyntaxtextarea
    implementation("com.fifesoft:rsyntaxtextarea:3.1.3")
    implementation("com.formdev:flatlaf:1.2")
    implementation("com.fifesoft:autocomplete:3.1.2")
    implementation("com.google.code.gson:gson:2.8.7")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.32")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

sourceSets["main"].java {
    srcDir("src/main/gen")
}

val fatJar = task("fatJar", type = Jar::class) {
    archiveBaseName.set(project.name)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "me.jacobtread.pond.Main"
    }
    from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks["jar"] as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}