package net.risesoft.service.impl;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sun.mail.imap.IMAPFolder;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.controller.dto.EmailAttachmentDTO;
import net.risesoft.james.service.JamesUserService;
import net.risesoft.service.EmailAttachmentService;
import net.risesoft.support.DefaultFolder;
import net.risesoft.support.EmailConst;
import net.risesoft.y9.configuration.app.y9webmail.Y9WebMailProperties;
import net.risesoft.y9.util.mime.ContentDispositionUtil;
import net.risesoft.y9.util.mime.MediaTypeUtil;

@Service
public class EmailAttachmentServiceImpl extends MailHelper implements EmailAttachmentService {

    private final ServletContext servletContext;

    public EmailAttachmentServiceImpl(
        Y9WebMailProperties y9WebMailProperties,
        JamesUserService jamesUserService,
        PersonApi personApi,
        ServletContext servletContext) {
        super(y9WebMailProperties, jamesUserService, personApi);
        this.servletContext = servletContext;
    }

    /**
     * 获取邮件中附件名集合
     *
     * @param originMimeMultipart 邮件体)
     * @return {@link Set}<{@link String}>
     * @throws MessagingException 通讯异常
     */
    private static Set<String> getFileNameSet(MimeMultipart originMimeMultipart) throws MessagingException {
        Set<String> fileNameSet = new HashSet<>();
        for (int i = 0; i < originMimeMultipart.getCount(); i++) {
            BodyPart bodyPart = originMimeMultipart.getBodyPart(i);
            if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                fileNameSet.add(bodyPart.getFileName());
            }
        }
        return fileNameSet;
    }

    @Override
    public void download(String folderName, String messageId, String name, HttpServletResponse response,
        HttpServletRequest request) throws IOException, MessagingException {
        OutputStream out = response.getOutputStream();
        response.reset();
        response.setHeader("Content-disposition", ContentDispositionUtil.standardizeAttachment(name));
        response.setContentType(MediaTypeUtil.getMediaTypeForFileName(servletContext, name).toString());

        Store store = createReceiveMailSession();
        Folder folder = store.getFolder(folderName);
        if (!folder.exists()) {
            folder = store.getFolder(DefaultFolder.MY_FOLDER.getName()).getFolder(folderName);
        }
        folder.open(Folder.READ_ONLY);
        Message message = getMessage((IMAPFolder)folder, messageId);

        MimeMultipart mimeMultipart = (MimeMultipart)message.getContent();
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            String disposition = bodyPart.getDisposition();
            if (StringUtils.isNotBlank(disposition) && disposition.equals(Part.ATTACHMENT)
                && name.equals(MimeUtility.decodeText(bodyPart.getFileName()))) {
                try (InputStream inputStream = bodyPart.getInputStream()) {
                    IOUtils.copy(inputStream, out, 1024);
                }
            }
        }
        out.flush();
        out.close();
        store.close();
    }

    @Override
    public void batchDownload(String folderName, String messageId, HttpServletRequest request,
        HttpServletResponse response) {
        DataOutputStream dataOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        OutputStream outputStream = null;
        String fileName = null;
        Store store = null;
        try {
            outputStream = response.getOutputStream();
            store = createReceiveMailSession();
            Folder folder = store.getFolder(folderName);
            if (!folder.exists()) {
                folder = store.getFolder(DefaultFolder.MY_FOLDER.getName()).getFolder(folderName);
            }
            folder.open(Folder.READ_ONLY);
            Message message = getMessage((IMAPFolder)folder, messageId);

            MimeMultipart mimeMultipart = (MimeMultipart)message.getContent();

            fileName = message.getSubject() + "_附件打包.zip";
            response.setContentType(MediaTypeUtil.getMediaTypeForFileName(servletContext, fileName).toString());
            response.setHeader("Content-disposition", ContentDispositionUtil.standardizeAttachment(fileName));
            // 设置压缩流：直接写入response，实现边压缩边下载
            zipOutputStream = new ZipOutputStream(new BufferedOutputStream(outputStream));
            zipOutputStream.setMethod(ZipOutputStream.DEFLATED);// 设置压缩方法

            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                if (StringUtils.isNotBlank(disposition) && disposition.equals(Part.ATTACHMENT)) {

                    try {
                        // 添加ZipEntry，并ZipEntry中写入文件流
                        // 这里，加上i是防止要下载的文件有重名的导致下载失败
                        zipOutputStream.putNextEntry(new ZipEntry(MimeUtility.decodeText(bodyPart.getFileName())));
                        dataOutputStream = new DataOutputStream(zipOutputStream);
                        try (InputStream is = bodyPart.getInputStream()) {
                            IOUtils.copy(is, dataOutputStream, 1024);
                        }
                        zipOutputStream.closeEntry();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.flush();
                    dataOutputStream.close();
                }
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
                if (zipOutputStream != null) {
                    zipOutputStream.close();
                }
                if (store != null) {
                    store.close();
                }
            } catch (IOException | MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public EmailAttachmentDTO addAttachment(String folder, String messageId, MultipartFile file)
        throws MessagingException, IOException {
        Store store = createReceiveMailSession();

        IMAPFolder draftsFolder = (IMAPFolder)store.getFolder(DefaultFolder.DRAFTS.getName());
        draftsFolder.open(Folder.READ_WRITE);

        MimeMessage originMessage = (MimeMessage)getMessage(draftsFolder, messageId);
        final String originMessageId = originMessage.getMessageID();
        MimeMessage message = new MimeMessage(originMessage) {
            @Override
            protected void updateMessageID() throws MessagingException {
                super.setHeader(EmailConst.HEADER_MESSAGE_ID, originMessageId);
            }
        };
        MimeMultipart mimeMultipart = (MimeMultipart)message.getContent();

        Set<String> fileNameSet = getFileNameSet(mimeMultipart);
        String fileName = getUniqueFileName(fileNameSet, file.getOriginalFilename());

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        ByteArrayDataSource dataSource =
            new ByteArrayDataSource(file.getBytes(), new MimetypesFileTypeMap().getContentType(fileName));
        dataSource.setName(fileName);
        DataHandler dataHandler = new DataHandler(dataSource);
        mimeBodyPart.setDataHandler(dataHandler);
        mimeBodyPart.setFileName(fileName);

        mimeMultipart.addBodyPart(mimeBodyPart);
        message.saveChanges();

        draftsFolder.setFlags(new Message[] {originMessage}, new Flags(Flags.Flag.DELETED), true);
        draftsFolder.addMessages(new Message[] {message});

        draftsFolder.close();
        store.close();

        return parseEmailAttachment(dataSource);
    }

    @Override
    public void removeAttachment(String folder, String messageId, String fileName)
        throws MessagingException, IOException {
        Store store = createReceiveMailSession();

        IMAPFolder draftsFolder = (IMAPFolder)store.getFolder(DefaultFolder.DRAFTS.getName());
        draftsFolder.open(Folder.READ_WRITE);

        MimeMessage originMessage = (MimeMessage)getMessage(draftsFolder, messageId);
        final String originMessageId = originMessage.getMessageID();
        MimeMessage message = new MimeMessage(originMessage) {
            @Override
            protected void updateMessageID() throws MessagingException {
                super.setHeader(EmailConst.HEADER_MESSAGE_ID, originMessageId);
            }
        };

        MimeMultipart originMimeMultipart = (MimeMultipart)message.getContent();
        for (int i = 0; i < originMimeMultipart.getCount(); i++) {
            BodyPart bodyPart = originMimeMultipart.getBodyPart(i);
            if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())
                && Objects.equals(fileName, bodyPart.getFileName())) {
                originMimeMultipart.removeBodyPart(i);
            }
        }

        message.saveChanges();

        draftsFolder.setFlags(new Message[] {originMessage}, new Flags(Flags.Flag.DELETED), true);
        draftsFolder.addMessages(new Message[] {message});

        draftsFolder.close();
        store.close();
    }

    /**
     * 获得跟给定的附件名集合不同的唯一的附件名
     *
     * @param fileNameSet 附件名集合
     * @param originalFilename 原始文件名
     * @return {@link String}
     */
    private String getUniqueFileName(Set<String> fileNameSet, String originalFilename) {
        String baseName = FilenameUtils.getBaseName(originalFilename);
        String extension = FilenameUtils.getExtension(originalFilename);
        int i = 1;
        while (fileNameSet.contains(originalFilename)) {
            originalFilename = String.format("%s(%d).%s", baseName, i++, extension);
        }
        return originalFilename;
    }

}
