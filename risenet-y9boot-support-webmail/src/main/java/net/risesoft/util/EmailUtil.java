package net.risesoft.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.controller.dto.EmailDTO;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.y9webmail.Y9WebMailProperties;
import net.risesoft.y9.util.Y9FileUtil;

public class EmailUtil {

    public static String getReplyOrForwardContent(EmailDTO email) {
        String toEmailAddresses = String.join(";", email.getToEmailAddressList());
        String ccEmailAddresses = String.join(";", email.getCcEmailAddressList());
        String sendTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(email.getSendTime());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getSignature());
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

    public static String getSignature() {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String positionId = StringUtils.isNotBlank(userInfo.getPositionId()) ? userInfo.getPositionId()
            : Arrays.stream(userInfo.getPositions().split(",")).findFirst().orElse(null);

        Position position = Y9Context.getBean(PositionApi.class).get(userInfo.getTenantId(), positionId).getData();
        HttpServletRequest request =
            ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String ipAddr = Y9Context.getIpAddr(request);

        Map<String, Object> keyValueMap = new HashMap<>();
        keyValueMap.put("personName", userInfo.getName());
        keyValueMap.put("mobile", userInfo.getMobile());
        keyValueMap.put("jobName", position.getJobName());
        keyValueMap.put("tenantName", userInfo.getTenantName());
        keyValueMap.put("ip", ipAddr);

        String template = null;
        try {
            template =
                Y9FileUtil.getContent(ResourceUtils.getFile("classpath:templates/signature.txt").getAbsolutePath());
        } catch (IOException e) {
        }
        return StringSubstitutor.replace(template, keyValueMap, "{{", "}}");
    }

    public static String buildEmailAddress(String loginName) {
        Y9WebMailProperties y9WebMailProperties = Y9Context.getBean(Y9WebMailProperties.class);
        return ConvertPinYin.getPinyin(loginName) + "@" + y9WebMailProperties.getHost();
    }
}
