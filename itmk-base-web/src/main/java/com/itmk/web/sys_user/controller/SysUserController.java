package com.itmk.web.sys_user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.util.ResultUtils;
import com.itmk.util.ResultVo;
import com.itmk.web.sys_user.entity.SelectItme;
import com.itmk.web.sys_user.entity.SysUser;
import com.itmk.web.sys_user.entity.SysUserPage;
import com.itmk.web.sys_user.service.SysUserService;
import com.itmk.web.sys_user_role.entity.SysUserRole;
import com.itmk.web.sys_user_role.service.SysUserRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sysUser")
public class SysUserController {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysUserRoleService sysUserRoleService;

    //新增
    @PostMapping
    public ResultVo add(@RequestBody SysUser sysUser){
        sysUser.setCreateTime(new Date());
        sysUserService.saveUser(sysUser);
        return ResultUtils.error("添加成功");
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody SysUser sysUser){
        sysUser.setUpdateTime(new Date());
        sysUserService.editUser(sysUser);
        return ResultUtils.error("编辑成功");
    }

    //删除
    @DeleteMapping("/{userId}")
    public ResultVo delete(@PathVariable("userId") Long userId){
        sysUserService.deleteUser(userId);
        return ResultUtils.success("删除成功");
    }

    //列表
    @GetMapping("/list")
    public ResultVo list(SysUserPage pram){
        //构造分页对象
        IPage<SysUser> page = new Page<>(pram.getCurrentPage(), pram.getPageSize());
        //构造查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(pram.getNickName())){
            wrapper.like(SysUser::getNickName, pram.getNickName());
        }
        if (StringUtils.isNotEmpty(pram.getPhone())){
            wrapper.like(SysUser::getPhone, pram.getPhone());
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        IPage<SysUser> list = sysUserService.page(page, wrapper);

        return ResultUtils.success("查询成功", list);
    }

    //角色下拉数据列表
    @GetMapping("/selectList")
    public ResultVo selectList(){
        List<SysUser> list = sysUserService.list();

        //返回值
        List<SelectItme> selectItmeList = new ArrayList<>();

        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(item ->{
                    SelectItme vo = new SelectItme();
                    vo.setCheck(false);
                    vo.setLabel(item.getNickName());
                    vo.setValue(item.getUserId());
                    selectItmeList.add(vo);
                });

        return ResultUtils.success("查询成功", selectItmeList);
    }

    //根据用户id获取角色列表
    @GetMapping("/getRoleList")
    public ResultVo getRoleList(Long userId){
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);

        List<SysUserRole> list = sysUserRoleService.list(wrapper);

        List<Long> roleIdList = new ArrayList<>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .forEach(item -> {
                    roleIdList.add(item.getRoleId());
                });
        return ResultUtils.success("查询成功", roleIdList);
    }

    //重置密码
    @PostMapping
    public ResultVo resetPassword(@RequestBody SysUser sysUser){
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUser::getUserId, sysUser.getUserId())
                .set(SysUser::getPassword, "123456");
        if (sysUserService.update(wrapper)) {
            return ResultUtils.success("重置密码成功");
        } else {
            return ResultUtils.error("重置密码失败");
        }
    }
}
