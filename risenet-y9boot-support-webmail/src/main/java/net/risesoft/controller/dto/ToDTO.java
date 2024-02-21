package net.risesoft.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ToDTO {
    /**
     * 收件人(仅包含.@youshengyun.com的发送人)
     */
    private String to;

    /**
     * 收件人姓名(仅包含.@youshengyun.com的发送人)
     */
    private String toName;

    /**
     * 收件人头像(仅包含.@youshengyun.com的发送人)
     */
    private String toAvator;
}
