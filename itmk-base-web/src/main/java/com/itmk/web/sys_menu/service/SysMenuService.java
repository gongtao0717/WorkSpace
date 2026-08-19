package com.itmk.web.sys_menu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itmk.web.sys_menu.entity.AssignTreeParm;
import com.itmk.web.sys_menu.entity.AssignTreeVo;
import com.itmk.web.sys_menu.entity.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {
    List<SysMenu> getParent();
    //根据用户id查询菜单
    List<SysMenu> getMenuByUserId(@Param("userId") Long userId);
    //根据角色id查询菜单
    List<SysMenu> getMenuByRoleId(@Param("roleId") Long roleId);

}
