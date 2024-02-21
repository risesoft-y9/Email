package net.risesoft.util;

import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;

import net.risesoft.controller.dto.EmailDTO;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.configuration.app.y9webmail.Y9WebmailProperties;

public class EmailUtil {

    public static String getReplyHead(EmailDTO email) {
        String toEmailAddresses = String.join(";", email.getToEmailAddressList());
        String ccEmailAddresses = String.join(";", email.getCcEmailAddressList());
        String sendTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(email.getSendTime());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<p><br/></p>");
        stringBuilder.append("<p><br/></p>");
        stringBuilder.append("<p>-------------------------------原正文如下-----------------------------------</p>");
        stringBuilder.append("<p>主题：").append(email.getSubject()).append("</p>");
        stringBuilder.append("<p>时间：").append(sendTime).append("</p>");
        stringBuilder.append("<p>发件人：").append(email.getFrom()).append("</p>");
        stringBuilder.append("<p>收件人：").append(toEmailAddresses).append("</p>");
        if (StringUtils.isNotBlank(ccEmailAddresses)) {
            stringBuilder.append("<p>抄送：").append(ccEmailAddresses).append("</p>");
        }
        stringBuilder.append("<p><br/></p>");
        return stringBuilder.toString();
    }

    public static String buildEmailAddress(String loginName) {
        Y9WebmailProperties emailProperties = Y9Context.getBean(Y9Properties.class).getApp().getWebmail();
        return ConvertPinYin.getPinyin(loginName) + "@" + emailProperties.getHost();
    }
}
