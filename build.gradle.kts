plugins {
    id("java")
}

group = "com.bhf"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":reusable-annotation-processor"))
    annotationProcessor(project(":reusable-annotation-processor"))
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}