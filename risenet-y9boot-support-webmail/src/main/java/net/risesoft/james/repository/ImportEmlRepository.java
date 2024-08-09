package net.risesoft.james.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.james.entity.ImportEml;

public interface ImportEmlRepository extends JpaRepository<ImportEml, String>, JpaSpecificationExecutor<ImportEml> {

    Optional<ImportEml> findByMessageId(String messageId);

    Page<ImportEml> findByPersonId(String personId, Pageable pageable);
}
