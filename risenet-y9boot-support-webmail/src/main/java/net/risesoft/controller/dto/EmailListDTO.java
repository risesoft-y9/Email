package net.risesoft.controller.dto;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮件列表
 */
@Data
@NoArgsConstructor
public class EmailListDTO implements Serializable {

    private static final long serialVersionUID = -7308732148669726883L;

    /**
     * IMAP UID，在一个账户中是唯一的
     */
    private Long uid;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 邮件纯文本正文
     */
    private String text;

    /**
     * 邮件发送人
     */
    private String fromPersonName;

    /**
     * 邮件收件人姓名
     */
    private String toPersonNames;

    /**
     * 是否有附件
     */
    private Boolean attachment;

    /**
     * 附件大小
     */
    private String attachmentSize;

    /**
     * 是否已读
     */
    private Boolean read;

    /**
     * 是否星标
     */
    private Boolean flagged;

    /**
     * 邮件 签收/阅读 时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date readTime;

    /**
     * 所在文件夹
     */
    private String folder;

    /**
     * 文件夹中文
     */
    private String folderStr;

    /**
     * 邮件类型: 1.正常 2.转发 3.回复
     */
    private Integer emailType;
    /**
     * 发件人(仅包含.@youshengyun.com的发送人)
     */
    private String from;
    /**
     * 发件人姓名(仅包含.@youshengyun.com的发送人)
     */
    private String fromName;
    /**
     * 发件人头像(仅包含.@youshengyun.com的发送人)
     */
    private String fromAvator;
    /**
     * 收件人信息列表(仅包含.@youshengyun.com的发送人)
     */
    private List<ToDTO> ToDTOList;

    public static Comparator<EmailListDTO> getComparator() {
        return Comparator.comparing(EmailListDTO::getRead)
            .thenComparing(EmailListDTO::getCreateTime, Comparator.reverseOrder());
    }

}
