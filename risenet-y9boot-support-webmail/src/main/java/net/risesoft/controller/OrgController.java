package net.risesoft.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.james.service.JamesUserService;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController(value = "standardOrgController")
@RequestMapping(value = "/api/standard/org")
public class OrgController {

    @Autowired
    private OrgUnitApi orgUnitApi;
    @Autowired
    private OrganizationApi organizationApi;
    @Autowired
    private JamesUserService jamesUserService;

    /**
     * 获取组织列表
     *
     * @return {@link Y9Result}<{@link List}<{@link Organization}>>
     */
    @GetMapping(value = "/getOrganization")
    public Y9Result<List<Organization>> getOrganization() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Organization> organizationList = organizationApi.list(tenantId).getData();
        return Y9Result.success(organizationList);
    }

    /**
     * 获取组织树
     *
     * @param id id
     * @param treeType 树类型
     * @param name 名字
     * @return {@link Y9Result}<{@link List}<{@link OrgUnit}>>
     */
    @RequestMapping("/getOrgTree")
    public Y9Result<List<OrgUnit>> getOrgTree(@RequestParam(required = false) String id,
        @RequestParam(required = false) OrgTreeTypeEnum treeType, @RequestParam(required = false) String name) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        if (StringUtils.isBlank(id)) {
            List<Organization> organizationList = organizationApi.list(tenantId).getData();
            if (organizationList != null && !organizationList.isEmpty()) {
                id = organizationList.get(0).getId();
            }
        }
        List<OrgUnit> orgUnitList;
        if (StringUtils.isNotBlank(name)) {
            orgUnitList = orgUnitApi.treeSearch(tenantId, name, treeType).getData();
            orgUnitList = mapEmailAddress(orgUnitList);
        } else {
            orgUnitList = orgUnitApi.getSubTree(tenantId, id, treeType).getData();
            orgUnitList = mapEmailAddress(orgUnitList);
        }
        return Y9Result.success(orgUnitList);
    }

    private List<OrgUnit> mapEmailAddress(List<OrgUnit> orgUnitList) {
        orgUnitList = orgUnitList.stream().peek(org -> {
            if (OrgTypeEnum.PERSON.equals(org.getOrgType())) {
                Person person = (Person)org;
                String email = jamesUserService.getEmailAddressByPersonId(org.getId());
                if (StringUtils.isEmpty(email) && StringUtils.isEmpty(person.getEmail())) {
                    email = "未注册邮箱";
                } else {
                    if (!StringUtils.isEmpty(email)) {
                        person.setEmail(email);
                    }
                    if (!StringUtils.isEmpty(person.getEmail())) {
                        email = person.getEmail();
                    }
                }
                person.setName(org.getName() + "(" + email + ")");
            }
        }).collect(Collectors.toList());
        return orgUnitList;
    }
}
