package net.risesoft.support;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public enum DefaultFolder {
    INBOX("INBOX", "收件箱", false), DRAFTS("Drafts", "草稿箱", false), SENT("Sent", "已发送", false),
    TRASH("Trash", "回收站", false), MY_FOLDER("My Folder", "我的文件夹", true);

    /**
     * 英文名称
     */
    private final String name;
    /**
     * 中文名称
     */
    private final String cName;

    private final boolean existSubFolder;

    DefaultFolder(String name, String cName, boolean existSubFolder) {
        this.name = name;
        this.cName = cName;
        this.existSubFolder = existSubFolder;
    }

    public static boolean is(String folderName) {
        if (StringUtils.isBlank(folderName)) {
            return false;
        }
        return Arrays.stream(DefaultFolder.values()).anyMatch(folder -> folderName.equals(folder.getName()));
    }

    public String getName() {
        return name;
    }

    public String getcName() {
        return cName;
    }

    public boolean isExistSubFolder() {
        return existSubFolder;
    }
}
