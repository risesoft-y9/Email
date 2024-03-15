package net.risesoft.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.platform.customgroup.CustomGroupApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.platform.CustomGroup;
import net.risesoft.model.platform.CustomGroupMember;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController(value = "standardGroupController")
@RequestMapping("/api/standard/group")
public class GroupController {

    @Autowired
    private CustomGroupApi customGroupApi;

    @Autowired
    private PersonApi personApi;

    /**
     * 获取所有群组
     *
     * @param name
     * @return
     */
    @RequestMapping(value = "/getAllGroups")
    public Y9Result<List<CustomGroup>> getAllGroups(@RequestParam(required = false) String name) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String personId = Y9LoginUserHolder.getUserInfo().getPersonId();
        List<CustomGroup> customGroupList = customGroupApi.listCustomGroupByPersonId(tenantId, personId).getData();
        return Y9Result.success(customGroupList);
    }

    /**
     * 获取群组成员
     *
     * @param groupId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getGroupMembers4Email")
    public Y9Result<List<CustomGroupMember>> getEmailGroupMembers(String groupId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String personId = Y9LoginUserHolder.getUserInfo().getPersonId();
        List<CustomGroupMember> groupMemberModels = new ArrayList<>();
        if (StringUtils.isNotBlank(groupId)) {
            groupMemberModels = customGroupApi.listCustomGroupMemberByGroupId(tenantId, personId, groupId).getData();
        }
        return Y9Result.success(groupMemberModels);
    }

    /**
     * 获取群组成员
     *
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/getGroupMembers")
    public Y9Result<List<CustomGroupMember>> getGroupMembers(String groupId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String personId = Y9LoginUserHolder.getUserInfo().getPersonId();
        List<CustomGroupMember> groupMemberModels = new ArrayList<>();
        if (StringUtils.isNotBlank(groupId)) {
            groupMemberModels = customGroupApi.listCustomGroupMemberByGroupId(tenantId, personId, groupId).getData();
        }
        for (CustomGroupMember customGroupModel : groupMemberModels) {
            if (customGroupModel.getMemberType().equals(OrgTypeEnum.PERSON)) {
                Person person = personApi.get(tenantId, customGroupModel.getMemberId()).getData();
                if (person != null) {
                    if (StringUtils.isNotBlank(person.getMobile())) {
                        customGroupModel
                            .setMemberName(customGroupModel.getMemberName() + "(" + person.getMobile() + ")");
                    }
                }
            }
        }
        return Y9Result.success(groupMemberModels);
    }

    /**
     * 获取邮件相关群组 1
     * 
     * @return
     */
    @RequestMapping(value = "/getGroups4Email")
    public Y9Result<List<CustomGroup>> getGroups4Email() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String personId = Y9LoginUserHolder.getUserInfo().getPersonId();
        List<CustomGroup> customGroupList = customGroupApi.listCustomGroupByPersonId(tenantId, personId).getData();
        return Y9Result.success(customGroupList);
    }
}
