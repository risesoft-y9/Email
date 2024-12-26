plugins {
    java
    id("com.vanniktech.maven.publish") version "0.30.0" apply false
    id("tech.yanand.maven-central-publish") version "1.3.0" apply false

    id("net.risesoft.y9.aspectj") version "9.7.0-01" apply false
    id("net.risesoft.y9.docker") version "9.7.0-01" apply false
    id("net.risesoft.y9.java-conventions") version "9.7.0-01" apply false
    id("net.risesoft.y9.java-publish") version "9.7.0-01" apply false
    id("net.risesoft.y9.java-publish-central") version "9.7.0-01" apply false
    id("net.risesoft.y9.javaPlatform-publish") version "9.7.0-01" apply false
    id("net.risesoft.y9.javaPlatform-publish-central") version "9.7.0-01" apply false
    id("net.risesoft.y9.lombok") version "9.7.0-01" apply false
    id("net.risesoft.y9.management") version "9.7.0-01" apply true
    id("net.risesoft.y9.repository") version "9.7.0-01" apply false
    id("net.risesoft.y9.smart-doc") version "9.7.0-01" apply false
}

repositories {
    mavenCentral()
    //gradlePluginPortal()
}

dependencies {
    //management(platform(project(":y9-digitalbase-dependencies")))
    //management(platform(libs.spring.boot.bom))
}

group = "net.risesoft"
version = "v9.7.0-SNAPSHUT"