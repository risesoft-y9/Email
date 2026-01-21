package net.risesoft.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.stream.MimeConfig;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.controller.vo.ImportEmlAttchMentVO;
import net.risesoft.controller.vo.ImportEmlVO;
import net.risesoft.james.entity.ImportEml;
import net.risesoft.james.entity.ImportEmlAttchMents;
import net.risesoft.james.service.ImportEmlAttchMentsService;
import net.risesoft.james.service.ImportEmlService;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.mime.ContentDispositionUtil;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * 导入邮件EML
 *
 * @author mengjuhua
 *
 */
@RestController
@RequestMapping(value = "/api/rest/importEml", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
public class ImportEmlController {

    private final ImportEmlService importEmlService;

    private final ImportEmlAttchMentsService importEmlAttchMentsService;
    private final Y9FileStoreService y9FileStoreService;

    /**
     * 压缩附件
     *
     * @param zipOut 压缩流
     * @param baseDir 目录
     * @param attachment 附件信息
     * @throws Exception
     */
    private void compress(ZipOutputStream zipOut, String baseDir, ImportEmlAttchMents attachment) throws Exception {
        String zipEntryName = baseDir + File.separator + attachment.getFileName();
        byte[] be = y9FileStoreService.downloadFileToBytes(attachment.getFileStoreId());
        try (InputStream is = new ByteArrayInputStream(be)) {
            // 添加ZipEntry，并ZipEntry中写入文件流
            // 这里，加上i是防止要下载的文件有重名的导致下载失败
            zipOut.putNextEntry(new ZipEntry(zipEntryName));
            byte[] b = new byte[100];
            int length = 0;
            while ((length = is.read(b)) != -1) {
                zipOut.write(b, 0, length);
            }
            zipOut.flush();
            zipOut.closeEntry();
        } catch (Exception e) {
            LOGGER.error("压缩文件失败！", e);
        }
    }

    /**
     * 批量删除导入信息
     *
     * @param ids 导入邮件id列表
     * @return {@code Y9Result<Object>}
     */

    @RiseLog(operationName = "批量删除导入信息")
    @PostMapping(value = "/deleteEml")
    public Y9Result<Object> deleteEml(@RequestParam @NotBlank List<String> ids) {
        importEmlService.delete(ids);
        return Y9Result.successMsg("删除邮件EML详细信息成功");
    }

    /**
     * 附件下载
     *
     * @param attId 附件id
     * @param response 响应
     */
    @RiseLog(operationName = "下载附件", operationType = OperationTypeEnum.DOWNLOAD)
    @RequestMapping(value = "/download")
    public void download(String attId, HttpServletResponse response) {
        ImportEmlAttchMents attachment = importEmlAttchMentsService.getById(attId);
        if (attachment == null) {
            LOGGER.warn("该附件不存在！");
        }
        String fileName = attachment.getFileName();
        response.reset();
        response.setHeader("Content-disposition", ContentDispositionUtil.standardizeAttachment(fileName));
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setContentType("application/octet-stream");
        try (ServletOutputStream out = response.getOutputStream()) {
            y9FileStoreService.downloadFileToOutputStream(attachment.getFileStoreId(), out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量下载附件
     *
     * @param importEmlId 导入邮件id
     * @param response 响应
     * @param request 请求
     */
    @RiseLog(operationName = "批量下载附件", operationType = OperationTypeEnum.DOWNLOAD)
    @RequestMapping(value = "/batchDownload")
    public void download4Batch(String importEmlId, HttpServletResponse response, HttpServletRequest request) {
        List<ImportEmlAttchMents> attachmentList = importEmlAttchMentsService.listByImportEmlId(importEmlId);
        if (attachmentList.isEmpty()) {
            LOGGER.warn("该附件不存在！");
        }
        ImportEml eml = importEmlService.getById(importEmlId);
        String fileName = null;
        ZipOutputStream zipOutputStream = null;
        try {
            fileName = eml.getSubject() + "_附件打包.zip";
            response.setContentType("text/html;charset=UTF-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", ContentDispositionUtil.standardizeAttachment(fileName));
            // 设置压缩流：直接写入response，实现边压缩边下载
            zipOutputStream = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
            zipOutputStream.setMethod(ZipOutputStream.DEFLATED);// 设置压缩方法
            for (ImportEmlAttchMents attachment : attachmentList) {
                compress(zipOutputStream, eml.getSubject(), attachment);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                zipOutputStream.close();
            } catch (IOException e) {
                LOGGER.error("关闭压缩流失败！", e);
            }
        }
    }

    /**
     * 获取详细信息
     *
     * @param id 部门id
     * @return {@code Y9Result<ImportEmlVO>}
     */
    @RiseLog(operationName = "根据导入id，获取详细信息")
    @GetMapping(value = "/getById")
    public Y9Result<ImportEmlVO> getById(@RequestParam @NotBlank String id) {
        ImportEml eml = importEmlService.getById(id);
        ImportEmlVO emlVO = Y9ModelConvertUtil.convert(eml, ImportEmlVO.class);
        if (Boolean.TRUE.equals(eml.getExistAttchMent())) {
            List<ImportEmlAttchMents> attchMentsList = importEmlAttchMentsService.listByImportEmlId(id);
            emlVO.setAttchMentsList(Y9ModelConvertUtil.convert(attchMentsList, ImportEmlAttchMentVO.class));
        }
        List<String> ids = importEmlService.listIdsByPersonId(Y9LoginUserHolder.getPersonId());
        if (!ids.isEmpty()) {
            // 当前记录的下标
            int index = ids.indexOf(id);
            int len = ids.size();
            String nextId = "-1";
            String prevId = "-1";
            if (index > 0) {
                prevId = ids.get(index - 1);
            }
            if (index + 1 < len) {
                nextId = ids.get(index + 1);
            }
            String firstId = ids.get(0);
            String lastId = ids.get(ids.size() - 1);
            // 第一个值与当前值相同, 把第一个的值和上一个的值置空 (根据需求灵活变动)
            if (firstId.equals(id)) {
                prevId = "-1";
            }
            // 最后一个值与当前值相同, 把最后一个的值和下一个的值置空 (根据需求灵活变动)
            if (lastId.equals(id)) {
                nextId = "-1";
            }
            emlVO.setNextUid(nextId);
            emlVO.setPreviousUid(prevId);
        }
        return Y9Result.success(emlVO, "获取邮件EML详细信息成功");
    }

    /**
     * 上传邮件EML文件
     *
     * @param file EML文件
     * @return {@code Y9Result<Object>}
     */
    @RiseLog(operationName = "上传邮件EML文件", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/importEml")
    public Y9Result<Object> importOrgTreeXls(@RequestParam MultipartFile file) {
        try (InputStream dataStream = file.getInputStream()) {
            MimeConfig config = MimeConfig.PERMISSIVE;
            Message message = Message.Builder.of().use(config).parse(dataStream).build();
            importEmlService.importEmailByEml(message);
            return Y9Result.success();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return Y9Result.failure("上传失败:" + e.getMessage());
        }
    }

    /**
     * 分页获取邮件信息
     *
     * @param subject 邮件主题
     * @param text 邮件内容
     * @param pageQuery 分页信息
     * @return {@code Y9Page<ImportEml>}
     */
    @RiseLog(operationName = "分页获取邮件信息")
    @GetMapping("/page")
    public Y9Page<ImportEml> page(String subject, String text, Y9PageQuery pageQuery) {
        Page<ImportEml> pageList =
            importEmlService.pageSearch(Y9LoginUserHolder.getPersonId(), subject, text, pageQuery);
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotalElements(),
            pageList.getContent(), "获取数据成功");
    }
}
