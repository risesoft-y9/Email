package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.Folder;
import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.controller.dto.EmailFolderDTO;
import net.risesoft.james.service.JamesUserService;
import net.risesoft.service.EmailFolderService;
import net.risesoft.support.DefaultFolder;
import net.risesoft.y9.configuration.app.y9webmail.Y9WebMailProperties;

import jodd.mail.ReceiveMailSession;

@Service
public class EmailFolderServiceImpl extends MailHelper implements EmailFolderService {

    public EmailFolderServiceImpl(Y9WebMailProperties y9WebMailProperties, JamesUserService jamesUserService,
        PersonApi personApi) {
        super(y9WebMailProperties, jamesUserService, personApi);
    }

    @Override
    public List<EmailFolderDTO> list() throws MessagingException {
        List<EmailFolderDTO> emailFolderList = new ArrayList<>();

        ReceiveMailSession session = createReceiveMailSession();
        session.open();
        try {
            Folder myFolder = session.getFolder(DefaultFolder.MY_FOLDER.getName());
            if (myFolder.exists()) {
                Folder[] folders = myFolder.list();
                for (Folder folder : folders) {
                    emailFolderList.add(new EmailFolderDTO(folder.getName(), folder.getFullName(), folder.getName()));
                }
            } else {
                myFolder.create(Folder.HOLDS_FOLDERS);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        session.close();
        return emailFolderList;
    }

    @Override
    public List<EmailFolderDTO> getDefaultFolderList() {
        ReceiveMailSession session = createReceiveMailSession();
        session.open();

        List<EmailFolderDTO> emailFolderDTOList = new ArrayList<>();
        List<DefaultFolder> defaultFolderList = Arrays.stream(DefaultFolder.values())
            .filter(defaultFolder -> !defaultFolder.isExistSubFolder()).collect(Collectors.toList());
        for (DefaultFolder folder : defaultFolderList) {
            Folder mailFolder = session.getFolder(folder.getName());
            try {
                if (!mailFolder.exists()) {
                    mailFolder.create(Folder.HOLDS_MESSAGES);
                }
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            emailFolderDTOList.add(new EmailFolderDTO(folder.getName(), folder.getName(), folder.getcName()));
        }

        session.close();

        return emailFolderDTOList;
    }

    @Override
    public void save(String originFolderName, String newFolderName) {
        ReceiveMailSession session = createReceiveMailSession();
        session.open();
        try {
            Folder myFolder = session.getFolder(DefaultFolder.MY_FOLDER.getName());
            Folder newFolder = myFolder.getFolder(newFolderName);
            if (StringUtils.isNotBlank(originFolderName)) {
                Folder originFolder = myFolder.getFolder(originFolderName);
                originFolder.renameTo(myFolder.getFolder(newFolderName));
            } else {
                newFolder.create(Folder.HOLDS_FOLDERS);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        session.close();
    }

    @Override
    public void delete(String folderName) {
        ReceiveMailSession session = createReceiveMailSession();
        session.open();
        try {
            Folder myFolder = session.getFolder(DefaultFolder.MY_FOLDER.getName());
            myFolder.getFolder(folderName).delete(true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        session.close();
    }
}
