/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

pluginManagement {
    // Include 'plugins build' to define convention plugins.
    //includeBuild("build-logic")
}


dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    versionCatalogs {
        create("y9libs") {
            // from(files("../gradle/libs.versions.toml"))
            from("net.risesoft.y9:risenet-gradle-version-catalog:9.7.0-01")
        }
    }
}

rootProject.name = "y9-email"
include(":risenet-y9boot-support-webmail")
include(":risenet-y9boot-api-interface-webmail")
include(":risenet-y9boot-api-feignclient-webmail")
include(":risenet-y9boot-webapp-webmail")
