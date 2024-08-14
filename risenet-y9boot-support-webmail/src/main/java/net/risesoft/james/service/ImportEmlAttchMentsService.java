package net.risesoft.james.service;

import java.io.InputStream;
import java.util.List;

import net.risesoft.james.entity.ImportEmlAttchMents;

public interface ImportEmlAttchMentsService {

    void saveAttchMents(String importEmlId, String fileName, Long fileSize, InputStream file);

    List<ImportEmlAttchMents> listByImportEmlId(String importEmlId);

    ImportEmlAttchMents getById(String id);
}
