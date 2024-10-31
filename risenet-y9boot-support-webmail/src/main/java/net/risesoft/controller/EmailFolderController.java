package net.risesoft.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.controller.dto.EmailFolderDTO;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.EmailFolderService;

@RestController(value = "standardEmailFolderController")
@RequestMapping(value = "/api/standard/emailFolder")
@RequiredArgsConstructor
public class EmailFolderController {

    private final EmailFolderService emailFolderService;

    /**
     * 新增或修改文件夹
     *
     * @param originFolderName 原始文件夹名称 （不为空时代表修改）
     * @param newFolderName 新文件夹名称
     * @return {@code Y9Result<Object>}
     */
    @PostMapping
    public Y9Result<Object> save(String originFolderName, String newFolderName) {
        emailFolderService.save(originFolderName, newFolderName);
        return Y9Result.success();
    }

    /**
     * 删除文件夹
     *
     * @param folder 文件夹
     * @return {@code Y9Result<Object>}
     */
    @PostMapping(value = "/delete")
    public Y9Result<Object> delete(String folder) {
        emailFolderService.delete(folder);
        return Y9Result.success();
    }

    /**
     * 默认文件夹列表
     * 
     * @return {@code Y9Result<List<}{@link EmailFolderDTO}{@code >>}
     */
    @GetMapping(value = "/defaultList")
    public Y9Result<List<EmailFolderDTO>> defaultList() {
        List<EmailFolderDTO> emailFolderList = emailFolderService.getDefaultFolderList();
        return Y9Result.success(emailFolderList);
    }

    /**
     * 自定义文件夹列表
     * 
     * @return {@code Y9Result<List<}{@link EmailFolderDTO}{@code >>}
     * @throws MessagingException 通讯异常
     */
    @GetMapping(value = "/customList")
    public Y9Result<List<EmailFolderDTO>> customList() throws MessagingException {
        List<EmailFolderDTO> emailFolderList = emailFolderService.list();
        return Y9Result.success(emailFolderList);
    }

    /**
     * 所有文件夹列表，包括默认文件夹和自定义文件夹列表
     *
     * @return {@code Y9Result<Map<String, Object>>}
     * @throws MessagingException 通讯异常
     */
    @GetMapping(value = "/allList")
    public Y9Result<Map<String, Object>> list() throws MessagingException {
        List<EmailFolderDTO> customFolderList = emailFolderService.list();
        List<EmailFolderDTO> defaultFolderList = emailFolderService.getDefaultFolderList();
        Map<String, Object> data = new HashMap<>();
        data.put("customFolderList", customFolderList);
        data.put("defaultFolderList", defaultFolderList);
        return Y9Result.success(data);
    }

}
