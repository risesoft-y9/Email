package net.risesoft.james.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "JAMES_IMPORT_EML_ATTCHMENTS")
@org.hibernate.annotations.Table(comment = " 历史邮件附件详细信息", appliesTo = "JAMES_IMPORT_EML_ATTCHMENTS")
public class ImportEmlAttchMents implements Serializable {

    private static final long serialVersionUID = -1899142526752041802L;

    /** 主键id */
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @Comment("主键id")
    private String id;

    /** 历史邮件ID */
    @Column(name = "IMPORT_EML_ID", length = 38, nullable = false)
    @Comment("历史邮件ID")
    private String importEmlId;

    /** 附件名称 */
    @Column(name = "FILE_NAME", length = 800)
    @Comment(value = "附件名称")
    private String fileName;

    /** 文件大小 */
    @Column(name = "FILE_SIZE", length = 20)
    @Comment(value = "文件大小")
    private String fileSize;

    /** 下载路径 */
    @Column(name = "URL", length = 800)
    @Comment(value = "下载路径")
    private String url;

    /** 文件服务器存储路径 */
    @Column(name = "FILESTORE_ID", nullable = false, length = 100)
    @Comment(value = "文件服务器存储路径")
    private String fileStoreId;

    @Column(name = "FILESTORE_EXT", length = 100)
    @Comment(value = "文件类型")
    private String fileExt;
}
