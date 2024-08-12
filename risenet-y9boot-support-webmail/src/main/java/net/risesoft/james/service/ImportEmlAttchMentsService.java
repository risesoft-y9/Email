package net.risesoft.james.service;

import java.io.InputStream;

public interface ImportEmlAttchMentsService {

    void saveAttchMents(String importEmlId, String fileName, Long fileSize, InputStream file);
}
