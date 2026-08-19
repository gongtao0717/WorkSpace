package com.itmk.web.sys_user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itmk.jwt.JwtUtils;
import com.itmk.util.ResultUtils;
import com.itmk.util.ResultVo;
import com.itmk.web.sys_menu.entity.AssignTreeParm;
import com.itmk.web.sys_menu.entity.AssignTreeVo;
import com.itmk.web.sys_menu.entity.SysMenu;
import com.itmk.web.sys_menu.service.SysMenuService;
import com.itmk.web.sys_user.entity.*;
import com.itmk.web.sys_user.service.SysUserService;
import com.itmk.web.sys_user_role.entity.SysUserRole;
import com.itmk.web.sys_user_role.service.SysUserRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sysUser")
public class SysUserController {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysUserRoleService sysUserRoleService;
    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private SysMenuService sysMenuService;

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

    //登录
    @PostMapping("/login")
    public ResultVo login(HttpServletRequest request){
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        wrapper.eq(SysUser::getUsername, username)
                .eq(SysUser::getPassword, password);
        SysUser user = sysUserService.getOne(wrapper);
        if (user != null) {
            return ResultUtils.success("登录成功", user);
        } else {
            return ResultUtils.error("登录失败");
        }
    }

    //查询菜单树
    @GetMapping("/getAssignTree")
    public ResultVo getAssignTree(AssignTreeParm parm){
        AssignTreeVo assignTree = sysUserService.getAssignTree(parm);
        return ResultUtils.success("查询成功", assignTree);
    }

    //修改密码
    @PostMapping("updatePassword")
    public ResultVo updatePassword(@RequestBody UpdatePasswordParm parm){
        SysUser user = sysUserService.getById(parm.getUserId());
        if (!parm.getOldPassword().equals(user.getPassword())){
            return ResultUtils.error("旧密码不正确");
        }
        //更新条件
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUser::getUserId, parm.getUserId())
                .set(SysUser::getPassword,parm.getNewPassword());
        if (sysUserService.update(wrapper)){

            return ResultUtils.success("修改密码成功");
        } else {
            return ResultUtils.error("修改密码失败");
        }
    }

    //获取用户信息
    @GetMapping("/getInfo")
    public ResultVo getInfo(Long userId){
        //根据id查询用户信息
        SysUser user = sysUserService.getById(userId);
        List<SysMenu> menuList = null;
        //判断是否为超级管理员
        if (StringUtils.isNotEmpty(user.getIsAdmin()) && "1".equals(user.getIsAdmin())){
            //超级管理员,全部查询
            menuList = sysMenuService.list();
        }else {
            //普通用户,根据用户id查询
            menuList = sysMenuService.getMenuByUserId(user.getUserId());
        }

        //过滤出code字段
        List<String> collect = Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> StringUtils.isNotEmpty(item.getCode()))
                .map(item -> item.getCode())
                .collect(Collectors.toList());
        //设置返回值
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setName(user.getNickName());
        userInfo.setPermissons(collect.toArray(new String[0]));
        return ResultUtils.success("查询成功", userInfo);
    }
}
