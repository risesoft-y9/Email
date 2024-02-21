package net.risesoft.controller.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailSearchDTO {

    /** 文件夹 */
    private String folder;

    /** 主题 */
    private String subject;

    /** 正文 */
    private String text;

    /** 发件人 */
    private String from;

    /** 收件人 */
    private String to;

    /** 开始时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /** 开始时间 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /** 是否有附件 */
    private Boolean attachment;

    /** 是否已读 */
    private Boolean read;

    /** 是否星标 */
    private Boolean flagged;

    /** 分页 */
    private Integer page;

    private Integer size;

}
