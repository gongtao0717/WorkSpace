package com.itmk.web.sys_role.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.util.ResultUtils;
import com.itmk.util.ResultVo;
import com.itmk.web.sys_role.entity.RoleParm;
import com.itmk.web.sys_role.entity.SysRole;
import com.itmk.web.sys_role.service.SysRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/api/role")
public class SysRoleController {
    @Resource
    private SysRoleService sysRoleService;

    // 添加角色
    @PostMapping
    public ResultVo add(@RequestBody SysRole sysRole){
        if (sysRoleService.save(sysRole)) {
            return ResultUtils.success("添加成功");
        }
        return ResultUtils.error("添加失败");
    }

    // 编辑角色
    @PutMapping
    public ResultVo edit(@RequestBody SysRole sysRole){
        sysRole.setUpdateTime(new Date());
        if (sysRoleService.updateById(sysRole)){
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }

    // 删除角色
    @DeleteMapping("/{roleId}")
    public ResultVo delete(@PathVariable("roleId") Long roleId){
        if (sysRoleService.removeById(roleId)){
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败");
    }

    // 查询角色列表
    @GetMapping("/getList")
    public ResultVo getList(RoleParm parm){
        //构建分页对象
        IPage<SysRole> page = new Page<>(parm.getCurrentPage(), parm.getPageSize());
        //构造查询条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(parm.getRoleName())){
            wrapper.like(SysRole::getRoleName, parm.getRoleName());
        }
        wrapper.orderByDesc(SysRole::getCreateTime);
        IPage<SysRole> list = sysRoleService.page(page, wrapper);

        return ResultUtils.success("查询成功",list);
    }
}
