package com.itmk.web.sys_user.service.imple;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itmk.web.sys_user.entity.SysUser;
import com.itmk.web.sys_user.mapper.SysUserMapper;
import com.itmk.web.sys_user.service.SysUserService;
import com.itmk.web.sys_user_role.entity.SysUserRole;
import com.itmk.web.sys_user_role.service.SysUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    @Resource
    private SysUserRoleService sysUserRoleService;

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
}
