package com.itmk.web.sys_role_menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.sys_role_menu.entity.RoleMenu;
import com.itmk.web.sys_role_menu.entity.SaveMenuParm;
import com.itmk.web.sys_role_menu.mapper.RoleMenuMapper;
import com.itmk.web.sys_role_menu.service.RoleMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu>
        implements RoleMenuService {
    @Override
    @Transactional
    public void saveRoleMenu(SaveMenuParm parm) {
        // 删除角色菜单
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId,parm.getRoleId());
        this.baseMapper.delete(wrapper);
        // 在保存新的角色菜单
        this.baseMapper.saveRoleMenu(parm.getRoleId(), parm.getList());
    }
}
