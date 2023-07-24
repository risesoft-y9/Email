package net.risesoft.james.entity.term;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.search.AddressStringTerm;

/**
 * description：收件人搜索
 * 
 * @author lzw
 * @date 2023-04-24 9:21
 */
public final class MyReceiverTerm extends AddressStringTerm {

    String pattern;

    /**
     * Construct a StringTerm with the given pattern. Case will be ignored.
     *
     * @param pattern the pattern
     */
    public MyReceiverTerm(String pattern) {
        super(pattern);
        this.pattern = pattern;
    }

    /**
     * This method applies a specific match criterion to the given message and returns the result.
     *
     * @param msg The match criterion is applied on this message
     * @return true, it the match succeeds, false if the match fails
     */
    @Override
    public boolean match(Message msg) {
        Address[] tos;
        try {
            tos = msg.getAllRecipients();
        } catch (Exception e) {
            return false;
        }

        if (tos == null) {
            return false;
        }

        for (Address to : tos) {
            if (super.match(to)) {
                return true;
            }
        }
        return false;
    }
}
