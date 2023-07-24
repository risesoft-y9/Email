package net.risesoft.james.entity.term;

import java.io.IOException;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.search.StringTerm;

/**
 * description：邮件文本搜索
 * 
 * @author lzw
 * @date 2023-04-23 18:14
 */

public final class MyBodyTerm extends StringTerm {
    /**
     * This method applies a specific match criterion to the given message and returns the result.
     *
     * @param msg The match criterion is applied on this message
     * @return true, it the match succeeds, false if the match fails
     */

    /**
     * Construct a StringTerm with the given pattern. Case will be ignored.
     *
     * @param pattern the pattern
     */
    public MyBodyTerm(String pattern) {
        super(pattern);
    }

    @Override
    public boolean match(Message msg) {

        return matchPart(msg);
    }

    /**
     * Search all the parts of the message for any text part that matches the pattern.
     */
    private boolean matchPart(Part p) {
        try {
            /*
             * Using isMimeType to determine the content type avoids
             * fetching the actual content data until we need it.
             */
            if (p.isMimeType("text/*")) {
                String s = (String)p.getContent();
                if (s == null) {
                    return false;
                }
                /*
                 * We invoke our superclass' (i.e., StringTerm) match method.
                 * Note however that StringTerm.match() is not optimized
                 * for substring searches in large string buffers. We really
                 * need to have a StringTerm subclass, say BigStringTerm,
                 * with its own match() method that uses a better algorithm ..
                 * and then subclass BodyTerm from BigStringTerm.
                 */
                return super.match(s);
            } else if (p.isMimeType("multipart/*")) {
                Multipart mp = (Multipart)p.getContent();
                int count = mp.getCount();
                for (int i = 0; i < count; i++) {
                    if (matchPart(mp.getBodyPart(i))) {
                        return true;
                    }
                }
            } else if (p.isMimeType("message/rfc822")) {
                return matchPart((Part)p.getContent());
            }
        } catch (MessagingException | IOException | RuntimeException ignored) {
        }
        return false;
    }
}
