package net.risesoft.controller.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailContactDTO implements Serializable {
    private static final long serialVersionUID = 8222438278258649120L;

    // 地址
    private String contactPerson;

    // 接收人id
    private String contactPersonId;

    // 接收人姓名
    private String contactPersonName;

    /**
     * 接收人头像(仅包含.@youshengyun.com的)
     */
    private String contactPersonAvator;

}
