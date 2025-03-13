package net.risesoft.james.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "JAMES_ADDRESSBOOK")
@Comment("邮件个人通讯录")
public class JamesAddressBook implements Serializable {

    private static final long serialVersionUID = 7817380951308022777L;

    /** id */
    @Id
    @Column(name = "ID", length = 100, nullable = false)
    @Comment("id")
    private String id;

    /** 人员ID(当前登录人的ID) */
    @Column(name = "PERSON_ID", length = 100)
    @Comment("人员ID")
    private String personId;

    /** 邮箱地址 (需要添加的邮箱地址) */
    @Column(name = "EMAIL_ADDRESS", length = 100)
    @Comment("邮箱地址")
    private String emailAddress;

    /** 姓名(需要添加的姓名) */
    @Column(name = "NAME", length = 50)
    @Comment("姓名")
    private String name;

    /** 手机号码 */
    @Column(name = "PHONE", length = 50)
    @Comment("手机号码")
    private String phone;

    /** 办公电话 */
    @Column(name = "OFFICE_PHONE", length = 50)
    @Comment("办公电话")
    private String officePhone;

    /** 家庭地址 */
    @Column(name = "HOUSE_ADDRESS", length = 100)
    @Comment("家庭地址")
    private String houseAddress;

    /** 生日 */
    @Column(name = "BIRTHDAY", length = 50)
    @Comment("生日")
    private String birthday;

    /** 公司 */
    @Column(name = "COMPANY", length = 100)
    @Comment("公司")
    private String company;

    /** 部门 */
    @Column(name = "DEPARTMENT", length = 100)
    @Comment("部门")
    private String department;

    /** 职位 */
    @Column(name = "POSITION", length = 100)
    @Comment("职位")
    private String position;

    /** 备注 */
    @Column(name = "REMARK", length = 200)
    @Comment("备注")
    private String remark;

}
