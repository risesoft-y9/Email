package net.risesoft.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.controller.dto.EmailDTO;
import net.risesoft.james.service.JamesUserService;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.EmailAttachmentService;
import net.risesoft.service.EmailService;
import net.risesoft.support.DefaultFolder;
import net.risesoft.support.EmailThreadLocalHolder;
import net.risesoft.y9.Y9LoginUserHolder;

import y9.client.platform.org.PersonApiClient;

import dm.jdbc.util.StringUtil;

@RestController(value = "Standard4EmailApiImpl")
@RequestMapping(value = "/services/rest/standardEmail", produces = MediaType.APPLICATION_JSON_VALUE)
public class StandardEmailApiImpl {

    @Autowired
    EmailService emailService;

    @Autowired
    PersonApiClient personManager;

    @Autowired
    JamesUserService jamesUserService;

    @Autowired
    private EmailAttachmentService emailAttachmentService;

    @Transactional
    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Y9Result<Object> send(@RequestParam("userId") String userId, @RequestParam("tenantId") String tenantId,
        @RequestParam("subject") String subject, @RequestParam("content") String content,
        @RequestParam(value = "fromEmail", required = false) String fromEmail,
        @RequestParam("toEmail") List<String> toEmail, @RequestPart("file") MultipartFile file) throws Exception {
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setUserInfo(person.toUserInfo());

        EmailDTO email = new EmailDTO();
        email.setSubject(subject);
        email.setRichText(content);
        email.setText(content);
        email.setToEmailAddressList(toEmail);
        if (StringUtil.isEmpty(fromEmail)) {
            EmailThreadLocalHolder.setEmailAddress(jamesUserService.getEmailAddressByPersonId(person.getId()));
            email.setFrom(EmailThreadLocalHolder.getEmailAddress());
        } else {
            EmailThreadLocalHolder.setEmailAddress(fromEmail);
            email.setFrom(fromEmail);
        }
        String messageId = emailService.save(email);

        emailAttachmentService.addAttachment(DefaultFolder.DRAFTS.getName(), messageId, file);
        emailService.send(messageId);

        return Y9Result.successMsg("发送成功");
    }

}
