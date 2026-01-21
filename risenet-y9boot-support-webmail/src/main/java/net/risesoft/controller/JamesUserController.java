package net.risesoft.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.james.service.JamesUserService;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;

@RestController(value = "standardJamesUserController")
@RequestMapping(value = "/api/standard/jamesUser")
@RequiredArgsConstructor
public class JamesUserController {

    private final JamesUserService jamesUserService;

    /**
     * 根据组织架构id获取下面所有人的邮箱地址
     *
     * @param orgUnitIdList 格式 orgType:orgUnitId
     * @return {@code Y9Result<List<String>>}
     */
    @RiseLog(operationName = "获取组织架构下所有人的邮箱地址")
    @GetMapping(value = "/addressList")
    public Y9Result<List<String>>
        getEmailAddressListByOrgUnitId(@RequestParam(name = "orgUnitIds") List<String> orgUnitIdList) {
        List<String> emailAddressList = jamesUserService.getEmailAddressListByOrgUnitId(orgUnitIdList);
        return Y9Result.success(emailAddressList);
    }

    /**
     * 修改密码
     * 
     * @param id 人员id
     * @param plainText 密码
     * @return {@code Y9Result<Object>}
     */
    @RiseLog(operationName = "修改用户密码", operationType = OperationTypeEnum.MODIFY)
    @RequestMapping(value = "/modifyPassWord")
    public Y9Result<Object> modifyPassWord(String id, String plainText) {
        jamesUserService.modifyPassword(id, plainText);
        return Y9Result.success();
    }
}
