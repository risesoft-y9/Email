package net.risesoft.james.entity.term;

import jakarta.mail.Message;
import jakarta.mail.search.StringTerm;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.service.impl.EmailServiceImpl;

/**
 * description： 附件搜索
 * 
 * @author lzw
 * @date 2023-04-25 9:25
 */
@Slf4j
public final class MyAttachmentTerm extends StringTerm {

    Boolean hasAttachment = false;

    /**
     * Construct a StringTerm with the given pattern. Case will be ignored.
     *
     * @param pattern the pattern
     * @param hasAttachment if has Attachment
     */
    public MyAttachmentTerm(String pattern, Boolean hasAttachment) {
        super(pattern);
        this.hasAttachment = hasAttachment;
    }

    /**
     * This method applies a specific match criterion to the given message and returns the result.
     *
     * @param msg The match criterion is applied on this message
     * @return true, it the match succeeds, false if the match fails
     */
    @Override
    public boolean match(Message msg) {
        try {
            boolean isAttachment = EmailServiceImpl.isHasAttachment(msg);
            if (isAttachment) {
                return hasAttachment;
            } else {
                return !hasAttachment;
            }
        } catch (Exception e) {
            LOGGER.warn("exception", e);
        }
        return false;
    }
}
