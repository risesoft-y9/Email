package net.risesoft.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.sun.mail.imap.IMAPFolder;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.controller.dto.EmailAttachmentDTO;
import net.risesoft.controller.dto.EmailContactDTO;
import net.risesoft.controller.dto.EmailListDTO;
import net.risesoft.controller.dto.ToDTO;
import net.risesoft.james.entity.JamesUser;
import net.risesoft.james.entity.term.MyMessageIDTerm;
import net.risesoft.james.service.JamesUserService;
import net.risesoft.model.platform.org.Person;
import net.risesoft.support.EmailThreadLocalHolder;
import net.risesoft.util.MimeMessageParser;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.y9webmail.Y9WebMailProperties;
import net.risesoft.y9.util.signing.Y9MessageDigestUtil;

@RequiredArgsConstructor
public class MailHelper {

    protected final Y9WebMailProperties y9WebMailProperties;

    protected final JamesUserService jamesUserService;

    protected final PersonApi personApi;

    public static EmailAttachmentDTO parseEmailAttachment(DataSource dataSource) throws IOException {
        EmailAttachmentDTO emailAttachmentDTO = new EmailAttachmentDTO();

        byte[] bytes = IOUtils.toByteArray(dataSource.getInputStream());
        emailAttachmentDTO.setMd5(Y9MessageDigestUtil.md5(bytes));
        emailAttachmentDTO.setFileExt(FilenameUtils.getExtension(dataSource.getName()));
        emailAttachmentDTO.setFileName(dataSource.getName());
        emailAttachmentDTO.setDisplaySize(FileUtils.byteCountToDisplaySize(bytes.length));

        return emailAttachmentDTO;
    }

    public Store createReceiveMailSession() throws MessagingException {
        // todo 密码
        // String plainText = properties.getCommon().getDefaultPassword();
        String plainText = jamesUserService.getPlainTextByPersonId(Y9LoginUserHolder.getUserInfo().getPersonId());
        String email = EmailThreadLocalHolder.getEmailAddress();

        Properties props = new Properties();
        props.put("mail.store.protocol", "imap");
        // 3.7.x james 无 authorizationid 时用户密码存在@符号会有问题
        // 具体可跟踪源码 org.apache.james.imap.processor.AuthenticateProcessor#parseDelegationAttempt
        props.put("mail.imap.sasl.authorizationid", email);
        props.put("mail.imap.host", y9WebMailProperties.getImapHost());
        props.put("mail.imap.port", y9WebMailProperties.getImapPort());
        props.put("mail.imap.auth", "true");

        Session session = Session.getInstance(props);
        // session.setDebug(true);

        Store store = session.getStore();
        store.connect(email, plainText);

        return store;
    }

    public Session createSendMailSession() {
        // String plainText = properties.getCommon().getDefaultPassword();
        String plainText = jamesUserService.getPlainTextByPersonId(Y9LoginUserHolder.getUserInfo().getPersonId());
        String email = EmailThreadLocalHolder.getEmailAddress();

        Properties prop = new Properties();
        prop.put("mail.smtp.host", y9WebMailProperties.getSmtpHost());
        prop.put("mail.smtp.port", y9WebMailProperties.getSmtpPort());
        prop.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, plainText);
            }
        });
        // session.setDebug(true);
        return session;
    }

    public Message getMessage(IMAPFolder folder, String messageId) throws MessagingException {
        Message[] searchedMessage = folder.search(new MyMessageIDTerm(messageId));
        if (searchedMessage.length > 0) {
            return searchedMessage[0];
        }
        return null;
    }

    public List<EmailAttachmentDTO> parseEmailAttachmentList(List<DataSource> attachmentList) throws IOException {
        List<EmailAttachmentDTO> emailAttachmentDTOList = new ArrayList<>();
        for (DataSource dataSource : attachmentList) {
            emailAttachmentDTOList.add(parseEmailAttachment(dataSource));
        }
        return emailAttachmentDTOList;
    }

    public void getPersonData(IMAPFolder folder, List<EmailListDTO> emailReceiverDTOList) {
        JamesUser JamesUser = null;
        Person person = null;
        try {
            for (EmailListDTO emailListDTO : emailReceiverDTOList) {
                Message message = folder.getMessageByUID(emailListDTO.getUid());
                if (message == null)
                    continue;
                MimeMessageParser parser = new MimeMessageParser((MimeMessage)message).parse();
                List<String> emailAddressList = parser.getTo()
                    .stream()
                    .map(address -> ((InternetAddress)address).getAddress())
                    .collect(Collectors.toList());
                List<ToDTO> toDTOList = new ArrayList<>();
                for (String emailAddress : emailAddressList) {
                    if (!emailAddress.contains("@youshengyun.com"))
                        break;
                    ToDTO toDTO = new ToDTO();
                    toDTO.setTo(emailAddress);
                    JamesUser = jamesUserService.findByEmailAddress(emailAddress);
                    if (JamesUser != null) {
                        person = personApi.get(Y9LoginUserHolder.getTenantId(), JamesUser.getPersonId()).getData();
                        toDTO.setToName(person.getName());
                        toDTO.setToAvator(person.getAvator());
                    }
                    toDTOList.add(toDTO);
                }
                emailListDTO.setToDTOList(toDTOList);
                if (parser.getFrom().contains("@youshengyun.com")) {
                    emailListDTO.setFrom(parser.getFrom());
                    JamesUser = jamesUserService.findByEmailAddress(emailListDTO.getFrom());
                    if (JamesUser != null) {
                        person = personApi.get(Y9LoginUserHolder.getTenantId(), JamesUser.getPersonId()).getData();
                        emailListDTO.setFromName(person.getName());
                        emailListDTO.setFromAvator(person.getAvator());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getEmailContactDTOList(IMAPFolder folder, List<EmailListDTO> emailReceiverDTOList,
        List<EmailContactDTO> contactDTOList) {
        JamesUser JamesUser = null;
        Person person = null;
        try {
            String email = jamesUserService.getEmailAddressByPersonId(Y9LoginUserHolder.getPersonId());
            for (EmailListDTO emailListDTO : emailReceiverDTOList) {
                Message message = folder.getMessageByUID(emailListDTO.getUid());
                if (message == null)
                    continue;
                MimeMessageParser parser = new MimeMessageParser((MimeMessage)message).parse();
                List<String> emailAddressList = parser.getTo()
                    .stream()
                    .map(address -> ((InternetAddress)address).getAddress())
                    .collect(Collectors.toList());
                for (String emailAddress : emailAddressList) {
                    if (emailAddress.equals(email))
                        continue;
                    boolean exists = contactDTOList.stream()
                        .anyMatch(param -> param.getContactPerson() instanceof String
                            && param.getContactPerson().equalsIgnoreCase(emailAddress));
                    if (exists)
                        continue;
                    EmailContactDTO contactDTO = new EmailContactDTO();
                    contactDTO.setContactPerson(emailAddress);
                    if (emailAddress.contains("@youshengyun.com")) {
                        JamesUser = jamesUserService.findByEmailAddress(emailAddress);
                        if (JamesUser != null) {
                            person = personApi.get(Y9LoginUserHolder.getTenantId(), JamesUser.getPersonId()).getData();
                            contactDTO.setContactPersonId(person.getId());
                            contactDTO.setContactPersonName(person.getName());
                            contactDTO.setContactPersonAvator(person.getAvator());
                        }
                    }
                    contactDTOList.add(contactDTO);
                }

                String from = parser.getFrom();
                if (StringUtils.isNotBlank(from) && !from.equals(email)) {
                    boolean exists = contactDTOList.stream()
                        .anyMatch(param -> param.getContactPerson() instanceof String
                            && param.getContactPerson().equalsIgnoreCase(from));
                    if (exists)
                        continue;
                    EmailContactDTO contactDTO = new EmailContactDTO();
                    contactDTO.setContactPerson(parser.getFrom());
                    if (parser.getFrom().contains("@youshengyun.com")) {
                        JamesUser = jamesUserService.findByEmailAddress(parser.getFrom());
                        if (JamesUser != null) {
                            person = personApi.get(Y9LoginUserHolder.getTenantId(), JamesUser.getPersonId()).getData();
                            contactDTO.setContactPersonId(person.getId());
                            contactDTO.setContactPersonName(person.getName());
                            contactDTO.setContactPersonAvator(person.getAvator());
                        }
                    }
                    contactDTOList.add(contactDTO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
