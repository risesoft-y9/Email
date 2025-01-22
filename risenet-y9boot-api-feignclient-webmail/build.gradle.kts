plugins {
    alias(libs.plugins.y9.conventions.java)
    alias(libs.plugins.y9.lombok)
}

dependencies {
    //   api(platform(libs.y9.digitalbase.bom))
    api("net.risesoft:risenet-y9boot-starter-openfeign")
    api(project(":risenet-y9boot-api-interface-webmail"))

    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-api-feignclient-webmail"
