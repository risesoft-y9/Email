package net.risesoft.controller.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 电子邮件详情dto
 *
 * @author shidaobang
 * @date 2023/04/27
 */
@Data
@NoArgsConstructor
public class EmailDetailDTO extends EmailDTO implements Serializable {
    private static final long serialVersionUID = 8222438278258649119L;

    /**
     * IMAP UID，在一个账户中是唯一的
     */
    private Long uid;

    /**
     * 上一封邮件的 uid，用于邮件详情页中跳转
     */
    private Long previousUid;

    /**
     * 下一封邮件的 uid，用于邮件详情页中跳转
     */
    private Long nextUid;

}
