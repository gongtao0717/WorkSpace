package com.itmk.web.sys_user.service.imple;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.sys_menu.entity.AssignTreeParm;
import com.itmk.web.sys_menu.entity.AssignTreeVo;
import com.itmk.web.sys_menu.entity.MakeMenuTree;
import com.itmk.web.sys_menu.entity.SysMenu;
import com.itmk.web.sys_menu.mapper.SysMenuMapper;
import com.itmk.web.sys_menu.service.SysMenuService;
import com.itmk.web.sys_user.entity.SysUser;
import com.itmk.web.sys_user.mapper.SysUserMapper;
import com.itmk.web.sys_user.service.SysUserService;
import com.itmk.web.sys_user_role.entity.SysUserRole;
import com.itmk.web.sys_user_role.service.SysUserRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    @Resource
    private SysUserRoleService sysUserRoleService;
    @Resource
    private SysMenuService sysMenuService;


    @Override
    @Transactional
    public void saveUser(SysUser sysUser) {
        //插入用户信息
        int i = this.baseMapper.insert(sysUser);
        //设置用户角色
        if (i > 0){
            String[] split = sysUser.getRoleId().split(",");
            if (split.length > 0){
                List<SysUserRole> roles = new ArrayList<>();
                for (int j = 0; j < split.length; j++) {
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setUserId(sysUser.getUserId());
                    sysUserRole.setRoleId(Long.parseLong(split[j]));
                    roles.add(sysUserRole);
                }
                //保存到用户角色表
                sysUserRoleService.saveBatch(roles);
            }
        }
    }

    @Override
    @Transactional
    public void editUser(SysUser sysUser) {
        int i = this.baseMapper.updateById(sysUser);
        if (i > 0){
            //获取刚刚传入的用户角色 用逗号分割
            String[] split = sysUser.getRoleId().split(",");
            //删除条件
            LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUserRole::getUserId, sysUser.getUserId());
            //删除原本的用户角色
            sysUserRoleService.remove(wrapper);

            //确认用户角色已经传入
            if (split.length > 0){
                List<SysUserRole> roles = new ArrayList<>();
                for (String roleId : split){
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setUserId(sysUser.getUserId());
                    sysUserRole.setRoleId(Long.parseLong(roleId));
                    roles.add(sysUserRole);
                }
                sysUserRoleService.saveBatch(roles);
            }
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        int i = this.baseMapper.deleteById(userId);
        if (i > 0){
            //删除角色
            LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUserRole::getUserId, userId);
            sysUserRoleService.remove(wrapper);
        }
    }

    @Override
    public AssignTreeVo getAssignTree(AssignTreeParm parm) {
        //查询用户的信息
        SysUser user = this.baseMapper.selectById(parm.getUserId());
        List<SysMenu> menuList = null;
        //判断是否为超级管理员
        if (StringUtils.isNotEmpty(user.getIsAdmin()) && "1".equals(user.getIsAdmin())){
            //是超级管理员，查询所有的菜单
            menuList = sysMenuService.list();
        } else {
            menuList = sysMenuService.getMenuByUserId(parm.getUserId());
        }
        List<SysMenu> menuTree = MakeMenuTree.makeTree(menuList, 0L);
        //查询角色原来的菜单
        List<SysMenu> roleList = sysMenuService.getMenuByRoleId(parm.getRoleId());
        List<Long> ids = new ArrayList<>();
        Optional.ofNullable(roleList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null)
                .forEach(item -> {
                    ids.add(item.getMenuId());
                });
        //组装返回数据
        AssignTreeVo vo = new AssignTreeVo();
        vo.setCheck(ids.toArray());
        vo.setListmenu(menuTree);
        return vo;
    }
}
