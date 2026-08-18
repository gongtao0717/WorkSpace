package com.itmk.web.sys_menu.entity;

import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class MakeMenuTree {
    public static List<SysMenu> makeTree(List<SysMenu> menuList, Long pid){
        //存放组装的树数据
        List<SysMenu> list = new ArrayList<>();
        //组装树
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null && item.getParentId().equals(pid))
                .forEach(item ->{
                    SysMenu sysMenu = new SysMenu();
                    BeanUtils.copyProperties(item, sysMenu);
                    sysMenu.setLabel(item.getTitle());
                    sysMenu.setValue(item.getMenuId());
                    //递归组装子菜单
                    List<SysMenu> children = makeTree(menuList, item.getMenuId());
                    sysMenu.setChildren(children);
                    list.add(sysMenu);
                });
        return list;
    }
}
