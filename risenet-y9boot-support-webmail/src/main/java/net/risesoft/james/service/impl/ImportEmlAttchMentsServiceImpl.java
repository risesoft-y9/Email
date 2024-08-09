package net.risesoft.james.service.impl;

import java.io.InputStream;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.id.Y9IdGenerator;
import net.risesoft.james.entity.ImportEmlAttchMents;
import net.risesoft.james.repository.ImportEmlAttchMentsRepository;
import net.risesoft.james.service.ImportEmlAttchMentsService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

@Service(value = "importEmlAttchMentsService")
@Slf4j
@RequiredArgsConstructor
public class ImportEmlAttchMentsServiceImpl implements ImportEmlAttchMentsService {

    private final ImportEmlAttchMentsRepository importEmlAttchMentsRepository;

    private final Y9FileStoreService y9FileStoreService;

    private final Y9Properties y9conf;

    @Override
    public void saveAttchMents(String importEmlId, String fileName, Long fileSize, InputStream file) {
        try {
            LocalDate currentDate = LocalDate.now();
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), "eml-attchMents",
                currentDate.getYear() + "", currentDate.getMonthValue() + "");
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath, fileName);

            ImportEmlAttchMents attch = new ImportEmlAttchMents();
            attch.setId(Y9IdGenerator.genId());
            attch.setImportEmlId(importEmlId);
            attch.setName(fileName);
            attch.setFileSize(fileSize);
            attch.setFileStoreId(y9FileStore.getId());

            String url =
                y9conf.getCommon().getWebmailBaseUrl() + "/s/" + y9FileStore.getId() + "." + y9FileStore.getFileExt();
            attch.setUrl(url);
            importEmlAttchMentsRepository.save(attch);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("上传附件失败", e);
        }

    }
}
