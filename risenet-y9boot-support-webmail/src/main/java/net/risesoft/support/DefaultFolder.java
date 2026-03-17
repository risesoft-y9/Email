package net.risesoft.support;

public enum DefaultFolder {
    INBOX("INBOX", "收件箱", false),
    DRAFTS("Drafts", "草稿箱", false),
    SENT("Sent", "已发送", false),
    TRASH("Trash", "回收站", false),
    MY_FOLDER("My Folder", "我的文件夹", true);

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

    public static DefaultFolder getByName(String name) {
        for (DefaultFolder folder : DefaultFolder.values()) {
            if (folder.getName().equals(name)) {
                return folder;
            }
        }
        return null;
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
