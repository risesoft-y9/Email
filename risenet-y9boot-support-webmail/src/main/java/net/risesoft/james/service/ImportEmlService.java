package net.risesoft.james.service;

import java.io.IOException;

import org.apache.james.mime4j.dom.Message;
import org.springframework.data.domain.Page;

import net.risesoft.james.entity.ImportEml;
import net.risesoft.pojo.Y9PageQuery;

public interface ImportEmlService {

    void importEmailByEml(Message message) throws IOException;

    ImportEml getById(String id);

    Page<ImportEml> pageByPersonId(String personId, Y9PageQuery pageQuery);
}
