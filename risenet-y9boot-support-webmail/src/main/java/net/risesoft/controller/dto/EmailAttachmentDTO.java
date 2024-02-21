package net.risesoft.controller.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailAttachmentDTO implements Serializable {

    private static final long serialVersionUID = 8259583975872573080L;

    /**
     * y9文件存储名称
     */
    private String fileName;

    /**
     * 展示的文件大小 K M G
     */
    private String displaySize;

    /**
     * 文件扩展名
     */
    private String fileExt;

    private String md5;

}
