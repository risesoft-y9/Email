plugins {
    alias(libs.plugins.y9.conventions.java)
    alias(libs.plugins.y9.lombok)
    alias(libs.plugins.y9.smart.doc)
}

dependencies {
    api("net.risesoft:risenet-y9boot-starter-idgenerator")
    api("net.risesoft:risenet-y9boot-properties")
    api("net.risesoft:risenet-y9boot-starter-jpa-public")
    api("net.risesoft:risenet-y9boot-starter-jpa-dedicated")
    api("net.risesoft:risenet-y9boot-starter-log")
    api("net.risesoft:risenet-y9boot-support-file-service-ftp")
    api("net.risesoft:risenet-y9boot-api-feignclient-platform")
    api("net.risesoft:risenet-y9boot-starter-listener-kafka")
    api("net.risesoft:risenet-y9boot-starter-security")
    api("net.risesoft:risenet-y9boot-starter-web")
    api(project(":risenet-y9boot-api-interface-webmail"))

    api("org.springframework.boot:spring-boot-starter-validation")
    api("com.google.guava:guava")
    api("org.apache.commons:commons-lang3")
    api(libs.jakarta.mail.jakarta.mail.api)
    api(libs.com.sun.mail.jakarta.mail)
    api(libs.jodd.mail)
    api(libs.commons.text)
    api(libs.james.apache.mime4j.dom)
    api(libs.james.apache.mime4j.mbox.iterator)
    api(libs.james.apache.mime4j.storage)
    api(libs.pinyin4j)

    compileOnly("jakarta.servlet:jakarta.servlet-api")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

}

description = "risenet-y9boot-support-webmail"
