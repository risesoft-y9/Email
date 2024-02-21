package net.risesoft.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.risesoft.controller.dto.*;
import net.risesoft.model.platform.Person;
import net.risesoft.support.EmailThreadLocalHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import net.risesoft.api.org.PersonApi;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.EmailService;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController(value = "standardEmailController")
@RequestMapping("/api/standard/email")
public class EmailController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private PersonApi personApi;

    /**
     * 删除邮件
     *
     * @param uids 邮件 uid
     * @param folder 文件夹
     * @return {@link Y9Result}<{@link Object}>
     * @throws MessagingException
     */
    @DeleteMapping
    public Y9Result<Object> delete(@RequestParam(value = "uids") long[] uids, String folder) throws MessagingException {
        emailService.delete(folder, uids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 永久删除邮件
     *
     * @param uids 邮件 uid
     * @param folder 文件夹
     * @return {@link Y9Result}<{@link Object}>
     * @throws MessagingException
     */
    @DeleteMapping(value = "/permanently")
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
     * @return {@link Y9Result}<{@link EmailDetailDTO}>
     * @throws Exception
     */
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
     * @throws MessagingException
     * @throws IOException
     */
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
     * @return {@link Y9Result}<{@link Object}>
     * @throws Exception
     */
    @PostMapping(value = "/flag")
    public Y9Result<Object> flag(@RequestParam(value = "uids") long[] uids, @RequestParam String folder,
        boolean flagged) throws Exception {
        emailService.flag(folder, uids, flagged);
        if(flagged) return Y9Result.successMsg("标星成功");
        else return Y9Result.successMsg("取消标星成功");
    }

    /**
     * 转发邮件
     *
     * @param uid 消息 uid
     * @param folder 文件夹
     * @return {@link Y9Result}<{@link EmailDTO}>
     * @throws Exception
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
     * @return {@link Y9Page}<{@link EmailListDTO}>
     * @throws IOException
     * @throws MessagingException
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
     * @return {@link Y9Result}<{@link Object}>
     * @throws MessagingException
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
     * @return {@link Y9Result}<{@link Object}>
     * @throws Exception
     */
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
     * @return {@link Y9Result}<{@link EmailDTO}>
     * @throws Exception
     */
    @GetMapping(value = "/reply/{folder}/{uid}")
    public Y9Result<EmailDTO> reply(@PathVariable String folder, @PathVariable Long uid) throws Exception {
        return Y9Result.success(emailService.reply(folder, uid));
    }

    /**
     * 快速回复
     *
     * @param folder 文件夹
     * @param uid uid
     * @return {@link Y9Result}<{@link EmailDTO}>
     * @throws Exception 异常
     */
    @PostMapping(value = "/quickReply/{folder}/{uid}")
    public Y9Result<EmailDTO> quickReply(@PathVariable String folder, @PathVariable Long uid,
        @RequestParam String richText) throws Exception {
        emailService.quickReply(folder, uid, richText);
        return Y9Result.successMsg("快捷回复成功");
    }

    /**
     * 回复不包括自己和密送的其他所有收件人
     *
     * @param uid 邮件 uid
     * @param folder 文件夹
     * @return {@link Y9Result}<{@link EmailDTO}>
     * @throws Exception
     */
    @GetMapping(value = "/replyAll/{folder}/{uid}")
    public Y9Result<EmailDTO> replyAll(@PathVariable String folder, @PathVariable Long uid) throws Exception {
        return Y9Result.success(emailService.replyAll(folder, uid));
    }

    /**
     * 保存邮件
     *
     * @param email 邮件
     * @return {@link Y9Result}<{@link String}>
     * @throws Exception
     */
    @PostMapping
    public Y9Result<String> save(EmailDTO email) throws Exception {
        String messageId = emailService.save(email);
        return Y9Result.success(messageId, "保存成功");
    }

    /**
     * 搜索邮件
     *
     * @param searchDTO 搜索dto
     * @return {@link List}<{@link EmailListDTO}>
     * @throws MessagingException
     * @throws IOException
     */
    @GetMapping(value = "/search")
    public Y9Page<EmailListDTO> search(EmailSearchDTO searchDTO) throws MessagingException, IOException {
        return emailService.search(searchDTO, searchDTO.getPage(), searchDTO.getSize());
    }

    /**
     * 发送邮件
     *
     * @param messageId 消息id
     * @return {@link Y9Result}<{@link Object}>
     * @throws Exception
     */
    @PostMapping(value = "/send")
    public Y9Result<Object> send(String messageId) throws Exception {
        emailService.send(messageId);
        return Y9Result.successMsg("发送成功");
    }

    /**
     * 待办数量列表
     *
     * @return {@link Y9Result}<{@link Object}>
     * @throws MessagingException
     */
    @GetMapping(value = "/todoList")
    public Y9Result<Object> todoList() throws MessagingException {
        String personId = Y9LoginUserHolder.getUserInfo().getPersonId();
        Map<String, Object> todoList = emailService.getTodoCount(personId);
        return Y9Result.success(todoList);
    }

    /**
     * 最近联系人列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/contact")
    public Y9Result<Object> contactPerson() throws MessagingException, IOException{
        List<EmailContactDTO> contactPerson = emailService.contactPerson();
        return Y9Result.success(contactPerson);
    }

    /**
     * 邮件地址/姓名 关联
     * search 邮件地址/姓名
     * @return
     */
    @GetMapping(value = "/addressRelevancy")
    public Y9Result<Object> addressRelevancy(String search) {
        return Y9Result.success(emailService.addressRelevancy(search));
    }
}
