package net.risesoft.service.impl;

import java.util.List;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.MimetypesFileTypeMap;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

import org.springframework.stereotype.Service;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.james.service.JamesUserService;
import net.risesoft.model.user.UserInfo;
import net.risesoft.service.EmailMessageService;
import net.risesoft.support.EmailThreadLocalHolder;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.y9webmail.Y9WebMailProperties;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

import jodd.mail.SendMailSession;

@Service
public class EmailMessageServiceImpl extends MailHelper implements EmailMessageService {

    private final Y9FileStoreService y9FileStoreService;

    public EmailMessageServiceImpl(Y9WebMailProperties y9WebMailProperties, JamesUserService jamesUserService,
        PersonApi personApi, Y9FileStoreService y9FileStoreService) {
        super(y9WebMailProperties, jamesUserService, personApi);
        this.y9FileStoreService = y9FileStoreService;
    }

    @Override
    public void send(Integer messageNumber, String subject, String richText, List<String> emailAddressList,
        List<String> ccEmailAddressList, List<String> bccEmailAddressList, List<String> attachmentIdList)
        throws Exception {
        SendMailSession sendMailSession = createSendMailSession();
        sendMailSession.open();
        Session session = sendMailSession.getSession();
        MimeMessage mimeMessage = createEmail(session, emailAddressList, ccEmailAddressList, bccEmailAddressList,
            subject, richText, attachmentIdList);
        Transport transport = sendMailSession.getService();
        // transport.connect(emailHost, loginName + "@" + emailHost, plainText);

        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        sendMailSession.close();
    }

    public MimeMessage createEmail(Session session, List<String> toJamesUserIds, List<String> ccJamesUserIds,
        List<String> bccJamesUserIds, String title, String body, List<String> attachmentIdList) throws Exception {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        MimeMessage message = new MimeMessage(session);
        /** 发件人 **/
        message.setFrom(new InternetAddress(EmailThreadLocalHolder.getEmailAddress(), userInfo.getName(), "UTF-8"));
        /** 收件人 **/
        if (toJamesUserIds != null) {
            int toSize = toJamesUserIds.size();
            Address[] toAddress = new Address[toSize];
            for (int i = 0; i < toSize; i++) {
                toAddress[i] = new InternetAddress(toJamesUserIds.get(i));
            }
            message.setRecipients(Message.RecipientType.TO, toAddress);
        }
        /** 抄送人 **/
        if (ccJamesUserIds != null) {
            int ccSize = ccJamesUserIds.size();
            Address[] ccAddress = new Address[ccSize];
            for (int i = 0; i < ccSize; i++) {
                ccAddress[i] = new InternetAddress(ccJamesUserIds.get(i));
            }
            message.setRecipients(Message.RecipientType.CC, ccAddress);
        }
        /** 密送人 **/
        if (bccJamesUserIds != null) {
            int bccSize = bccJamesUserIds.size();
            Address[] bccAddress = new Address[bccSize];
            for (int i = 0; i < bccSize; i++) {
                bccAddress[i] = new InternetAddress(bccJamesUserIds.get(i));
            }
            message.setRecipients(Message.RecipientType.BCC, bccAddress);
        }
        /** 主题 **/
        message.setSubject(title);
        MimeMultipart mimeMultipart = new MimeMultipart();
        /** 文本内容 **/
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(body, "text/html;charset=UTF-8");
        mimeMultipart.addBodyPart(bodyPart);
        /** 附件内容 **/
        if (attachmentIdList != null) {
            MimeBodyPart mimeBodyPart = null;
            for (String attachmentId : attachmentIdList) {
                mimeBodyPart = new MimeBodyPart();
                byte[] bytes = y9FileStoreService.downloadFileToBytes(attachmentId);
                Y9FileStore y9FileStore = y9FileStoreService.getById(attachmentId);
                DataSource dataSource = new ByteArrayDataSource(bytes,
                    new MimetypesFileTypeMap().getContentType(y9FileStore.getFileName()));
                DataHandler dataHandler = new DataHandler(dataSource);
                mimeBodyPart.setDataHandler(dataHandler);
                mimeBodyPart.setFileName(y9FileStore.getFileName());
                mimeMultipart.addBodyPart(mimeBodyPart);
            }
        }
        message.setContent(mimeMultipart);
        message.saveChanges();
        return message;
    }
}
