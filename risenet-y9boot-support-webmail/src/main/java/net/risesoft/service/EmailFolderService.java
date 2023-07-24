package net.risesoft.service;

import java.util.List;

import jakarta.mail.MessagingException;

import net.risesoft.controller.dto.EmailFolderDTO;

public interface EmailFolderService {

    List<EmailFolderDTO> list() throws MessagingException;

    List<EmailFolderDTO> getDefaultFolderList();

    void save(String originFolderName, String newFolderName);

    void delete(String id);
}
