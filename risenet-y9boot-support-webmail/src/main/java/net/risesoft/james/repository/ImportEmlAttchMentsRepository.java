package net.risesoft.james.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.james.entity.ImportEmlAttchMents;

public interface ImportEmlAttchMentsRepository
    extends JpaRepository<ImportEmlAttchMents, String>, JpaSpecificationExecutor<ImportEmlAttchMents> {

    List<ImportEmlAttchMents> findByImportEmlId(String importEmlId);
}
