package net.risesoft.james.service;

import java.io.IOException;
import java.util.List;

import org.apache.james.mime4j.dom.Message;
import org.springframework.data.domain.Page;

import net.risesoft.james.entity.ImportEml;
import net.risesoft.pojo.Y9PageQuery;

public interface ImportEmlService {

    /**
     * 删除历史邮件
     * 
     * @param ids 历史邮件id列表
     */
    void delete(List<String> ids);

    /**
     * 转发（还存在问题）
     * 
     * @param id 邮件id
     * @throws Exception 异常
     */
    void forward(String id) throws Exception;

    /**
     * 根据id，获取历史邮件
     * 
     * @param id 历史邮件id
     * @return {@code ImportEml}
     */
    ImportEml getById(String id);

    /**
     * 导入历史邮件
     * 
     * @param message 邮件信息
     * @throws IOException IO异常
     */
    void importEmailByEml(Message message) throws IOException;

    /**
     * 获取历史邮件id列表
     * 
     * @param personId 人员id
     * @return {@code List<String>}
     */
    List<String> listIdsByPersonId(String personId);

    /**
     * 根据人员id，获取历史邮件分页
     * 
     * @param personId 人员id
     * @param pageQuery 分页信息
     * @return {@code Page<ImportEml>}
     */
    Page<ImportEml> pageByPersonId(String personId, Y9PageQuery pageQuery);

    /**
     * 搜索历史邮件
     * 
     * @param personId 人员id
     * @param subject 主题
     * @param htmlContent 内容
     * @param pageQuery 分页信息
     * @return {@code Page<ImportEml>}
     */
    Page<ImportEml> pageSearch(String personId, String subject, String htmlContent, Y9PageQuery pageQuery);
}
