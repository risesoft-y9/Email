package net.risesoft.james.entity.term;

import javax.mail.Message;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.StringTerm;

public final class MyMessageIDTerm extends StringTerm {

    private static final long serialVersionUID = -2121096296454694315L;

    /**
     * Constructor.
     *
     * @param msgid the msgid to search for
     */
    public MyMessageIDTerm(String msgid) {
        // Note: comparison is case-insensitive
        super(msgid);
    }

    /**
     * The match method.
     *
     * @param msg the match is applied to this Message's Message-ID header
     * @return true if the match succeeds, otherwise false
     */
    @Override
    public boolean match(Message msg) {
        String[] s;

        try {
            s = msg.getHeader("Message-ID");
        } catch (Exception e) {
            return false;
        }

        if (s == null) {
            return false;
        }

        for (String value : s) {
            if (super.match(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Equality comparison.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MessageIDTerm)) {
            return false;
        }
        return super.equals(obj);
    }
}
