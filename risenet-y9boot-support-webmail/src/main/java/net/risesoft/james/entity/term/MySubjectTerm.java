package net.risesoft.james.entity.term;

import javax.mail.Message;
import javax.mail.search.StringTerm;

/**
 * @Description 邮件主题搜索
 * @Author lzw
 * @Date 2023-04-23 17:48
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
