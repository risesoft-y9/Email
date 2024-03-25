package net.risesoft.james.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.customgroup.CustomGroupApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.enums.platform.ExtendedOrgTypeEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.james.entity.JamesUser;
import net.risesoft.james.repository.JamesUserRepository;
import net.risesoft.james.service.JamesUserService;
import net.risesoft.model.platform.CustomGroupMember;
import net.risesoft.model.platform.Person;
import net.risesoft.util.EmailUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.util.signing.Y9MessageDigest;

@Service(value = "jamesUserService")
@RequiredArgsConstructor
public class JamesUserServiceImpl implements JamesUserService {

    private final CustomGroupApi customGroupApi;
    private final PersonApi personApi;
    private final Y9Properties y9Properties;
    private final JamesUserRepository jamesUserRepository;

    @Override
    public void add(String personId, String loginName) {
        Optional<JamesUser> jamesUserOptional = jamesUserRepository.findByPersonId(personId);
        if (!jamesUserOptional.isPresent()) {
            String emailAddress = EmailUtil.buildEmailAddress(loginName);
            int i = 1;
            while (jamesUserRepository.existsById(emailAddress)) {
                // 考虑到可能通过james提供的其他方式添加同样的账号 或者登录名转成拼音后可能相同 所以需要保证生成的邮箱不存在
                emailAddress = EmailUtil.buildEmailAddress(String.format("%s%d", loginName, i++));
            }

            String password = y9Properties.getCommon().getDefaultPassword();
            JamesUser jamesUser = new JamesUser();
            jamesUser.setEmailAddress(emailAddress);
            jamesUser.setVersion(1);
            jamesUser.setPasswordHashAlgorithm("MD5");
            jamesUser.setPlainText(password);
            jamesUser.setPersonId(personId);
            jamesUser.setPassword(Y9MessageDigest.MD5(password));
            jamesUserRepository.save(jamesUser);
        }
    }

    private void addByPersonId(List<String> emailAddressList, String id) {
        String emailAddress = this.getEmailAddressByPersonId(id);
        if (StringUtils.isNotBlank(emailAddress)) {
            emailAddressList.add(emailAddress);
        }
    }

    @Override
    public JamesUser findById(String id) {
        return jamesUserRepository.findById(id).orElse(null);
    }

    @Override
    public JamesUser findByEmailAddress(String emailAddress) {
        return jamesUserRepository.findByEmailAddress(emailAddress).orElse(null);
    }

    @Override
    public List<String> getEmailAddressListByOrgUnitId(List<String> orgUnitIdList) {
        List<String> emailAddressList = new ArrayList<>();
        for (String orgUnitId : orgUnitIdList) {
            emailAddressList.addAll(getEmailAddressListByOrgUnitId(orgUnitId));
        }
        return emailAddressList;
    }

    @Override
    public void delete(Person person) {
        jamesUserRepository.deleteByPersonId(person.getId());
    }

    private List<String> getEmailAddressListByOrgUnitId(String orgTypeAndOrgUnitId) {
        List<String> emailAddressList = new ArrayList<>();

        String tenantId = Y9LoginUserHolder.getTenantId();

        String[] orgTypeAndOrgUnitIdArr = orgTypeAndOrgUnitId.split(":");
        String orgType = orgTypeAndOrgUnitIdArr[0];
        String orgUnitId = orgTypeAndOrgUnitIdArr[1];

        if (OrgTypeEnum.DEPARTMENT.getEnName().equals(orgType)) {
            List<Person> personList = personApi.listRecursivelyByParentId(tenantId, orgUnitId).getData();
            for (Person person : personList) {
                addByPersonId(emailAddressList, person.getId());
            }
        } else if (OrgTypeEnum.PERSON.getEnName().equals(orgType)) {
            addByPersonId(emailAddressList, orgUnitId);
        } else if (ExtendedOrgTypeEnum.CUSTOM_GROUP.getEnName().equals(orgType)) {
            List<CustomGroupMember> groupMemberList = customGroupApi
                .listCustomGroupMemberByGroupId(tenantId, Y9LoginUserHolder.getPersonId(), orgUnitId).getData();
            for (CustomGroupMember customGroupMember : groupMemberList) {
                addByPersonId(emailAddressList, customGroupMember.getMemberId());
            }
        }
        return emailAddressList;
    }

    @Override
    public String getEmailAddressByPersonId(String personId) {
        return jamesUserRepository.findEmailAddressByPersonId(personId);
    }

    @Override
    public List<String> getIdByPersonIDs(List<String> personIds) {
        return jamesUserRepository.findEmailAddressByPersonIds(personIds);
    }

    @Override
    public String getPlainTextByPersonId(String personId) {
        Optional<JamesUser> jamesUserOptional = jamesUserRepository.findByPersonId(personId);
        if (jamesUserOptional.isPresent()) {
            return jamesUserOptional.get().getPlainText();
        } else {
            return "";
        }
    }

    @Override
    public void modifyPassword(String id, String plainText) {
        JamesUser oldju = this.findById(id);
        Integer version = oldju.getVersion() + 1;
        oldju.setPlainText(plainText);
        plainText = Y9MessageDigest.MD5(plainText);
        oldju.setPassword(plainText);
        oldju.setVersion(version);
        jamesUserRepository.save(oldju);
    }
}
