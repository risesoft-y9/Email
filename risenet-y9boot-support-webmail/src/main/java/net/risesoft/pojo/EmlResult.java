package net.risesoft.pojo;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.james.mime4j.dom.Message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;

import lombok.Data;

/**
 * Eml文件解析数据对象
 *
 * @author chendd
 * @date 2023/2/11 21:40
 */
@Data
@JsonIgnoreProperties(value = {"message"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmlResult {

    /**
     * 原始message对象
     */
    private Message message;

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
     * 邮件附件
     */
    private List<MutableTriple<String, Long, InputStream>> attachments = Lists.newArrayList();

    /**
     * 发件人
     */
    private String from;

    /**
     * 收件人
     */
    private List<Pair<String, String>> to;

    /**
     * 抄送人
     */
    private List<Pair<String, String>> cc;

    /**
     * 密送人
     */
    private List<Pair<String, String>> bcc;

    /**
     * 邮件时间
     */
    private String dateTime;

}
