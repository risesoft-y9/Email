package net.risesoft.api.webmail;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import net.risesoft.pojo.Y9Result;

/**
 * 标准电子邮件 (webmail)
 */
public interface StandardEmailApi {

    /**
     * 发送标准电子邮件
     *
     * @param userId 发件人Id
     * @param tenantId
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param fromEmail 发件人 （可不填，默认当前userId的email）
     * @param toEmail 收件人
     * @param file 邮件附件
     */
    Y9Result<Object> send(String userId, String tenantId, String subject, String content, String fromEmail,
        List<String> toEmail, MultipartFile file);
}
