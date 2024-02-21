package net.risesoft.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;

import net.risesoft.james.service.JamesUserService;
import net.risesoft.model.user.UserInfo;
import net.risesoft.support.EmailThreadLocalHolder;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 查找当前用户关联的邮箱并设值到 EmailThreadLocalHolder 中
 *
 * @author shidaobang
 * @date 2023/05/08
 */
public class EmailAddressFilter implements Filter {

    @Override
    public void destroy() {}

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            if (userInfo != null) {
                JamesUserService jamesUserService = Y9Context.getBean(JamesUserService.class);
                String emailAddress = jamesUserService.getEmailAddressByPersonId(userInfo.getPersonId());
                if (StringUtils.isNotBlank(emailAddress)) {
                    EmailThreadLocalHolder.setEmailAddress(emailAddress);
                } else {
                    // 当前用户没有注册邮箱，直接注册
                    jamesUserService.add(userInfo.getPersonId(), userInfo.getLoginName());
                }
            }
            chain.doFilter(request, response);
        } finally {
            Y9LoginUserHolder.clear();
        }
    }

}
