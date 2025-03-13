package net.risesoft.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.MimetypesFileTypeMap;
import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.ContentType;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimePart;
import jakarta.mail.internet.ParseException;
import jakarta.mail.search.AndTerm;
import jakarta.mail.search.ComparisonTerm;
import jakarta.mail.search.OrTerm;
import jakarta.mail.search.ReceivedDateTerm;
import jakarta.mail.search.SearchTerm;
import jakarta.mail.search.SentDateTerm;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPMessage;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.controller.dto.EmailContactDTO;
import net.risesoft.controller.dto.EmailDTO;
import net.risesoft.controller.dto.EmailDetailDTO;
import net.risesoft.controller.dto.EmailListDTO;
import net.risesoft.controller.dto.EmailSearchDTO;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.james.entity.JamesAddressBook;
import net.risesoft.james.entity.term.MyAttachmentTerm;
import net.risesoft.james.entity.term.MyBodyTerm;
import net.risesoft.james.entity.term.MyFlagTerm;
import net.risesoft.james.entity.term.MyFormTerm;
import net.risesoft.james.entity.term.MyReceiverTerm;
import net.risesoft.james.entity.term.MySubjectTerm;
import net.risesoft.james.service.JamesAddressBookService;
import net.risesoft.james.service.JamesUserService;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.EmailService;
import net.risesoft.support.DefaultFolder;
import net.risesoft.support.EmailConst;
import net.risesoft.support.EmailErrorCodeEnum;
import net.risesoft.support.EmailThreadLocalHolder;
import net.risesoft.util.EmailUtil;
import net.risesoft.util.MimeMessageParser;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.y9webmail.Y9WebMailProperties;
import net.risesoft.y9.exception.Y9BusinessException;
import net.risesoft.y9.util.mime.ContentDispositionUtil;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

import jodd.mail.ReceiveMailSession;
import jodd.mail.SendMailSession;
import jodd.util.Base64;

@Service
public class EmailServiceImpl extends MailHelper implements EmailService {

    private final Y9FileStoreService y9FileStoreService;

    private final OrgUnitApi orgUnitApi;

    private final JamesAddressBookService jamesAddressBookService;

    private final JamesUserService jamesUserService;

    public EmailServiceImpl(Y9WebMailProperties y9WebMailProperties, JamesUserService jamesUserService,
        PersonApi personApi, Y9FileStoreService y9FileStoreService, OrgUnitApi orgUnitApi,
        JamesAddressBookService jamesAddressBookService, JamesUserService jamesUserService1) {
        super(y9WebMailProperties, jamesUserService, personApi);
        this.y9FileStoreService = y9FileStoreService;
        this.orgUnitApi = orgUnitApi;
        this.jamesAddressBookService = jamesAddressBookService;
        this.jamesUserService = jamesUserService1;
    }

    private static void setMailer(MimeMessage mimeMessage) throws MessagingException {
        mimeMessage.setHeader(EmailConst.HEADER_MAILER, "risesoft webmail");
    }

    private static EmailDTO buildReplyEmail(String folderName, EmailDTO email) {
        EmailDTO replyEmail = new EmailDTO();
        replyEmail.setFolder(folderName);
        replyEmail.setReplyMessageId(email.getMessageId());
        replyEmail.setRichText(EmailUtil.getReplyOrForwardContent(email)
            + (StringUtils.isNotBlank(email.getRichText()) ? email.getRichText() : ""));
        replyEmail.setSubject("回复：" + email.getSubject());
        return replyEmail;
    }

    public static boolean isHasAttachment(Message message) throws IOException, MessagingException {
        boolean hasAttachment = false;
        if (message.getContent().getClass().toString().trim().contains("MimeMultipart")) {
            MimeMultipart mmp = (MimeMultipart)message.getContent();
            int count = mmp.getCount();
            for (int j = 0; j < count; j++) {
                BodyPart bodyPart = mmp.getBodyPart(j);
                String disposition = bodyPart.getDisposition();
                if (null != disposition && disposition.equals(Part.ATTACHMENT)) {
                    hasAttachment = true;
                }
            }
        }
        return hasAttachment;
    }

    private static String getAttachmentSize(Message message) throws IOException, MessagingException {
        long attachmentSize = 0L;
        if (message.getContent().getClass().toString().trim().contains("MimeMultipart")) {
            MimeMultipart mmp = (MimeMultipart)message.getContent();
            int count = mmp.getCount();
            for (int j = 0; j < count; j++) {
                BodyPart bodyPart = mmp.getBodyPart(j);
                String disposition = bodyPart.getDisposition();
                if (null != disposition && disposition.equals(Part.ATTACHMENT)) {
                    InputStream inputStream = bodyPart.getInputStream();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int bytesRead = -1;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    attachmentSize += outputStream.toByteArray().length;
                    outputStream.close();
                }
            }
        }
        return (attachmentSize >> 10) + " KB";
    }

    private void addHtml(MimeMultipart mimeMultipart, String richText) throws MessagingException {
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(richText, "text/html;charset=UTF-8");
        mimeMultipart.addBodyPart(bodyPart);
    }

    private List<String> addHtmlTextReplaceCID(MimeMultipart alternativeMultipart, String richText)
        throws MessagingException {
        List<String> inlineIdList = new ArrayList<>();

        String patternStr = "<img\\s*([^>]*)\\s*src=\\\"(.*?)\\\"\\s*([^>]*)>";
        Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(richText);
        String result = richText;
        while (matcher.find()) {
            String src = matcher.group(2);
            int index = src.indexOf(Y9Context.getSystemName());
            if (index != -1) {
                try {
                    String fileName = src.substring(src.lastIndexOf("/") + 1);
                    String fileId = FilenameUtils.getBaseName(fileName);
                    String replaceSrc = "cid:" + fileId;
                    if (StringUtils.isNotBlank(replaceSrc)) {
                        result = result.replaceAll(src, replaceSrc);
                    }
                    inlineIdList.add(fileId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        MimeBodyPart htmlBodyPart = new MimeBodyPart();
        htmlBodyPart.setContent(result, "text/html;charset=UTF-8");
        alternativeMultipart.addBodyPart(htmlBodyPart);

        return inlineIdList;
    }

    private void addPictures(MimeMultipart mimeMultipart, List<String> cidList) throws MessagingException {
        if (cidList != null) {
            for (String fileId : cidList) {
                try {
                    MimeBodyPart mimeBodyPart = new MimeBodyPart();
                    byte[] bytes = y9FileStoreService.downloadFileToBytes(fileId);
                    Y9FileStore y9FileStore = y9FileStoreService.getById(fileId);
                    DataSource dataSource = new ByteArrayDataSource(bytes,
                        new MimetypesFileTypeMap().getContentType(y9FileStore.getFileName()));
                    DataHandler dataHandler = new DataHandler(dataSource);
                    mimeBodyPart.setDataHandler(dataHandler);
                    mimeBodyPart.setContentID("<" + fileId + ">");
                    mimeMultipart.addBodyPart(mimeBodyPart);

                    // 附件临时存入 y9FileStore 里，上传到邮件中后删除
                    y9FileStoreService.deleteFile(fileId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addText(MimeMultipart alternativeMultipart, String text) throws MessagingException {
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(text, "text/plain;charset=UTF-8");
        alternativeMultipart.addBodyPart(mimeBodyPart);
    }

    @Override
    public void delete(String folderName, long[] uids) throws MessagingException {
        move(uids, folderName, DefaultFolder.TRASH.getName());
    }

    @Override
    public void deletePermanently(String folderName, long[] uids) throws MessagingException {
        ReceiveMailSession sendMailSession = createReceiveMailSession();
        sendMailSession.open();
        IMAPFolder folder = (IMAPFolder)sendMailSession.getFolder(folderName);
        folder.open(Folder.READ_WRITE);
        Message[] messages = folder.getMessagesByUID(uids);
        folder.setFlags(messages, new Flags(Flags.Flag.DELETED), true);
        folder.close(true);
        sendMailSession.close();
    }

    @Override
    public EmailDetailDTO detail(String folderName, long uid) throws Exception {
        ReceiveMailSession receiveMailSession = createReceiveMailSession();
        EmailDetailDTO email = null;
        receiveMailSession.open();
        IMAPFolder folder = (IMAPFolder)receiveMailSession.getFolder(folderName);
        if (!folder.exists()) {
            folder = (IMAPFolder)receiveMailSession.getFolder(DefaultFolder.MY_FOLDER.getName()).getFolder(folderName);
        }
        if (folder.exists()) {
            folder.open(Folder.READ_WRITE);
            Message message = getMessageByUID(folder, uid);
            folder.setFlags(new Message[] {message}, new Flags(Flags.Flag.SEEN), true);

            email = parseToEmail((MimeMessage)message, EmailDetailDTO.class);
            email.setUid(uid);

            int messageNumber = message.getMessageNumber();
            // 这里注意，数值小的 messageNumber 为较老的邮件
            if (messageNumber > 1) {
                Message nextMessage = folder.getMessage(messageNumber - 1);
                email.setNextUid(folder.getUID(nextMessage));
            }
            if (messageNumber < folder.getMessageCount()) {
                Message previousMessage = folder.getMessage(messageNumber + 1);
                email.setPreviousUid(folder.getUID(previousMessage));
            }

            folder.close(true);
        }
        return email;
    }

    @Override
    public void exportEml(String folderName, long uid, HttpServletResponse response, HttpServletRequest request)
        throws IOException, MessagingException {
        ReceiveMailSession receiveMailSession = createReceiveMailSession();
        receiveMailSession.open();
        IMAPFolder folder = (IMAPFolder)receiveMailSession.getFolder(folderName);
        folder.open(Folder.READ_ONLY);
        Message message = getMessageByUID(folder, uid);
        String filename = message.getSubject() + ".eml";

        OutputStream out = response.getOutputStream();
        response.reset();
        response.setHeader("Content-disposition", ContentDispositionUtil.standardizeAttachment(filename));
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setContentType("application/octet-stream");

        message.writeTo(out);
        out.flush();
        out.close();
    }

    @Override
    public EmailDTO findByFolderAndUid(String folderName, long uid) throws Exception {
        ReceiveMailSession receiveMailSession = createReceiveMailSession();
        EmailDTO email = null;
        receiveMailSession.open();
        IMAPFolder folder = (IMAPFolder)receiveMailSession.getFolder(folderName);
        if (!folder.exists()) {
            folder = (IMAPFolder)receiveMailSession.getFolder(DefaultFolder.MY_FOLDER.getName()).getFolder(folderName);
        }
        if (folder.exists()) {
            folder.open(Folder.READ_ONLY);
            Message message = getMessageByUID(folder, uid);

            email = parseToEmail((MimeMessage)message, EmailDTO.class);

            folder.close();
        }
        return email;
    }

    private Message getMessageByUID(IMAPFolder folder, long uid) throws MessagingException {
        Message message = folder.getMessageByUID(uid);
        if (message == null) {
            throw new Y9BusinessException(EmailErrorCodeEnum.EMAIL_NOT_EXIST.getCode(),
                EmailErrorCodeEnum.EMAIL_NOT_EXIST.getDescription());
        }
        return message;
    }

    @Override
    public void flag(String folderName, long[] uids, boolean flagged) throws MessagingException {
        ReceiveMailSession receiveMailSession = createReceiveMailSession();
        receiveMailSession.open();
        IMAPFolder folder = (IMAPFolder)receiveMailSession.getFolder(folderName);

        if (folder.exists()) {
            folder.open(Folder.READ_WRITE);
            Message[] msgs = folder.getMessagesByUID(uids);
            folder.setFlags(msgs, new Flags(Flags.Flag.FLAGGED), flagged);
            folder.close(true);
        }

        receiveMailSession.close();
    }

    @Override
    public EmailDTO forward(String folderName, long uid) throws Exception {
        EmailDTO email = this.findByFolderAndUid(folderName, uid);

        EmailDTO forwardEmail = new EmailDTO();
        forwardEmail.setFolder(folderName);
        forwardEmail.setForwardMessageId(email.getMessageId());
        forwardEmail.setRichText(EmailUtil.getReplyOrForwardContent(email)
            + (StringUtils.isNotBlank(email.getRichText()) ? email.getRichText() : ""));
        forwardEmail.setSubject("转发：" + email.getSubject());
        forwardEmail.setEmailAttachmentDTOList(email.getEmailAttachmentDTOList());
        return forwardEmail;
    }

    @Override
    public int getCountByFolder(String folderName, boolean unRead) throws MessagingException {
        int receiveImapCount = 0;

        ReceiveMailSession receiveMailSession = createReceiveMailSession();
        receiveMailSession.open();
        IMAPFolder folder = (IMAPFolder)receiveMailSession.getFolder(folderName);
        if (!folder.exists()) {
            folder.create(Folder.HOLDS_MESSAGES);
        }
        folder.open(Folder.READ_ONLY);
        if (unRead) {
            receiveImapCount = folder.getUnreadMessageCount();
        } else {
            receiveImapCount = folder.getMessageCount();
        }
        folder.close();
        receiveMailSession.close();

        return receiveImapCount;
    }

    @Override
    public Map<String, Object> getTodoCount(String personId) throws MessagingException {
        Map<String, Object> data = new HashMap<>();

        int draftCount = getCountByFolder(DefaultFolder.DRAFTS.getName(), false);
        if (draftCount != 0) {
            data.put(DefaultFolder.DRAFTS.getName(), draftCount);
        }

        int receivedCount = getCountByFolder(DefaultFolder.INBOX.getName(), true);
        if (0 != receivedCount) {
            data.put(DefaultFolder.INBOX.getName(), receivedCount);
        }

        int sendCount = getCountByFolder(DefaultFolder.SENT.getName(), false);
        if (0 != sendCount) {
            data.put(DefaultFolder.SENT.getName(), sendCount);
        }
        int deletedCount = getCountByFolder(DefaultFolder.TRASH.getName(), false);
        if (0 != deletedCount) {
            data.put(DefaultFolder.TRASH.getName(), deletedCount);
        }
        return data;
    }

    @Override
    public Y9Page<EmailListDTO> listByFolder(String folderName, int page, int rows)
        throws MessagingException, IOException {
        ReceiveMailSession receiveMailSession = createReceiveMailSession();
        receiveMailSession.open();

        List<EmailListDTO> emailReceiverDTOList = new ArrayList<>();

        IMAPFolder folder = (IMAPFolder)receiveMailSession.getFolder(folderName);
        if (!folder.exists()) {
            folder = (IMAPFolder)receiveMailSession.getFolder(DefaultFolder.MY_FOLDER.getName()).getFolder(folderName);
        }
        int totalCount = 0;
        int totalPage = 0;
        if (folder.exists()) {
            folder.open(Folder.READ_ONLY);

            totalCount = folder.getMessageCount();
            totalPage = totalCount / rows + 1;
            int end = totalCount - (page - 1) * rows;
            int start = totalCount - page * rows + 1;
            if (start < 1) {
                start = 1;
            }
            Message[] messages = folder.getMessages(start, end);
            if (messages != null && messages.length > 0) {

                // 按照接收时间倒序排
                for (int i = messages.length - 1; i >= 0; i--) {
                    IMAPMessage imapMessage = (IMAPMessage)messages[i];
                    long uid = folder.getUID(imapMessage);
                    EmailListDTO eDTO = messageToEmailListDTO(messages[i], uid);
                    if (eDTO.getCreateTime() == null) {
                        eDTO.setCreateTime(messages[i].getReceivedDate());
                    }
                    emailReceiverDTOList.add(eDTO);
                }
                // 未读置顶
                if (!"Sent".equals(folderName)) {
                    emailReceiverDTOList = emailReceiverDTOList.stream().sorted(EmailListDTO.getComparator())
                        .collect(java.util.stream.Collectors.toList());
                }
            }
        }
        getPersonData(folder, emailReceiverDTOList);
        folder.close(true);
        receiveMailSession.close();
        return Y9Page.success(page, totalPage, totalCount, emailReceiverDTOList);
    }

    @Override
    public void move(long[] uids, String originFolderName, String toFolderName) throws MessagingException {
        ReceiveMailSession receiveMailSession = createReceiveMailSession();
        receiveMailSession.open();
        IMAPFolder originFolder = (IMAPFolder)receiveMailSession.getFolder(originFolderName);
        if (!originFolder.exists()) {
            originFolder =
                (IMAPFolder)receiveMailSession.getFolder(DefaultFolder.MY_FOLDER.getName()).getFolder(originFolderName);
        }
        IMAPFolder toFolder = (IMAPFolder)receiveMailSession.getFolder(toFolderName);
        if (!toFolder.exists()) {
            toFolder.create(Folder.HOLDS_MESSAGES);
        }
        originFolder.open(Folder.READ_WRITE);
        toFolder.open(Folder.READ_WRITE);
        Message[] messages = originFolder.getMessagesByUID(uids);
        originFolder.copyMessages(messages, toFolder);
        originFolder.setFlags(messages, new Flags(Flags.Flag.DELETED), true);
        originFolder.close(true);
        toFolder.close(true);
        receiveMailSession.close();
    }

    @Override
    public void quickReply(String folderName, Long uid, String richText) throws Exception {
        EmailDTO replyEmail = this.reply(folderName, uid);
        replyEmail.setRichText(richText + replyEmail.getRichText());
        replyEmail.setText(richText);
        String messageId = this.save(replyEmail);
        this.send(messageId);
    }

    @Override
    public void read(String folderName, long[] uids, Boolean isRead) throws Exception {
        ReceiveMailSession receiveMailSession = createReceiveMailSession();
        receiveMailSession.open();
        IMAPFolder folder = (IMAPFolder)receiveMailSession.getFolder(folderName);

        if (folder.exists()) {
            folder.open(Folder.READ_WRITE);
            Message[] msgs = folder.getMessagesByUID(uids);
            folder.setFlags(msgs, new Flags(Flags.Flag.SEEN), isRead);
            folder.close(true);
        }

        receiveMailSession.close();
    }

    @Override
    public EmailDTO reply(String folderName, Long uid) throws Exception {
        EmailDTO email = this.findByFolderAndUid(folderName, uid);
        EmailDTO replyEmail = buildReplyEmail(folderName, email);

        replyEmail.setToEmailAddressList(Collections.singletonList(email.getFrom()));
        return replyEmail;
    }

    @Override
    public EmailDTO replyAll(String folderName, Long uid) throws Exception {
        EmailDTO email = this.findByFolderAndUid(folderName, uid);
        EmailDTO replyEmail = buildReplyEmail(folderName, email);

        List<String> toEmailAddressList = new ArrayList<>();
        toEmailAddressList.add(email.getFrom());
        toEmailAddressList.addAll(email.getToEmailAddressList());
        toEmailAddressList.addAll(email.getCcEmailAddressList());
        toEmailAddressList.remove(EmailThreadLocalHolder.getEmailAddress());

        replyEmail.setToEmailAddressList(toEmailAddressList);
        return replyEmail;
    }

    @Override
    public String save(EmailDTO email) throws Exception {
        ReceiveMailSession receiveMailSession = createReceiveMailSession();
        receiveMailSession.open();
        Session session = receiveMailSession.getSession();

        IMAPFolder draftsFolder = (IMAPFolder)receiveMailSession.getFolder(DefaultFolder.DRAFTS.getName());
        draftsFolder.open(Folder.READ_WRITE);

        IMAPFolder folder = null;

        MimeMessage mimeMessage;
        if (StringUtils.isNotBlank(email.getReplyMessageId())) {
            // 新建的回复邮件
            folder = (IMAPFolder)receiveMailSession.getFolder(email.getFolder());
            if (!folder.exists()) {
                folder = (IMAPFolder)receiveMailSession.getFolder(DefaultFolder.MY_FOLDER.getName())
                    .getFolder(email.getFolder());
            }
            folder.open(Folder.READ_WRITE);

            Message originMessage = getMessage(folder, email.getReplyMessageId());
            mimeMessage = (MimeMessage)originMessage.reply(false);
            mimeMessage.setReplyTo(originMessage.getReplyTo());
            setBody(mimeMessage, email);
        } else if (StringUtils.isNotBlank(email.getForwardMessageId())) {
            // 新建的转发邮件
            folder = (IMAPFolder)receiveMailSession.getFolder(email.getFolder());
            if (!folder.exists()) {
                folder = (IMAPFolder)receiveMailSession.getFolder(DefaultFolder.MY_FOLDER.getName())
                    .getFolder(email.getFolder());
            }
            folder.open(Folder.READ_WRITE);

            MimeMessage originMessage = (MimeMessage)getMessage(folder, email.getForwardMessageId());
            mimeMessage = new MimeMessage(originMessage) {
                @Override
                protected void updateMessageID() throws MessagingException {
                    super.setHeader(EmailConst.HEADER_MESSAGE_ID, generateUniqueMessageId());
                }
            };
            setAlternativeContent(mimeMessage, email.getText(), email.getRichText());
        } else if (StringUtils.isNotBlank(email.getMessageId())) {
            // 修改
            MimeMessage originMessage = (MimeMessage)getMessage(draftsFolder, email.getMessageId());
            final String originMessageId = originMessage.getMessageID();
            mimeMessage = new MimeMessage(originMessage) {
                @Override
                protected void updateMessageID() throws MessagingException {
                    super.setHeader(EmailConst.HEADER_MESSAGE_ID, originMessageId);
                }
            };
            setAlternativeContent(mimeMessage, email.getText(), email.getRichText());
            draftsFolder.setFlags(new Message[] {originMessage}, new Flags(Flags.Flag.DELETED), true);
        } else {
            // 普通新建
            mimeMessage = new MimeMessage(session) {
                @Override
                protected void updateMessageID() throws MessagingException {
                    super.setHeader(EmailConst.HEADER_MESSAGE_ID, generateUniqueMessageId());
                }
            };
            setBody(mimeMessage, email);
            setMailer(mimeMessage);
            // draftsFolder.setFlags(new Message[]{mimeMessage}, new Flags(Flags.Flag.DRAFT), true);
        }

        setSubject(mimeMessage, email.getSubject());
        setFrom(mimeMessage);
        setRecipients(mimeMessage, email.getToEmailAddressList(), email.getCcEmailAddressList(),
            email.getBccEmailAddressList());

        mimeMessage.saveChanges();

        draftsFolder.appendMessages(new Message[] {mimeMessage});
        draftsFolder.close(true);

        if (folder != null) {
            folder.close();
        }

        receiveMailSession.close();
        return mimeMessage.getMessageID();
    }

    @Override
    public Y9Page<EmailListDTO> search(EmailSearchDTO searchDTO, int page, int size)
        throws MessagingException, IOException {
        ReceiveMailSession receiveMailSession = createReceiveMailSession();
        receiveMailSession.open();
        List<EmailListDTO> emailListDTOList = new ArrayList<>();

        SearchTerm searchTerm = buildSearchTerm(searchDTO);

        int totalCount = 0;
        int totalPage = 0;
        if (StringUtils.isNotBlank(searchDTO.getFolder())) {
            IMAPFolder folder = (IMAPFolder)receiveMailSession.getFolder(searchDTO.getFolder());
            if (folder.exists()) {
                folder.open(Folder.READ_WRITE);
                Message[] messages = folder.search(searchTerm);
                for (Message message : messages) {
                    long uid = folder.getUID(message);
                    EmailListDTO eDTO = messageToEmailListDTO(message, uid);
                    if (eDTO.getCreateTime() == null) {
                        eDTO.setCreateTime(message.getReceivedDate());
                    }
                    emailListDTOList.add(eDTO);
                }
                getPersonData(folder, emailListDTOList);
                folder.close(true);
            }

        } else {
            IMAPFolder[] folders = (IMAPFolder[])receiveMailSession.getService().getDefaultFolder().list("*");
            for (IMAPFolder folder : folders) {
                if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
                    folder.open(Folder.READ_ONLY);
                    Message[] messages = folder.search(searchTerm);
                    for (Message message : messages) {
                        long uid = folder.getUID(message);
                        EmailListDTO eDTO = messageToEmailListDTO(message, uid);
                        if (eDTO.getCreateTime() == null) {
                            eDTO.setCreateTime(message.getReceivedDate());
                        }
                        emailListDTOList.add(eDTO);
                    }
                    getPersonData(folder, emailListDTOList);
                    folder.close(true);
                }
            }
        }
        totalCount = emailListDTOList.size();
        totalPage = totalCount / size + 1;
        int end = page * size;
        int start = (page - 1) * size;
        if (end > totalCount) {
            end = totalCount;
        }
        // 按是否已读、时间倒序
        emailListDTOList = emailListDTOList.stream().sorted(EmailListDTO.getComparator()).collect(Collectors.toList());
        emailListDTOList = emailListDTOList.subList(start, end);

        receiveMailSession.close();

        return Y9Page.success(page, totalPage, totalCount, emailListDTOList);
    }

    @Override
    public int todoCount(String folder) {
        ReceiveMailSession receiveMailSession = createReceiveMailSession();
        receiveMailSession.open();
        IMAPFolder folder1 = (IMAPFolder)receiveMailSession.getFolder(folder);
        List<EmailListDTO> emailListDTOList = new ArrayList<>();
        EmailSearchDTO searchDTO = new EmailSearchDTO();
        searchDTO.setRead(false);
        searchDTO.setFolder(folder);
        SearchTerm searchTerm = buildSearchTerm(searchDTO);
        try {
            if (folder1.exists()) {
                folder1.open(Folder.READ_WRITE);
                Message[] messages = folder1.search(searchTerm);
                return messages.length;
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    private SearchTerm buildSearchTerm(EmailSearchDTO searchDTO) {
        List<SearchTerm> searchTermList = new ArrayList<>();

        // 主题搜索
        if (StringUtils.isNotBlank(searchDTO.getSubject())) {
            searchTermList.add(new MySubjectTerm(searchDTO.getSubject()));
        } else {
            // 为空 查所有
            searchTermList.add(new MySubjectTerm(""));
        }

        // 正文搜索
        if (StringUtils.isNotBlank(searchDTO.getText())) {
            searchTermList.add(new MyBodyTerm(searchDTO.getText()));
        }

        // 发件人搜索
        if (StringUtils.isNotBlank(searchDTO.getFrom())) {
            searchTermList.add(new MyFormTerm(searchDTO.getFrom()));
        }

        // 收件人搜索
        if (StringUtils.isNotBlank(searchDTO.getTo())) {
            searchTermList.add(new MyReceiverTerm(searchDTO.getTo()));
        }

        // 发送/接收时间搜索（开始时间）
        if (searchDTO.getStartDate() != null) {
            OrTerm startDateTerm =
                new OrTerm(new SearchTerm[] {new SentDateTerm(ComparisonTerm.GE, searchDTO.getStartDate()),
                    new ReceivedDateTerm(ComparisonTerm.GE, searchDTO.getStartDate())});
            searchTermList.add(startDateTerm);
        }

        // 发送/接收时间搜索（结束时间）
        if (searchDTO.getEndDate() != null) {
            OrTerm endDateTerm =
                new OrTerm(new SearchTerm[] {new SentDateTerm(ComparisonTerm.LE, searchDTO.getEndDate()),
                    new ReceivedDateTerm(ComparisonTerm.LE, searchDTO.getEndDate())});
            searchTermList.add(endDateTerm);
        }

        // 是否有附件搜索
        if (searchDTO.getAttachment() != null) {
            searchTermList.add(new MyAttachmentTerm("", searchDTO.getAttachment()));
        }

        // 是否已读搜索
        if (searchDTO.getRead() != null) {
            searchTermList.add(new MyFlagTerm(new Flags(Flags.Flag.SEEN), searchDTO.getRead()));
        }

        // 是否标记
        if (searchDTO.getFlagged() != null) {
            searchTermList.add(new MyFlagTerm(new Flags(Flags.Flag.FLAGGED), searchDTO.getFlagged()));
        }

        return new AndTerm(searchTermList.toArray(new SearchTerm[searchTermList.size()]));
    }

    @Override
    public void send(String messageId) throws MessagingException, IOException {
        ReceiveMailSession receiveMailSession = createReceiveMailSession();
        receiveMailSession.open();
        SendMailSession sendMailSession = createSendMailSession();
        sendMailSession.open();

        IMAPFolder draftsFolder = (IMAPFolder)receiveMailSession.getFolder(DefaultFolder.DRAFTS.getName());
        draftsFolder.open(Folder.READ_WRITE);

        MimeMessage originMessage = (MimeMessage)getMessage(draftsFolder, messageId);
        final String originMessageId = originMessage.getMessageID();
        MimeMessage message = new MimeMessage(originMessage) {
            @Override
            protected void updateMessageID() throws MessagingException {
                super.setHeader(EmailConst.HEADER_MESSAGE_ID, originMessageId);
            }
        };

        // 未发送之前的邮件正文中图片依赖文件系统生成的 url 进行展示
        // 所以发送时需将邮件正文中的内嵌资源（图片）替换成 <img src="cid:012F21FAF333E7B7973DFCF41C240E3C"> 的标准形式
        List<String> cidList = new ArrayList<>();
        setHtml4Send(message, cidList);
        addPictures((MimeMultipart)message.getContent(), cidList);
        message.saveChanges();

        Transport transport = sendMailSession.getService();
        transport.sendMessage(message, message.getAllRecipients());

        draftsFolder.setFlags(new Message[] {originMessage}, new Flags(Flags.Flag.DELETED), true);

        sendMailSession.close();
        draftsFolder.close(true);
        receiveMailSession.close();
    }

    @Override
    public List<EmailContactDTO> contactPerson() throws MessagingException, IOException {
        ReceiveMailSession receiveMailSession = createReceiveMailSession();
        receiveMailSession.open();
        List<EmailContactDTO> contactDTOList = new ArrayList<EmailContactDTO>();
        List<EmailListDTO> emailReceiverDTOList = new ArrayList<>();

        IMAPFolder folderSent = (IMAPFolder)receiveMailSession.getFolder("Sent");
        if (!folderSent.exists())
            folderSent = (IMAPFolder)receiveMailSession.getFolder(DefaultFolder.MY_FOLDER.getName()).getFolder("Sent");
        if (folderSent.exists()) {
            folderSent.open(Folder.READ_ONLY);
            Message[] messagesSent =
                folderSent.getMessages(1, folderSent.getMessageCount() <= 20 ? folderSent.getMessageCount() : 20);
            if (messagesSent != null && messagesSent.length > 0) {
                for (int i = messagesSent.length - 1; i >= 0; i--) {
                    IMAPMessage imapMessage = (IMAPMessage)messagesSent[i];
                    long uid = folderSent.getUID(imapMessage);
                    emailReceiverDTOList.add(messageToEmailListDTO(messagesSent[i], uid));
                }
            }
        }
        getEmailContactDTOList(folderSent, emailReceiverDTOList, contactDTOList);
        folderSent.close(true);
        emailReceiverDTOList = new ArrayList<>();
        IMAPFolder folderINBOX = (IMAPFolder)receiveMailSession.getFolder("INBOX");
        if (!folderINBOX.exists())
            folderINBOX =
                (IMAPFolder)receiveMailSession.getFolder(DefaultFolder.MY_FOLDER.getName()).getFolder("INBOX");
        if (folderINBOX.exists()) {
            folderINBOX.open(Folder.READ_ONLY);
            Message[] messagesINBOX =
                folderINBOX.getMessages(1, folderINBOX.getMessageCount() <= 20 ? folderINBOX.getMessageCount() : 20);
            if (messagesINBOX != null && messagesINBOX.length > 0) {
                for (int i = messagesINBOX.length - 1; i >= 0; i--) {
                    IMAPMessage imapMessage = (IMAPMessage)messagesINBOX[i];
                    long uid = folderINBOX.getUID(imapMessage);
                    emailReceiverDTOList.add(messageToEmailListDTO(messagesINBOX[i], uid));
                }
            }
        }
        getEmailContactDTOList(folderINBOX, emailReceiverDTOList, contactDTOList);
        folderINBOX.close(true);
        receiveMailSession.close();
        return contactDTOList;
    }

    private EmailListDTO messageToEmailListDTO(Message message, Long uid) throws IOException, MessagingException {
        EmailListDTO emailListDTO = new EmailListDTO();
        emailListDTO.setUid(uid);
        emailListDTO.setCreateTime(message.getSentDate());
        emailListDTO.setSubject(message.getSubject());
        emailListDTO.setRead(message.getFlags().contains(Flags.Flag.SEEN));
        emailListDTO.setFlagged(message.getFlags().contains(Flags.Flag.FLAGGED));
        emailListDTO.setEmailType(1);
        emailListDTO.setFolder(message.getFolder().getName());
        emailListDTO.setFolderStr(getFolderString(message));
        emailListDTO.setFromPersonName(getFromString(message));
        emailListDTO.setAttachment(isHasAttachment(message));
        emailListDTO.setAttachmentSize(getAttachmentSize(message));
        emailListDTO.setToPersonNames(getToString(message.getAllRecipients()));
        try {
            MimeMessage mm = (MimeMessage)message;
            MimeMessageParser parser = null;
            parser = new MimeMessageParser(mm).parse();
            if (parser != null)
                emailListDTO.setText(parser.getPlainContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return emailListDTO;
    }

    private String getFolderString(Message message) {
        String folder = message.getFolder().getName();
        return "INBOX".equals(folder) ? "收件箱"
            : "Sent".equals(folder) ? "已发送" : "Trash".equals(folder) ? "回收站" : "Drafts".equals(folder) ? "草稿箱" : folder;
    }

    private String getFromString(Message message) throws MessagingException {
        InternetAddress internetAddress = (InternetAddress)message.getFrom()[0];
        return internetAddress.getAddress().split("@")[0];
    }

    private String getToString(Address[] to) {
        String toPerson = "";
        if (null != to) {
            toPerson = Arrays.stream(to).map(address -> ((InternetAddress)address).getAddress().split("@")[0])
                .collect(Collectors.joining(";"));
        }
        return toPerson;
    }

    private <T extends EmailDTO> T parseToEmail(MimeMessage mimeMessage, Class<T> tClass) throws Exception {
        T email = tClass.getDeclaredConstructor().newInstance();
        MimeMessageParser parser = new MimeMessageParser(mimeMessage).parse();
        email.setFolder(mimeMessage.getFolder().getName());
        email.setMessageId(mimeMessage.getMessageID());
        email.setSubject(parser.getSubject());
        email.setFrom(parser.getFrom());
        email.setSendTime(mimeMessage.getSentDate());
        email.setRichText(getRichText(parser));
        email.setEmailAttachmentDTOList(parseEmailAttachmentList(parser.getAttachmentList()));
        email.setToEmailAddressList(getEmailAddressList(parser, Message.RecipientType.TO));
        email.setCcEmailAddressList(getEmailAddressList(parser, Message.RecipientType.CC));
        email.setBccEmailAddressList(getEmailAddressList(parser, Message.RecipientType.BCC));
        return email;
    }

    private String getRichText(MimeMessageParser parser) throws IOException {
        String htmlContent = parser.getHtmlContent();
        Collection<String> inlineImageContentIds = parser.getInlineContentIds();
        // Collection<String> inlineImageContentIds = parser.getInlineImageContentIds();
        for (String inlineImageContentId : inlineImageContentIds) {
            DataSource dataSource = parser.findInlineByCid(inlineImageContentId);
            // DataSource dataSource = parser.findInlineImageByCid(inlineImageContentId);
            byte[] bytes = IOUtils.toByteArray(dataSource.getInputStream());
            String imageContent = "data:image/png;base64," + Base64.encodeToString(bytes);
            htmlContent = htmlContent.replace("cid:" + inlineImageContentId, imageContent);
        }
        return htmlContent;
    }

    private List<String> getEmailAddressList(MimeMessageParser parser, Message.RecipientType recipientType)
        throws Exception {
        if (recipientType == Message.RecipientType.TO) {
            return parser.getTo().stream().map(address -> ((InternetAddress)address).getAddress())
                .collect(Collectors.toList());
        } else if (recipientType == Message.RecipientType.CC) {
            return parser.getCc().stream().map(address -> ((InternetAddress)address).getAddress())
                .collect(Collectors.toList());
        } else {
            return parser.getBcc().stream().map(address -> ((InternetAddress)address).getAddress())
                .collect(Collectors.toList());
        }
    }

    private String generateUniqueMessageId() {
        return String.format("<%s@%s>", Y9IdGenerator.genId(), y9WebMailProperties.getHost());
    }

    private MimeBodyPart getBaseBodyPart(final MimeMultipart mimeMultipart) throws MessagingException {
        final MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(mimeMultipart);
        return bodyPart;
    }

    private boolean isMimeType(final MimePart part, final String mimeType) throws MessagingException, IOException {
        try {
            final ContentType ct = new ContentType(part.getDataHandler().getContentType());
            return ct.match(mimeType);
        } catch (final ParseException ex) {
            return part.getContentType().equalsIgnoreCase(mimeType);
        }
    }

    private void setAlternativeContent(final MimePart part, String text, String html)
        throws MessagingException, IOException {
        if (isMimeType(part, "text/plain") && !Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
            part.setContent(text, "text/plain;charset=UTF-8");
        } else {
            if (isMimeType(part, "text/html") && !Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                part.setContent(html, "text/html;charset=UTF-8");
            } else {
                if (isMimeType(part, "multipart/*")) {
                    final Multipart mp = (Multipart)part.getContent();
                    final int count = mp.getCount();

                    for (int i = 0; i < count; i++) {
                        setAlternativeContent((MimeBodyPart)mp.getBodyPart(i), text, html);
                    }
                }
            }
        }
    }

    private void setBody(MimeMessage mimeMessage, EmailDTO email) throws Exception {
        MimeMultipart rootMimeMultipart = new MimeMultipart();

        MimeMultipart alternativeMultipart = new MimeMultipart(EmailConst.ALTERNATIVE);
        addHtml(alternativeMultipart, email.getRichText());
        addText(alternativeMultipart, email.getText());

        rootMimeMultipart.addBodyPart(getBaseBodyPart(alternativeMultipart));

        mimeMessage.setContent(rootMimeMultipart);
    }

    private void setFrom(MimeMessage mimeMessage) throws MessagingException, UnsupportedEncodingException {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        mimeMessage.setFrom(new InternetAddress(EmailThreadLocalHolder.getEmailAddress(), userInfo.getName(), "UTF-8"));
    }

    private void setHtml4Send(final MimePart part, List<String> cidList) throws MessagingException, IOException {
        if (isMimeType(part, "text/plain") && !Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {

        } else {
            if (isMimeType(part, "text/html") && !Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {

                String richText = (String)part.getContent();

                String patternStr = "<img\\s*([^>]*)\\s*src=\\\"(.*?)\\\"\\s*([^>]*)>";
                Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(richText);
                String result = richText;
                while (matcher.find()) {
                    String src = matcher.group(2);
                    int index = src.indexOf(Y9Context.getSystemName());
                    if (index != -1) {
                        try {
                            String fileName = src.substring(src.lastIndexOf("/") + 1);
                            String fileId = FilenameUtils.getBaseName(fileName);
                            String replaceSrc = "cid:" + fileId;
                            if (StringUtils.isNotBlank(replaceSrc)) {
                                result = result.replaceAll(src, replaceSrc);
                            }
                            cidList.add(fileId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                part.setContent(result, "text/html;charset=UTF-8");
            } else {
                if (isMimeType(part, "multipart/*")) {
                    final Multipart mp = (Multipart)part.getContent();
                    final int count = mp.getCount();

                    for (int i = 0; i < count; i++) {
                        setHtml4Send((MimeBodyPart)mp.getBodyPart(i), cidList);
                    }
                }
            }
        }
    }

    private void setRecipients(MimeMessage mimeMessage, List<String> toPersonIdList, List<String> ccPersonIdList,
        List<String> bccPersonIdList) throws MessagingException {
        setRecipients(mimeMessage, toPersonIdList, Message.RecipientType.TO);
        setRecipients(mimeMessage, ccPersonIdList, Message.RecipientType.CC);
        setRecipients(mimeMessage, bccPersonIdList, Message.RecipientType.BCC);
    }

    private void setRecipients(MimeMessage mimeMessage, List<String> toPersonIdList,
        Message.RecipientType recipientType) throws MessagingException {
        if (toPersonIdList != null) {
            int toSize = toPersonIdList.size();
            Address[] toAddress = new Address[toSize];
            for (int i = 0; i < toSize; i++) {
                toAddress[i] = new InternetAddress(toPersonIdList.get(i));
            }
            mimeMessage.setRecipients(recipientType, toAddress);
        }
    }

    private void setSubject(MimeMessage mimeMessage, String subject) throws MessagingException {
        mimeMessage.setSubject(subject);
    }

    @Override
    public Map<String, Object> addressRelevancy(String search) {
        Map<String, Object> map = new HashMap<String, Object>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        // 从组织架构获取
        List<OrgUnit> orgUnitList = orgUnitApi.treeSearch(tenantId, search, OrgTreeTypeEnum.TREE_TYPE_PERSON).getData();
        List<OrgUnit> orgUnits = new ArrayList<OrgUnit>();
        for (OrgUnit ou : orgUnitList) {
            if (OrgTypeEnum.PERSON.getEnName().equals(ou.getOrgType().getEnName())) {
                Person person = (Person)ou;
                String email = jamesUserService.getEmailAddressByPersonId(ou.getId());
                if (StringUtils.isEmpty(email)) {
                    email = "未注册邮箱";
                } else {
                    person.setEmail(email);
                }
                orgUnits.add(person);
            }
        }
        map.put("business", orgUnits);
        // 从个人通讯录获取
        List<JamesAddressBook> jamesAddressBookList = jamesAddressBookService.findSearch(search);
        map.put("personal", jamesAddressBookList);
        // 从最近联系人获取
        try {
            List<EmailContactDTO> emailContactDTOList = this.contactPerson();
            List<EmailContactDTO> emailContactDTOs = new ArrayList<>();
            for (EmailContactDTO ec : emailContactDTOList) {
                if ((StringUtils.isNotBlank(ec.getContactPerson()) && ec.getContactPerson().indexOf(search) != -1)
                    || (StringUtils.isNotBlank(ec.getContactPersonName())
                        && ec.getContactPersonName().indexOf(search) != -1)) {
                    emailContactDTOs.add(ec);
                }
            }
            map.put("recently", emailContactDTOs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    @Override
    public EmailDTO newEmail() {
        EmailDTO email = new EmailDTO();
        email.setRichText(EmailUtil.getSignature());
        return email;
    }
}
