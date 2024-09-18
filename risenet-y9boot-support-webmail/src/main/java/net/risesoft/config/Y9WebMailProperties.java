package net.risesoft.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "y9.app.webmail", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9WebMailProperties {

    /**
     * 邮箱域名
     */
    private String host = "youshengyun.com";
    /**
     * imap 域名
     */
    private String imapHost = "imap.youshengyun.com";
    /**
     * imap 协议端口
     */
    private Integer imapPort = 143;

    /**
     * smtp 域名
     */
    private String smtpHost = "smtp.youshengyun.com";

    /**
     * smtp 端口
     */
    private Integer smtpPort = 25;

    /**
     * james jmx 管理IP
     */
    private String jmxHost = "127.0.0.1";
    /**
     * james jmx 管理端口
     */
    private Integer jmxPort = 9999;

}
