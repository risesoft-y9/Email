package net.risesoft.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.controller.dto.EmailContactDTO;
import net.risesoft.controller.dto.EmailDTO;
import net.risesoft.controller.dto.EmailDetailDTO;
import net.risesoft.controller.dto.EmailListDTO;
import net.risesoft.controller.dto.EmailSearchDTO;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.EmailService;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController(value = "standardEmailController")
@RequestMapping("/api/standard/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final PersonApi personApi;

    /**
     * 删除邮件
     *
     * @param uids 邮件 uid
     * @param folder 文件夹
     * @return {@code Y9Result<Object>}
     * @throws MessagingException 通讯异常
     */
    @RiseLog(operationName = "删除邮件", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/delete")
    public Y9Result<Object> delete(@RequestParam(value = "uids") long[] uids, String folder) throws MessagingException {
        emailService.delete(folder, uids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 永久删除邮件
     *
     * @param uids 邮件 uid
     * @param folder 文件夹
     * @return {@code Y9Result<Object>}
     * @throws MessagingException 通讯异常
     */
    @RiseLog(operationName = "永久删除邮件", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/deletePermanently")
    public Y9Result<Object> deletePermanently(@RequestParam(value = "uids") long[] uids, String folder)
        throws MessagingException {
        emailService.deletePermanently(folder, uids);
        return Y9Result.successMsg("永久删除成功");
    }

    /**
     * 邮件详情
     *
     * @param folder 文件夹
     * @param uid 邮件 uid
     * @return {@code Y9Result<}{@link EmailDetailDTO}{@code >}
     * @throws Exception 异常
     */
    @RiseLog(operationName = "邮件详情")
    @GetMapping(value = "/{folder}/{uid}")
    public Y9Result<EmailDetailDTO> detail(@PathVariable String folder, @PathVariable long uid) throws Exception {
        EmailDetailDTO email = emailService.detail(folder, uid);
        return Y9Result.success(email);
    }

    /**
     * 导出邮件 eml
     *
     * @param folder 文件夹
     * @param uid 邮件 uid
     * @param response 响应
     * @param request 请求
     * @throws MessagingException 通讯异常
     * @throws IOException IO异常
     */
    @RiseLog(operationName = "导出邮件eml", operationType = OperationTypeEnum.EXPORT)
    @GetMapping(value = "/exportEml")
    public void exportEml(String folder, int uid, HttpServletResponse response, HttpServletRequest request)
        throws MessagingException, IOException {
        emailService.exportEml(folder, uid, response, request);
    }

    /**
     * 邮件标星
     *
     * @param uids 邮件 uid
     * @param folder 文件夹
     * @param flagged 是否标星
     * @return {@code Y9Result<Object>}
     * @throws Exception 异常
     */
    @RiseLog(operationName = "邮件标星", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/flag")
    public Y9Result<Object> flag(@RequestParam(value = "uids") long[] uids, @RequestParam String folder,
        boolean flagged) throws Exception {
        emailService.flag(folder, uids, flagged);
        if (flagged)
            return Y9Result.successMsg("标星成功");
        else
            return Y9Result.successMsg("取消标星成功");
    }

    /**
     * 新建邮件
     *
     * @return {@code Y9Result<}{@link EmailDTO}{@code >}
     * @throws Exception 异常
     */
    @RiseLog(operationName = "新建邮件")
    @GetMapping(value = "/newEmail")
    public Y9Result<EmailDTO> newEmail() throws Exception {
        return Y9Result.success(emailService.newEmail());
    }

    /**
     * 转发邮件
     *
     * @param uid 消息 uid
     * @param folder 文件夹
     * @return {@code Y9Result<}{@link EmailDTO}{@code >}
     * @throws Exception 异常
     */
    @GetMapping(value = "/forward/{folder}/{uid}")
    public Y9Result<EmailDTO> forward(@PathVariable String folder, @PathVariable long uid) throws Exception {
        return Y9Result.success(emailService.forward(folder, uid));
    }

    /**
     * 根据文件夹分页查找邮件列表
     *
     * @param page 页数
     * @param size 每页数量
     * @param folder 文件夹
     * @return {@code Y9Page<}{@link EmailListDTO}{@code >}
     * @throws IOException IO异常
     * @throws MessagingException 通讯异常
     */
    @GetMapping(value = "/list")
    public Y9Page<EmailListDTO> list(int page, @RequestParam int size, @RequestParam(required = false) String folder)
        throws IOException, MessagingException {
        return emailService.listByFolder(folder, page, size);
    }

    /**
     * 移动邮件
     *
     * @param uids 邮件 uid
     * @param originFolder 原始文件夹
     * @param toFolder 移动至文件夹
     * @return {@code Y9Result<Object>}
     * @throws MessagingException 通讯异常
     */
    @PostMapping(value = "/move")
    public Y9Result<Object> move(@RequestParam(value = "uids") long[] uids, String originFolder, String toFolder)
        throws MessagingException {
        emailService.move(uids, originFolder, toFolder);
        return Y9Result.successMsg("移动成功");
    }

    /**
     * 标记为已读或未读
     *
     * @param uids 邮件 uid
     * @param folder 文件夹
     * @param isRead 是否已读
     * @return {@code Y9Result<Object>}
     * @throws Exception 异常信息
     */
    @RiseLog(operationName = "标记邮件已读或未读", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/read")
    public Y9Result<Object> read(@RequestParam(value = "uids") long[] uids, @RequestParam String folder,
        @RequestParam Boolean isRead) throws Exception {
        emailService.read(folder, uids, isRead);
        return Y9Result.success();
    }

    /**
     * 回复
     *
     * @param uid 邮件 uid
     * @param folder 文件夹
     * @return {@code Y9Result<}{@link EmailDTO}{@code >}
     * @throws Exception 异常消息
     */
    @RiseLog(operationName = "回复邮件", operationType = OperationTypeEnum.EVENT)
    @GetMapping(value = "/reply/{folder}/{uid}")
    public Y9Result<EmailDTO> reply(@PathVariable String folder, @PathVariable Long uid) throws Exception {
        return Y9Result.success(emailService.reply(folder, uid));
    }

    /**
     * 快速回复
     *
     * @param folder 文件夹
     * @param uid uid
     * @param richText 回复内容
     * @return {@code Y9Result<}{@link EmailDTO}{@code >}
     * @throws Exception 异常
     */
    @RiseLog(operationName = "快速回复邮件", operationType = OperationTypeEnum.EVENT)
    @PostMapping(value = "/quickReply/{folder}/{uid}")
    public Y9Result<EmailDTO> quickReply(@PathVariable String folder, @PathVariable Long uid,
        @RequestParam String richText) throws Exception {
        emailService.quickReply(folder, uid, richText);
        return Y9Result.successMsg("快捷回复成功");
    }

    /**
     * 回复所有收件人（不包括自己和密送的其他）
     *
     * @param uid 邮件 uid
     * @param folder 文件夹
     * @return {@code Y9Result<}{@link EmailDTO}{@code >}
     * @throws Exception 异常
     * @see Exception
     */
    @RiseLog(operationName = "回复所有收件人", operationType = OperationTypeEnum.EVENT)
    @GetMapping(value = "/replyAll/{folder}/{uid}")
    public Y9Result<EmailDTO> replyAll(@PathVariable String folder, @PathVariable Long uid) throws Exception {
        return Y9Result.success(emailService.replyAll(folder, uid));
    }

    /**
     * 保存邮件
     *
     * @param email 邮件
     * @return {@code Y9Result<String>}
     * @throws Exception 异常
     * @see Exception
     */
    @RiseLog(operationName = "保存邮件", operationType = OperationTypeEnum.ADD)
    @PostMapping
    public Y9Result<String> save(EmailDTO email) throws Exception {
        String messageId = emailService.save(email);
        return Y9Result.success(messageId, "保存成功");
    }

    /**
     * 搜索邮件
     *
     * @param searchDTO 搜索dto
     * @return {@code Y9Page<}{@link EmailListDTO}{@code >}
     * @throws MessagingException 接收异常
     * @throws IOException IO异常
     */
    @RiseLog(operationName = "搜索邮件")
    @GetMapping(value = "/search")
    public Y9Page<EmailListDTO> search(EmailSearchDTO searchDTO) throws MessagingException, IOException {
        return emailService.search(searchDTO, searchDTO.getPage(), searchDTO.getSize());
    }

    /**
     * 发送邮件
     *
     * @param messageId 消息id
     * @return {@code Y9Result<Object>}
     * @throws Exception 异常
     */
    @RiseLog(operationName = "发送邮件", operationType = OperationTypeEnum.SEND)
    @PostMapping(value = "/send")
    public Y9Result<Object> send(String messageId) throws Exception {
        emailService.send(messageId);
        return Y9Result.successMsg("发送成功");
    }

    /**
     * 待办数量列表
     *
     * @return {@code Y9Result<Object>}
     * @throws MessagingException 通讯异常
     */
    @RiseLog(operationName = "获取待办数量列表")
    @GetMapping(value = "/todoList")
    public Y9Result<Object> todoList() throws MessagingException {
        String personId = Y9LoginUserHolder.getUserInfo().getPersonId();
        Map<String, Object> todoList = emailService.getTodoCount(personId);
        return Y9Result.success(todoList);
    }

    /**
     * 最近联系人列表
     *
     * @return {@code Y9Result<Object>}
     * @throws MessagingException 接收异常
     * @throws IOException IO异常
     */
    @RiseLog(operationName = "获取最近联系人列表")
    @ResponseBody
    @RequestMapping(value = "/contact")
    public Y9Result<Object> contactPerson() throws MessagingException, IOException {
        List<EmailContactDTO> contactPerson = emailService.contactPerson();
        return Y9Result.success(contactPerson);
    }

    /**
     * 邮件地址/姓名 关联
     * 
     * @param search 邮件地址/姓名
     * 
     * @return {@code Y9Result<Object>}
     */
    @RiseLog(operationName = "邮件地址/姓名关联")
    @GetMapping(value = "/addressRelevancy")
    public Y9Result<Object> addressRelevancy(String search) {
        return Y9Result.success(emailService.addressRelevancy(search));
    }
}
