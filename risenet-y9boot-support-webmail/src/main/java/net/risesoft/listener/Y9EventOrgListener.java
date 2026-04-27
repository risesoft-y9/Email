package net.risesoft.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.james.service.JamesUserService;
import net.risesoft.model.platform.org.Person;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.constant.Y9OrgEventTypeConst;
import net.risesoft.y9.pubsub.event.Y9EventOrg;

@Component
@Slf4j
@RequiredArgsConstructor
public class Y9EventOrgListener implements ApplicationListener<Y9EventOrg> {

    private final JamesUserService jamesUserService;

    @Override
    public void onApplicationEvent(Y9EventOrg event) {
        LOGGER.info(event.getEventType());

        String tenantId = event.getTenantId();
        Y9LoginUserHolder.setTenantId(tenantId);

        try {
            if (Y9OrgEventTypeConst.PERSON_DELETE.equals(event.getEventType())) {
                Person person = (Person)event.getOrgObj();
                LOGGER.info("--------------------------删除人员-------------------------------");
                LOGGER.info("--------------------{}---------------------", person.getName());

                jamesUserService.delete(person);
            }

        } catch (Exception e) {
            LOGGER.error("处理组织事件失败", e);
        }
    }
}
