package y9.client.rest.webmail;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.pojo.Y9Result;

/**
 * 标准电子邮件 (webmail)
 *
 */
@FeignClient(contextId = "StandardEmail4ApiClient", name = "${y9.service.webmail.name:webmail}",
    url = "${y9.service.webmail.directUrl:}", path = "/${y9.service.webmail.name:webmail}/services/rest/standardEmail")
public interface StandardEmailApiClient {

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
    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Y9Result<Object> send(@RequestParam("userId") String userId, @RequestParam("tenantId") String tenantId,
        @RequestParam("subject") String subject, @RequestParam("content") String content,
        @RequestParam(value = "fromEmail", required = false) String fromEmail,
        @RequestParam("toEmail") List<String> toEmail, @RequestPart("file") MultipartFile file);
}
