package net.risesoft.controller;

import java.io.InputStream;

import javax.validation.constraints.NotBlank;

import org.apache.james.mime4j.dom.Message;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.james.entity.ImportEml;
import net.risesoft.james.service.ImportEmlService;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController
@RequestMapping(value = "/api/rest/importEml", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
public class ImportEmlController {

    private final ImportEmlService importEmlService;

    /**
     * 获取详细信息
     *
     * @param id 部门id
     * @return
     */
    @RiseLog(moduleName = "电子邮件", operationName = "根据导入id，获取详细信息")
    @RequestMapping(value = "/getById")
    public Y9Result<ImportEml> getById(@RequestParam @NotBlank String id) {
        ImportEml eml = importEmlService.getById(id);
        return Y9Result.success(eml, "获取邮件EML详细信息成功");
    }

    @RiseLog(moduleName = "电子邮件", operationName = "上传邮件EML文件", operationType = OperationTypeEnum.ADD)
    @RequestMapping(value = "/importEml")
    public Y9Result<Object> importOrgTreeXls(@RequestParam MultipartFile file) {
        try (InputStream dataStream = file.getInputStream()) {
            Message message = Message.Builder.of(dataStream).build();
            importEmlService.importEmailByEml(message);
            return Y9Result.success();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return Y9Result.failure("上传失败:" + e.getMessage());
        }
    }

    @RiseLog(moduleName = "电子邮件", operationName = "分页获取邮件信息")
    @RequestMapping("/page")
    public Y9Page<ImportEml> page(String subject, String text, Y9PageQuery pageQuery) {
        Page<ImportEml> pageList = importEmlService.pageByPersonId(Y9LoginUserHolder.getPersonId(), pageQuery);
        return Y9Page.success(pageQuery.getPage(), pageList.getTotalPages(), pageList.getTotalElements(),
            pageList.getContent(), "获取数据成功");
    }
}
