package net.risesoft.james.service;

import java.io.IOException;
import java.util.List;

import org.apache.james.mime4j.dom.Message;
import org.springframework.data.domain.Page;

import net.risesoft.james.entity.ImportEml;
import net.risesoft.pojo.Y9PageQuery;

public interface ImportEmlService {

    void delete(List<String> ids);

    /**
     * 转发（还存在问题）
     * 
     * @param id
     * @throws Exception
     */
    void forward(String id) throws Exception;

    ImportEml getById(String id);

    void importEmailByEml(Message message) throws IOException;

    List<String> listIdsByPersonId(String personId);

    Page<ImportEml> pageByPersonId(String personId, Y9PageQuery pageQuery);

    Page<ImportEml> pageSearch(String personId, String subject, String htmlContent, Y9PageQuery pageQuery);
}
