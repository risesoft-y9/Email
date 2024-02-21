package net.risesoft.james.entity.term;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.SearchTerm;

/**
 * @Description 是否已读搜索
 * @Author lzw
 * @Date 2023-04-24 11:34
 */
public final class MyFlagTerm extends SearchTerm {
    private final boolean set;

    /**
     * Flags object containing the flags to test.
     *
     * @serial
     */
    private final Flags flags;

    private static final long serialVersionUID = -142991500302030647L;

    /**
     * Constructor.
     *
     * @param flags Flags object containing the flags to check for
     * @param set the flag setting to check for
     */
    public MyFlagTerm(Flags flags, boolean set) {
        this.flags = flags;
        this.set = set;
    }

    /**
     * Return the Flags to test.
     *
     * @return the flags
     */
    public Flags getFlags() {
        return (Flags)flags.clone();
    }

    /**
     * Return true if testing whether the flags are set.
     *
     * @return true if testing whether the flags are set
     */
    public boolean getTestSet() {
        return set;
    }

    /**
     * The comparison method.
     *
     * @param msg The flag comparison is applied to this Message
     * @return true if the comparson succeeds, otherwise false.
     */
    @Override
    public boolean match(Message msg) {

        try {
            Flags f = msg.getFlags();
            if (set) { // This is easy
                return f.contains(flags);
            }

            // Return true if ALL flags in the passed in Flags
            // object are NOT set in this Message.

            // Got to do this the hard way ...
            Flags.Flag[] sf = flags.getSystemFlags();

            // Check each flag in the passed in Flags object
            for (Flags.Flag flag : sf) {
                if (f.contains(flag))
                    // this flag IS set in this Message, get out.
                    return false;
            }

            String[] s = flags.getUserFlags();

            // Check each flag in the passed in Flags object
            for (String value : s) {
                if (f.contains(value))
                    // this flag IS set in this Message, get out.
                    return false;
            }

            return true;

        } catch (MessagingException | RuntimeException e) {
            return false;
        }
    }
}
