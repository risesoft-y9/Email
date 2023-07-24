package net.risesoft.james.entity.term;

import jakarta.mail.Message;
import jakarta.mail.search.StringTerm;

/**
 * description：邮件主题搜索
 * 
 * @author lzw
 * @date 2023-04-23 17:48
 */
public final class MySubjectTerm extends StringTerm {

    /**
     * Construct a StringTerm with the given pattern. Case will be ignored.
     *
     * @param pattern the pattern
     */

    public MySubjectTerm(String pattern) {
        super(pattern);
    }

    @Override
    public boolean match(Message msg) {
        String subj;

        try {
            subj = msg.getSubject();
        } catch (Exception e) {
            return false;
        }

        if (subj == null) {
            return false;
        }

        return super.match(subj);
    }
}
