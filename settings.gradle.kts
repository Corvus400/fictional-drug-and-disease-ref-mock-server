rootProject.name = "fictional-drug-and-disease-ref-mock-server"

// Gradle @Incubating API。dependencyResolutionManagement.repositoriesはGradle公式推奨の書き方
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
