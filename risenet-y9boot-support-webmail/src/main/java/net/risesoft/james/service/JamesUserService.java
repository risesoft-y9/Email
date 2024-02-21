package net.risesoft.james.service;

import java.util.List;

import net.risesoft.james.entity.JamesUser;
import net.risesoft.model.platform.Person;

public interface JamesUserService {

    /**
     * 根据id查找用户
     *
     * @param id
     * @return
     */
    JamesUser findById(String id);

    /**
     * 根据邮箱地址查找用户
     *
     * @param emailAddress
     * @return
     */
    JamesUser findByEmailAddress(String emailAddress);

    /**
     * 添加
     *
     * @param personId
     * @param loginName
     */
    void add(String personId, String loginName);

    /**
     * 修改密码
     *
     * @param id
     * @param plainText
     */
    void modifyPassword(String id, String plainText);

    /**
     * 获取密码
     *
     * @param personId
     * @return
     */
    String getPlainTextByPersonId(String personId);

    /**
     * 被人电子邮件地址id
     *
     * @param personId 人员id
     * @return {@link String}
     */
    String getEmailAddressByPersonId(String personId);

    List<String> getIdByPersonIDs(List<String> personIds);

    List<String> getEmailAddressListByOrgUnitId(List<String> orgUnitIdList);

    void delete(Person person);
}
