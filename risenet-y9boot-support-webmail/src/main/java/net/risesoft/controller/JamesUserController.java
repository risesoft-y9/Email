package net.risesoft.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.james.service.JamesUserService;
import net.risesoft.pojo.Y9Result;

@RestController(value = "standardJamesUserController")
@RequestMapping(value = "/api/standard/jamesUser")
public class JamesUserController {

    @Autowired
    private JamesUserService jamesUserService;

    /**
     * 根据组织架构id获取下面所有人的邮箱地址
     *
     * @param orgUnitIdList 格式 orgType:orgUnitId
     * @return
     */
    @GetMapping(value = "/addressList")
    public Y9Result<List<String>>
        getEmailAddressListByOrgUnitId(@RequestParam(name = "orgUnitIds") List<String> orgUnitIdList) {
        List<String> emailAddressList = jamesUserService.getEmailAddressListByOrgUnitId(orgUnitIdList);
        return Y9Result.success(emailAddressList);
    }

    @RequestMapping(value = "/modifyPassWord")
    public Y9Result<Object> modifyPassWord(String id, String plainText) {
        jamesUserService.modifyPassword(id, plainText);
        return Y9Result.success();
    }
}
