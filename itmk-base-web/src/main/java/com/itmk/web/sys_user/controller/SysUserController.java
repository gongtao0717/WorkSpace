package com.itmk.web.sys_user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.util.ResultUtils;
import com.itmk.util.ResultVo;
import com.itmk.web.sys_user.entity.SysUser;
import com.itmk.web.sys_user.entity.SysUserPage;
import com.itmk.web.sys_user.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/sysUser")
public class SysUserController {
    @Resource
    private SysUserService sysUserService;

    //新增
    @PostMapping
    public ResultVo add(@RequestBody SysUser sysUser){
        sysUser.setCreateTime(new Date());
        if (sysUserService.save(sysUser)){
            return ResultUtils.success("添加成功");
        }
        return ResultUtils.error("添加失败");
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody SysUser sysUser){
        sysUser.setUpdateTime(new Date());
        if (sysUserService.updateById(sysUser)){
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }

    //删除
    @DeleteMapping("/{userId}")
    public ResultVo delete(@PathVariable("userId") Long userId){
        if (sysUserService.removeById(userId)){
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败");
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
}
