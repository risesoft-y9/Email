plugins {
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.docker")
    id("net.risesoft.y9.java-conventions")
    alias(y9libs.plugins.org.springframework.boot)
}

dependencies {
   // api(platform(libs.y9.digitalbase.bom))
  //  api(platform(y9libs.spring.boot.bom))

    api(project(":risenet-y9boot-support-webmail"))
    api("net.risesoft:risenet-y9boot-starter-sso-oauth2-resource")
    api("net.risesoft:risenet-y9boot-common-nacos")
    api(y9libs.commons.pool2)
    api("org.springframework.boot:spring-boot-starter-tomcat")
}

description = "risenet-y9boot-webapp-webmail"
