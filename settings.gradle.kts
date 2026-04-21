rootProject.name = "mock-server-base"

// Gradle @Incubating API。dependencyResolutionManagement.repositoriesはGradle公式推奨の書き方
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
