plugins {
    alias(libs.plugins.y9.docker)
    alias(libs.plugins.y9.conventions.war)
    alias(libs.plugins.y9.lombok)
}

dependencies {
    providedRuntime(platform(libs.y9.digitalbase.dependencies))

    api(project(":risenet-y9boot-support-webmail"))
    api("net.risesoft:risenet-y9boot-starter-sso-oauth2-resource")
    api("net.risesoft:risenet-y9boot-common-nacos")
    api("org.apache.commons:commons-pool2")

    compileOnly("jakarta.servlet:jakarta.servlet-api")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
}

description = "risenet-y9boot-webapp-webmail"

val finalName = "webmail"
y9Docker {
    appName = finalName
}

y9War {
    archiveBaseName = finalName
}