package net.risesoft.controller.vo;

import lombok.Data;

@Data
public class ImportEmlAttchMentVO {
    /** 主键id */
    private String id;

    /** 历史邮件ID */
    private String importEmlId;

    /** 附件名称 */
    private String fileName;

    /** 文件大小 */
    private String fileSize;

    /** 下载路径 */
    private String url;

    /** 文件服务器存储路径 */
    private String fileStoreId;

    /** 文件类型 */
    private String fileExt;
}
