package com.itmk.web.sys_role.entity;

import lombok.Data;
import lombok.Lombok;

@Data
public class RoleParm {
    // 当前页
    private Long currentPage;
    // 每页显示条数
    private Long pageSize;
    // 角色名
    private String roleName;
}
