package net.risesoft.james.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * james邮件服务器定义的用户表 在这里新增了 personId plainText 两个字段， 为了能让数字底座人员能登录邮件服务器 todo 实际不应该直接修改其他系统的表 而是通过接口等其他方式操作且又能实现账号密码同步
 * 目前暂无较好的方法
 *
 * @author shidaobang
 * @date 2023/05/06
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "JAMES_USER")
@org.hibernate.annotations.Table(comment = "邮件用户", appliesTo = "JAMES_USER")
public class JamesUser implements Serializable {

    private static final long serialVersionUID = 7817380951308002778L;

    @Id
    @Column(name = "USER_NAME", length = 100, nullable = false)
    @Comment("邮箱地址")
    private String emailAddress;

    @Column(name = "PASSWORD", length = 128, nullable = false)
    @Comment("密码")
    private String password;

    @Column(name = "PASSWORD_HASH_ALGORITHM", length = 100)
    @Comment("加密算法")
    private String passwordHashAlgorithm;

    @Column(name = "PERSON_ID", length = 100)
    @Comment("人员ID")
    private String personId;

    @Column(name = "PLAINTEXT", length = 50)
    @Comment("密码明文")
    private String plainText;

    @Column(name = "VERSION", length = 11)
    @Comment("版本")
    private Integer version;
}
