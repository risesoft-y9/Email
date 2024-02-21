package net.risesoft.service;

import java.util.List;

public interface EmailMessageService {

    void send(Integer messageNumber, String subject, String richText, List<String> emailAddressList,
        List<String> ccEmailAddressList, List<String> bccEmailAddressList, List<String> attachmentIdList)
        throws Exception;

}
