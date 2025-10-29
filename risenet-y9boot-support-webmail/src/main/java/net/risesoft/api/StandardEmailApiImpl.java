package net.risesoft.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.user.UserApi;
import net.risesoft.api.webmail.StandardEmailApi;
import net.risesoft.controller.dto.EmailDTO;
import net.risesoft.james.service.JamesUserService;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.EmailAttachmentService;
import net.risesoft.service.EmailService;
import net.risesoft.support.DefaultFolder;
import net.risesoft.support.EmailThreadLocalHolder;
import net.risesoft.y9.Y9LoginUserHolder;

import y9.client.rest.platform.org.PersonApiClient;

import dm.jdbc.util.StringUtil;

@RestController(value = "Standard4EmailApiImpl")
@RequestMapping(value = "/services/rest/standardEmail", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
public class StandardEmailApiImpl implements StandardEmailApi {

    private final EmailService emailService;

    private final PersonApiClient personManager;

    private final JamesUserService jamesUserService;

    private final EmailAttachmentService emailAttachmentService;

    private final UserApi userApi;

    @Transactional
    public Y9Result<Object> send(@RequestParam("userId") String userId, @RequestParam("tenantId") String tenantId,
        @RequestParam("subject") String subject, @RequestParam("content") String content,
        @RequestParam(value = "fromEmail", required = false) String fromEmail,
        @RequestParam("toEmail") List<String> toEmail, @RequestPart("file") MultipartFile file) {
        Y9LoginUserHolder.setTenantId(tenantId);
        UserInfo userInfo = userApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(userInfo);

        EmailDTO email = new EmailDTO();
        email.setSubject(subject);
        email.setRichText(content);
        email.setText(content);
        email.setToEmailAddressList(toEmail);
        if (StringUtil.isEmpty(fromEmail)) {
            EmailThreadLocalHolder.setEmailAddress(jamesUserService.getEmailAddressByPersonId(userId));
            email.setFrom(EmailThreadLocalHolder.getEmailAddress());
        } else {
            EmailThreadLocalHolder.setEmailAddress(fromEmail);
            email.setFrom(fromEmail);
        }
        try {
            String messageId = emailService.save(email);

            emailAttachmentService.addAttachment(DefaultFolder.DRAFTS.getName(), messageId, file);
            emailService.send(messageId);
        } catch (Exception e) {
            LOGGER.warn("发送标准电子邮件失败", e);
            return Y9Result.failure("发送标准电子邮件失败，原因：" + e.getMessage());
        }

        return Y9Result.successMsg("发送成功");
    }

}
