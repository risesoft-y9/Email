package net.risesoft.james.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "JAMES_IMPORT_EML")
@Comment("历史邮件详细信息" )
public class ImportEml implements Serializable {

    private static final long serialVersionUID = -3212100925052119103L;

    /** 主键id */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键id")
    private String id;

    @Column(name = "PERSON_ID", length = 38, nullable = false)
    @Comment("人员ID")
    private String personId;

    /**
     * 消息ID
     */
    @Column(name = "MESSAGE_ID", length = 100)
    @Comment("消息ID")
    private String messageId;

    /**
     * 邮件主题
     */
    @Column(name = "SUBJECT", length = 1000)
    @Comment("邮件主题")
    private String subject;

    /**
     * 纯文本邮件内容
     */
    @Lob
    @Column(name = "TEXT_CONTENT", nullable = true)
    @Comment("纯文本邮件内容")
    private String textContent;

    /**
     * 富文本邮件内容
     */
    @Lob
    @Column(name = "HTML_CONTENT", nullable = true)
    @Comment("富文本邮件内容")
    private String htmlContent;

    /**
     * 发件人
     */
    @Column(name = "EMAIL_FORM", length = 500)
    @Comment("发件人")
    private String from;

    /**
     * 收件人
     */
    @Column(name = "EMAIL_TO", length = 2000)
    @Comment("收件人")
    private String to;

    /**
     * 抄送人
     */
    @Column(name = "EMAIL_CC", length = 1500)
    @Comment("抄送人")
    private String cc;

    /**
     * 密送人
     */
    @Column(name = "EMAIL_BCC", length = 1500)
    @Comment("密送人")
    private String bcc;

    /**
     * 邮件时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Comment("邮件时间")
    @Column(name = "DATE_TIME")
    private String dateTime;

    @Comment("是否有附件")
    @Column(name = "EXIST_ATTCHMENT")
    private Boolean existAttchMent;

}
