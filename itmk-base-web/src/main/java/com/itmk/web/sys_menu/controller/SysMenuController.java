package com.itmk.web.sys_menu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itmk.util.ResultUtils;
import com.itmk.util.ResultVo;
import com.itmk.web.sys_menu.entity.MakeMenuTree;
import com.itmk.web.sys_menu.entity.RouterVO;
import com.itmk.web.sys_menu.entity.SysMenu;
import com.itmk.web.sys_menu.service.SysMenuService;
import com.itmk.web.sys_user.entity.SysUser;
import com.itmk.web.sys_user.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sysMenu")
public class SysMenuController {
    @Resource
    private SysMenuService sysMenuService;
    @Resource
    private SysUserService sysUserService;

    //新增
    @PostMapping
    public ResultVo add(@RequestBody SysMenu sysMenu){
        sysMenu.setCreateTime(new Date());
        if (sysMenuService.save(sysMenu)) {
            return ResultUtils.success("添加成功");
        }
        return ResultUtils.error("添加失败");
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody SysMenu sysMenu){
        sysMenu.setUpdateTime(new Date());
        if (sysMenuService.updateById(sysMenu)) {
            return ResultUtils.success("编辑成功");
        }
        return ResultUtils.error("编辑失败");
    }

    //删除
    @DeleteMapping("/{menuId}")
    public ResultVo delete(@PathVariable("menuId") Long menuId){
        //如果存在下级，不能删除
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId,menuId);
        List<SysMenu> list = sysMenuService.list(wrapper);
        if (list.size() > 0) {
            return ResultUtils.error("存在下级，不能删除");
        }

        if (sysMenuService.removeById(menuId)) {
            return ResultUtils.success("删除成功");
        }
        return ResultUtils.error("删除失败");
    }

    //列表
    @GetMapping("/list")
    public ResultVo getList(){
        //排序
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getOrderNum);
        //查询列表
        List<SysMenu> list = sysMenuService.list(wrapper);
        //组装树数据
        List<SysMenu> sysMenus = MakeMenuTree.makeTree(list, 0L);
        return ResultUtils.success("查询成功",sysMenus);
    }

    //查询上级菜单
    @GetMapping("/getParent")
    public ResultVo getParent(){
        List<SysMenu> list = sysMenuService.getParent();
        return ResultUtils.success("查询成功",list);
    }

    //获取菜单
    @GetMapping("/getMenuList")
    public ResultVo getMenuList(Long userId){
        //获取用户信息
        SysUser user = sysUserService.getById(userId);
        List<SysMenu> menuList = null;
        //判断是否为超级管理员
        if(StringUtils.isNotEmpty(user.getIsAdmin()) && "1".equals(user.getIsAdmin())){
            menuList = sysMenuService.list();
        }else {
            menuList = sysMenuService.getMenuByUserId(userId);
        }
        //过滤菜单数据,去掉按钮数据
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> StringUtils.isNotEmpty(item.getType()) && !item.getType().equals("2")).collect(Collectors.toList());
        //组装路由数据
        List<RouterVO> routerList = MakeMenuTree.makeRouter(menuList, 0L);
        return ResultUtils.success("查询成功",routerList);
    }
}

