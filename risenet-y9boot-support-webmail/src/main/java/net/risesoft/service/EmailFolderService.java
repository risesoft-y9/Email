package net.risesoft.service;

import java.util.List;

import javax.mail.MessagingException;

import net.risesoft.controller.dto.EmailFolderDTO;

public interface EmailFolderService {

    List<EmailFolderDTO> list() throws MessagingException;

    List<EmailFolderDTO> getDefaultFolderList() throws MessagingException;

    void save(String originFolderName, String newFolderName) throws MessagingException;

    void delete(String id) throws MessagingException;
}
