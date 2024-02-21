package net.risesoft.controller;

import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.controller.dto.EmailAttachmentDTO;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.EmailAttachmentService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

@RestController(value = "standardEmailAttachmentController")
@RequestMapping(value = "/api/standard/emailAttachment")
public class EmailAttachmentController {

    @Autowired
    private EmailAttachmentService emailAttachmentService;

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    @Autowired
    private Y9Properties y9Properties;

    @PostMapping
    public Y9Result<EmailAttachmentDTO> addAttachment(String folder, String messageId, MultipartFile file)
        throws Exception {
        EmailAttachmentDTO emailAttachmentDTO = emailAttachmentService.addAttachment(folder, messageId, file);
        return Y9Result.success(emailAttachmentDTO);
    }

    /**
     * 批量下载文件
     *
     * @param folder 文件夹
     * @param messageId 消息id
     * @param request 请求
     * @param response 响应
     */
    @RequestMapping(value = "/batchDownload")
    public void batchDownload(String folder, String messageId, HttpServletRequest request,
        HttpServletResponse response) {
        emailAttachmentService.batchDownload(folder, messageId, request, response);
    }

    /**
     * 删除附件
     *
     * @param folder 文件夹
     * @param messageId 消息id
     * @param fileName 文件名称
     * @return {@link Y9Result}<{@link Object}>
     * @throws Exception 异常
     */
    @DeleteMapping
    public Y9Result<Object> deleteAttachment(String folder, String messageId, String fileName) throws Exception {
        emailAttachmentService.removeAttachment(folder, messageId, fileName);
        return Y9Result.success();
    }

    /**
     * 附件下载
     *
     * @param folder 文件夹
     * @param messageId 消息id
     * @param fileName 文件名
     * @param response 响应
     * @param request 请求
     */
    @RequestMapping(value = "/download")
    public void download(String folder, String messageId, String fileName, HttpServletResponse response,
        HttpServletRequest request) {
        try {
            emailAttachmentService.download(folder, messageId, fileName, response, request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return {@link Y9Result}<{@link String}>
     * @throws Exception 异常
     */
    @PostMapping("/uploadFile")
    public Y9Result<String> uploadFile(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String fileName = FilenameUtils.getName(originalFilename);
        LocalDate localDate = LocalDate.now();
        String fullPath = Y9FileStore.buildFullPath(Y9Context.getSystemName(), String.valueOf(localDate.getYear()),
            String.valueOf(localDate.getMonth().getValue()), String.valueOf(localDate.getDayOfMonth()));
        Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);
        String webmailBaseUrl = y9Properties.getCommon().getWebmailBaseUrl();
        return Y9Result.success(webmailBaseUrl + "/s/" + y9FileStore.getRealFileName());
    }

}
