package net.risesoft.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.risesoft.controller.dto.EmailContactDTO;
import net.risesoft.controller.dto.EmailDTO;
import net.risesoft.controller.dto.EmailDetailDTO;
import net.risesoft.controller.dto.EmailListDTO;
import net.risesoft.controller.dto.EmailSearchDTO;
import net.risesoft.pojo.Y9Page;

public interface EmailService {

    void delete(String folderName, long[] uids) throws MessagingException;

    void deletePermanently(String folderName, long[] uids) throws MessagingException;

    EmailDetailDTO detail(String folder, long uid) throws Exception;

    void exportEml(String folderName, long uid, HttpServletResponse response, HttpServletRequest request)
            throws IOException, MessagingException;

    EmailDTO findByFolderAndUid(String folderName, long uid) throws Exception;

    void flag(String folderName, long[] uids, boolean flagged) throws MessagingException;

    EmailDTO forward(String folderName, long uid) throws Exception;

    int getCountByFolder(String type, boolean unRead) throws MessagingException;

    Map<String, Object> getTodoCount(String personId) throws MessagingException;

    Y9Page<EmailListDTO> listByFolder(String folderName, int page, int rows) throws MessagingException, IOException;

    void move(long[] uids, String originFolderName, String toFolderName) throws MessagingException;

    void quickReply(String folderName, Long uid, String richText) throws Exception;

    void read(String folderName, long[] uids, Boolean isRead) throws Exception;

    EmailDTO reply(String folderName, Long uid) throws Exception;

    EmailDTO replyAll(String folderName, Long uid) throws Exception;

    String save(EmailDTO email) throws Exception;

    Y9Page<EmailListDTO> search(EmailSearchDTO searchDTO, int page, int size) throws MessagingException, IOException;

    int todoCount(String folder);

    void send(String messageId) throws MessagingException, IOException;

    List<EmailContactDTO> contactPerson() throws MessagingException, IOException;

    Map<String, Object> addressRelevancy(String search);

}
