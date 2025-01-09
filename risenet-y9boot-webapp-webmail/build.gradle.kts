plugins {
    alias(libs.plugins.y9.docker)
    alias(libs.plugins.y9.conventions.war)
    alias(libs.plugins.y9.lombok)
    alias(y9libs.plugins.org.springframework.boot)
}

dependencies {
    // api(platform(libs.y9.digitalbase.bom))
    providedRuntime(platform(y9libs.spring.boot.bom))

    api(project(":risenet-y9boot-support-webmail"))
    api("net.risesoft:risenet-y9boot-starter-sso-oauth2-resource")
    api("net.risesoft:risenet-y9boot-common-nacos")
    api("org.apache.commons:commons-pool2")

    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
}

description = "risenet-y9boot-webapp-webmail"

val finalName = "webmail"
jib.container.appRoot = "/usr/local/tomcat/webapps/${finalName}"

tasks.bootWar {
    archiveFileName.set("${finalName}.${archiveExtension.get()}")
}