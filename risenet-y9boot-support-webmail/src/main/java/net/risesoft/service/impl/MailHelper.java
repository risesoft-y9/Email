package net.risesoft.service.impl;

import com.sun.mail.imap.IMAPFolder;
import jodd.mail.ImapServer;
import jodd.mail.MailServer;
import jodd.mail.ReceiveMailSession;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import net.risesoft.controller.dto.EmailAttachmentDTO;
import net.risesoft.controller.dto.EmailContactDTO;
import net.risesoft.controller.dto.EmailListDTO;
import net.risesoft.controller.dto.ToDTO;
import net.risesoft.james.entity.JamesUser;
import net.risesoft.james.entity.term.MyMessageIDTerm;
import net.risesoft.james.service.JamesUserService;
import net.risesoft.model.platform.Person;
import net.risesoft.support.EmailThreadLocalHolder;
import net.risesoft.util.MimeMessageParser;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.app.y9webmail.Y9WebmailProperties;
import net.risesoft.y9.util.signing.Y9MessageDigest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import y9.client.rest.platform.org.PersonApiClient;

import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MailHelper {

    @Autowired
    protected Y9Properties properties;

    @Autowired
    private JamesUserService jamesUserService;

    @Autowired
    private PersonApiClient personManager;

    public ReceiveMailSession createReceiveMailSession() {
        // String plainText = properties.getCommon().getDefaultPassword();
        String plainText = jamesUserService.getPlainTextByPersonId(Y9LoginUserHolder.getUserInfo().getPersonId());
        String email = EmailThreadLocalHolder.getEmailAddress();

        // todo 密码
        Y9WebmailProperties emailProperties = properties.getApp().getWebmail();
        ImapServer imapServer = MailServer.create().host(emailProperties.getImapHost())
            // 3.7.x james 无 authorizationid 时用户密码存在@符号会有问题
            // 具体可跟踪源码 org.apache.james.imap.processor.AuthenticateProcessor#parseDelegationAttempt
            .property("mail.imap.sasl.authorizationid", email).port(emailProperties.getImapPort())
            .auth(email, plainText).buildImapMailServer();
        return imapServer.createSession();
    }

    public SendMailSession createSendMailSession() {
        // String plainText = properties.getCommon().getDefaultPassword();
        String plainText = jamesUserService.getPlainTextByPersonId(Y9LoginUserHolder.getUserInfo().getPersonId());
        String email = EmailThreadLocalHolder.getEmailAddress();

        Y9WebmailProperties emailProperties = properties.getApp().getWebmail();
        SmtpServer smtpServer = MailServer.create().host(emailProperties.getSmtpHost())
            .port(emailProperties.getSmtpPort()).auth(email, plainText).buildSmtpMailServer();
        return smtpServer.createSession();
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

    public static EmailAttachmentDTO parseEmailAttachment(DataSource dataSource) throws IOException {
        EmailAttachmentDTO emailAttachmentDTO = new EmailAttachmentDTO();

        byte[] bytes = IOUtils.toByteArray(dataSource.getInputStream());
        emailAttachmentDTO.setMd5(Y9MessageDigest.MD5(bytes));
        emailAttachmentDTO.setFileExt(FilenameUtils.getExtension(dataSource.getName()));
        emailAttachmentDTO.setFileName(dataSource.getName());
        emailAttachmentDTO.setDisplaySize(FileUtils.byteCountToDisplaySize(bytes.length));

        return emailAttachmentDTO;
    }

    public void getPersonData( IMAPFolder folder, List<EmailListDTO> emailReceiverDTOList) {
        JamesUser JamesUser = null;
        Person person = null;
        try {
            for(EmailListDTO emailListDTO : emailReceiverDTOList){
                Message message = folder.getMessageByUID(emailListDTO.getUid());
                if(message == null) continue;
                MimeMessageParser parser = new MimeMessageParser((MimeMessage)message).parse();
                List<String> emailAddressList = parser.getTo().stream().map(address -> ((InternetAddress)address).getAddress()).collect(Collectors.toList());
                if(emailAddressList != null && emailAddressList.size() != 0 ){
                    List<ToDTO> toDTOList = new ArrayList<ToDTO>();
                    for(String emailAddress : emailAddressList){
                        if(emailAddress.indexOf("@youshengyun.com") == -1) break;
                        ToDTO toDTO = new ToDTO();
                        toDTO.setTo(emailAddress);
                        JamesUser = jamesUserService.findByEmailAddress(emailAddress);
                        if(JamesUser != null) {
                            person = personManager.getPerson(Y9LoginUserHolder.getTenantId(),JamesUser.getPersonId()).getData();
                            toDTO.setToName(person.getName());
                            toDTO.setToAvator(person.getAvator());
                        }
                        toDTOList.add(toDTO);
                    }
                    emailListDTO.setToDTOList(toDTOList);
                }
                if(parser.getFrom().indexOf("@youshengyun.com") != -1) {
                    emailListDTO.setFrom(parser.getFrom());
                    JamesUser = jamesUserService.findByEmailAddress(emailListDTO.getFrom());
                    if(JamesUser != null) {
                        person = personManager.getPerson(Y9LoginUserHolder.getTenantId(),JamesUser.getPersonId()).getData();
                        emailListDTO.setFromName(person.getName());
                        emailListDTO.setFromAvator(person.getAvator());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void getEmailContactDTOList(IMAPFolder folder, List<EmailListDTO> emailReceiverDTOList, List<EmailContactDTO> contactDTOList){
        JamesUser JamesUser = null;
        Person person = null;
        try {
            String email = jamesUserService.getEmailAddressByPersonId(Y9LoginUserHolder.getPersonId());
            for(EmailListDTO emailListDTO : emailReceiverDTOList){
                Message message = folder.getMessageByUID(emailListDTO.getUid());
                if(message == null) continue;
                MimeMessageParser parser = new MimeMessageParser((MimeMessage)message).parse();
                List<String> emailAddressList = parser.getTo().stream().map(address -> ((InternetAddress)address).getAddress()).collect(Collectors.toList());
                if(emailAddressList != null && emailAddressList.size() != 0 ){
                    for(String emailAddress : emailAddressList){
                        if(emailAddress.equals(email)) continue;
                        boolean exists = contactDTOList.stream().anyMatch(param -> param.getContactPerson() instanceof String && ((String) param.getContactPerson()).equalsIgnoreCase(emailAddress));
                        if(exists) continue;
                        EmailContactDTO contactDTO = new EmailContactDTO();
                        contactDTO.setContactPerson(emailAddress);
                        if(emailAddress.indexOf("@youshengyun.com") != -1) {
                            JamesUser = jamesUserService.findByEmailAddress(emailAddress);
                            if(JamesUser != null) {
                                person = personManager.getPerson(Y9LoginUserHolder.getTenantId(),JamesUser.getPersonId()).getData();
                                contactDTO.setContactPersonId(person.getId());
                                contactDTO.setContactPersonName(person.getName());
                                contactDTO.setContactPersonAvator(person.getAvator());
                            }
                        }
                        contactDTOList.add(contactDTO);
                    }
                }
                String from = parser.getFrom();
                if(StringUtils.isNotBlank(from) && !from.equals(email)) {
                    boolean exists = contactDTOList.stream().anyMatch(param -> param.getContactPerson() instanceof String && ((String) param.getContactPerson()).equalsIgnoreCase(from));
                    if(exists) continue;
                    EmailContactDTO contactDTO = new EmailContactDTO();
                    contactDTO.setContactPerson(parser.getFrom());
                    if(parser.getFrom().indexOf("@youshengyun.com") != -1) {
                        JamesUser = jamesUserService.findByEmailAddress(parser.getFrom());
                        if(JamesUser != null) {
                            person = personManager.getPerson(Y9LoginUserHolder.getTenantId(),JamesUser.getPersonId()).getData();
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
