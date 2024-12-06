package net.risesoft.service;

import java.io.IOException;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import net.risesoft.controller.dto.EmailAttachmentDTO;

public interface EmailAttachmentService {

    void download(String folderName, String messageId, String name, HttpServletResponse response,
        HttpServletRequest request) throws IOException, MessagingException;

    void batchDownload(String folderName, String messageId, HttpServletRequest request, HttpServletResponse response);

    EmailAttachmentDTO addAttachment(String folder, String messageId, MultipartFile file)
        throws MessagingException, IOException;

    void removeAttachment(String folder, String messageId, String filename) throws MessagingException, IOException;
}
