package net.risesoft.controller;

import net.risesoft.controller.dto.EmailDTO;
import net.risesoft.james.entity.JamesAddressBook;
import net.risesoft.james.service.JamesAddressBookService;
import net.risesoft.james.service.JamesUserService;
import net.risesoft.pojo.Y9Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController(value = "standardJamesAddressBookController")
@RequestMapping(value = "/api/standard/jamesAddressBook")
public class JamesAddressBookController {

    @Autowired
    private JamesAddressBookService jamesAddressBookService;

    /**
     * 个人通讯录保存/修改
     *
     * @param jamesAddressBook
     * @return
     */
    @PostMapping
    public Y9Result<Object> save(JamesAddressBook jamesAddressBook) throws Exception {
        return Y9Result.success(jamesAddressBookService.saveOrUpdate(jamesAddressBook),"保存成功");
    }

    /**
     * 个人通讯录查询
     *
     * @param search
     * @return
     */
    @GetMapping(value = "/search")
    public Y9Result<Object> search(String search) {
        return Y9Result.success(jamesAddressBookService.findSearch(search));
    }

    /**
     * 通讯录详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/addressBook")
    public Y9Result<Object> addressBookList(String id) {
        return Y9Result.success(jamesAddressBookService.findOne(id));
    }

    /**
     * 获取个人通讯录
     *
     * @return
     */
    @GetMapping(value = "/addressBookList")
    public Y9Result<Object> addressBookList() {
        return Y9Result.success(jamesAddressBookService.findAll());
    }

    /**
     * 删除个人通讯录
     *
     * @return
     */
    @DeleteMapping
    public Y9Result<Object> delete(String id){
        jamesAddressBookService.delete(id);
        return Y9Result.successMsg("删除成功");
    }

}
