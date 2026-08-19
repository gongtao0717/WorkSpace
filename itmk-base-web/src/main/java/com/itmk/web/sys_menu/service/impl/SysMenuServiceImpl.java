package com.itmk.web.sys_menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.sys_menu.entity.AssignTreeParm;
import com.itmk.web.sys_menu.entity.AssignTreeVo;
import com.itmk.web.sys_menu.entity.MakeMenuTree;
import com.itmk.web.sys_menu.entity.SysMenu;
import com.itmk.web.sys_menu.mapper.SysMenuMapper;
import com.itmk.web.sys_menu.service.SysMenuService;
import com.itmk.web.sys_user.entity.SysUser;
import com.itmk.web.sys_user.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
        implements SysMenuService {

    @Resource
    private SysMenuMapper sysMenuMapper;
    @Resource
    private SysUserService sysUserService;

    @Override
    public List<SysMenu> getParent() {
        String[] type = {"0","1"};
        List<String> strings = Arrays.asList(type);
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysMenu::getType, strings).orderByAsc(SysMenu::getOrderNum);
        List<SysMenu> menuList = this.baseMapper.selectList(wrapper);
        //组装顶级树
        SysMenu menu = new SysMenu();
        menu.setTitle("顶级菜单");
        menu.setLabel("顶级菜单");
        menu.setParentId(-1L);
        menu.setMenuId(0L);
        menu.setValue(0L);
        menuList.add(menu);
        //组装树数据
        List<SysMenu> tree = MakeMenuTree.makeTree(menuList, 0L);
        return tree;
    }

    @Override
    public List<SysMenu> getMenuByUserId(Long userId) {
        return sysMenuMapper.getMenuByUserId(userId);
    }

    @Override
    public List<SysMenu> getMenuByRoleId(Long roleId) {
        return sysMenuMapper.getMenuByRoleId(roleId);
    }

}
