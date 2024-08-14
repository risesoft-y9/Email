package net.risesoft.controller.vo;

import java.util.List;

import lombok.Data;

@Data
public class ImportEmlVO {

    /** 主键id */
    private String id;
    /** 人员ID */
    private String personId;
    /**
     * 消息ID
     */
    private String messageId;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 纯文本邮件内容
     */
    private String textContent;
    /**
     * 富文本邮件内容
     */
    private String htmlContent;
    /**
     * 发件人
     */
    private String from;
    /**
     * 收件人
     */
    private String to;
    /**
     * 抄送人
     */
    private String cc;
    /**
     * 密送人
     */
    private String bcc;
    /**
     * 邮件时间
     */
    private String dateTime;

    /** 是否有附件 */
    private Boolean existAttchMent;

    /** 附件列表 */
    private List<ImportEmlAttchMentVO> attchMentsList;

    /** 下一封 */
    private String nextUid;

    /** 上一封 */
    private String previousUid;
}
