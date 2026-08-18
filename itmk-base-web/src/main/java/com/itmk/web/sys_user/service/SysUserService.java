package com.itmk.web.sys_user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.sys_user.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    void saveUser(SysUser sysUser);
    void editUser(SysUser sysUser);
    void deleteUser(Long userId);
}
