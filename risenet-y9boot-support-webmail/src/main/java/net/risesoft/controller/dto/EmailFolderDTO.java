package net.risesoft.controller.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailFolderDTO implements Serializable {

    private static final long serialVersionUID = -157039089680216641L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 文件夹名称
     */
    private String name;

    /**
     * 文件夹全名（子文件夹会包含父文件夹名，例如：MyFolder.test）
     */
    private String fullName;

    /**
     * 用于展示的文件夹名
     */
    private String title;

    private Integer messageCount;

    public EmailFolderDTO(String name, String fullName, String title) {
        this.name = name;
        this.fullName = fullName;
        this.title = title;
    }
}
