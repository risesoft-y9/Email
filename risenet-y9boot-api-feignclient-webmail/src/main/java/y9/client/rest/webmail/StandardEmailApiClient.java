package y9.client.rest.webmail;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.webmail.StandardEmailApi;

/**
 * 标准电子邮件 (webmail)
 *
 */

@FeignClient(contextId = "StandardEmail4ApiClient", name = "${y9.service.webmail.name:webmail}",
    url = "${y9.service.webmail.directUrl:}",
    path = "/${y9.service.webmail.name:server-webmail}/services/rest/standardEmail")
public interface StandardEmailApiClient extends StandardEmailApi {

}
