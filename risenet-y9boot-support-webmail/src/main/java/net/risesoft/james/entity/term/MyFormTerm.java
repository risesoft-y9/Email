package net.risesoft.james.entity.term;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.search.AddressStringTerm;

/**
 * description：发件人搜索
 * 
 * @author lzw
 * @date 2023-04-24 9:14
 */
public final class MyFormTerm extends AddressStringTerm {

    private static final long serialVersionUID = -8293562089611618849L;

    /**
     * Constructor.
     *
     * @param pattern the address pattern to be compared.
     */
    public MyFormTerm(String pattern) {
        super(pattern);
    }

    /**
     * This method applies a specific match criterion to the given message and returns the result.
     *
     * @param msg The match criterion is applied on this message
     * @return true, it the match succeeds, false if the match fails
     */
    @Override
    public boolean match(Message msg) {
        Address[] from;
        try {
            from = msg.getFrom();
        } catch (Exception e) {
            return false;
        }

        if (from == null)
            return false;

        for (Address address : from)
            if (super.match(address))
                return true;
        return false;
    }

}
