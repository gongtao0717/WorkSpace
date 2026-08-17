package com.itmk.web.sys_user.entity;

import lombok.Data;

@Data
public class SysUserPage {
    private String phone;
    private String nickName;
    // 当前页
    private Long currentPage;
    // 每页显示条数
    private Long pageSize;
}
