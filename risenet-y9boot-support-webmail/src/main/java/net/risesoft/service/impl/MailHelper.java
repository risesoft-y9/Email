package net.risesoft.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.activation.DataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

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
import net.risesoft.model.platform.org.OrgUnit;
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

    /**
     * 填充人员数据
     * @param emailReceiverDTOList
     */
    public void populatePersonData(List<EmailListDTO> emailReceiverDTOList) {
        // 获取邮件列表所有邮件的发件人、收件人的邮箱，后面直接批量获取数据
        List<String> emailAddressList = emailReceiverDTOList.stream().flatMap(emailListDTO -> {
            Stream<String> fromStream = Stream.ofNullable(emailListDTO.getFrom());

            Stream<String> toStream = Optional.ofNullable(emailListDTO.getToDTOList())
                .orElse(Collections.emptyList())
                .stream()
                .map(ToDTO::getTo);

            return Stream.concat(fromStream, toStream);
        })
            .filter(Objects::nonNull)
            .filter(emailAddress -> emailAddress.contains(y9WebMailProperties.getHost()))
            .collect(Collectors.toList());

        Map<String,
            String> emailAddressAndPersonIdMap = jamesUserService.findByEmailAddressIn(emailAddressList)
                .stream()
                .collect(Collectors.toMap(JamesUser::getEmailAddress, JamesUser::getPersonId));

        List<String> personIdList = new ArrayList<>(emailAddressAndPersonIdMap.values());
        Map<String,
            Person> idPersonMap = personApi.listByIds(Y9LoginUserHolder.getTenantId(), personIdList)
                .getData()
                .stream()
                .collect(Collectors.toMap(OrgUnit::getId, person -> person));

        for (EmailListDTO emailListDTO : emailReceiverDTOList) {
            String personId = emailAddressAndPersonIdMap.get(emailListDTO.getFrom());
            if (StringUtils.isNotBlank(personId)) {
                Person fromPerson = idPersonMap.get(personId);
                if (fromPerson != null) {
                    emailListDTO.setFromName(fromPerson.getName());
                    emailListDTO.setFromAvator(fromPerson.getAvator());
                }
            }
            for (ToDTO toDTO : emailListDTO.getToDTOList()) {
                String toPersonId = emailAddressAndPersonIdMap.get(toDTO.getTo());
                if (StringUtils.isNotBlank(toPersonId)) {
                    Person toPerson = idPersonMap.get(toPersonId);
                    if (toPerson != null) {
                        toDTO.setToName(toPerson.getName());
                        toDTO.setToAvator(toPerson.getAvator());
                    }
                }
            }
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
                    if (emailAddress.contains(y9WebMailProperties.getHost())) {
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
                    if (parser.getFrom().contains(y9WebMailProperties.getHost())) {
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
