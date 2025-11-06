package net.risesoft.controller.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailDTO implements Serializable {

    private static final long serialVersionUID = -3786840646717751241L;

    private String folder;

    /**
     * 邮件的唯一id
     */
    private String messageId;

    private String replyMessageId;

    private String forwardMessageId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;

    /**
     * 发件人
     */
    private String from;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 邮件正文纯文本
     */
    private String text;

    /**
     * 邮件正文富文本
     */
    private String richText;

    /** 是否星标 */
    private boolean flagged;

    /**
     * 是否为分开发送
     */
    private boolean separated;

    private List<String> toEmailAddressList;
    private List<String> ccEmailAddressList;
    private List<String> bccEmailAddressList;

    private List<EmailAttachmentDTO> emailAttachmentDTOList;

    public enum Type {
        NORMAL(1),
        REPLY(2),
        FORWARD(3);

        Integer value;

        Type() {

        }

        Type(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }
}
