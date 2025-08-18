package net.risesoft.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import net.risesoft.james.service.JamesUserService;
import net.risesoft.model.platform.org.Person;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.constant.Y9OrgEventTypeConst;
import net.risesoft.y9.pubsub.event.Y9EventOrg;

@Component
@RequiredArgsConstructor
public class Y9EventOrgListener implements ApplicationListener<Y9EventOrg> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private final JamesUserService jamesUserService;

    @Override
    public void onApplicationEvent(Y9EventOrg event) {
        logger.info(event.getEventType());

        String tenantId = event.getTenantId();
        Y9LoginUserHolder.setTenantId(tenantId);

        try {
            if (Y9OrgEventTypeConst.PERSON_DELETE.equals(event.getEventType())) {
                Person person = (Person)event.getOrgObj();
                logger.info("--------------------------删除人员-------------------------------");
                logger.info("--------------------" + person.getName() + "---------------------");

                jamesUserService.delete(person);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
