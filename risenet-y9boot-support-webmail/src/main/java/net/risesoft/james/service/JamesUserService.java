package net.risesoft.james.service;

import java.util.List;

import net.risesoft.james.entity.JamesUser;
import net.risesoft.model.platform.org.Person;

public interface JamesUserService {

    /**
     * 根据id查找用户
     *
     * @param id 人员id
     * @return {@link JamesUser}
     */
    JamesUser findById(String id);

    /**
     * 根据邮箱地址查找用户
     *
     * @param emailAddress 邮箱地址
     * @return {@link JamesUser}
     */
    JamesUser findByEmailAddress(String emailAddress);

    /**
     * 添加
     *
     * @param personId 人员id
     * @param loginName 登录名称
     */
    void add(String personId, String loginName);

    /**
     * 修改密码
     *
     * @param id 人员id
     * @param plainText 密码
     */
    void modifyPassword(String id, String plainText);

    /**
     * 获取密码
     *
     * @param personId 人员id
     * @return String
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
