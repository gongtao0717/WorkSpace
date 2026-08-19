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

    public static List<RouterVO> makeRouter(List<SysMenu> menuList, Long pid) {
        //存放路由数据
        List<RouterVO> list = new ArrayList<>();
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null && item.getParentId().equals(pid))
                .forEach(item -> {
                    RouterVO router = new RouterVO();
                    router.setName(item.getName());
                    router.setPath(item.getPath());
                    //递归获取子菜单
                    List<RouterVO> children = makeRouter(menuList, item.getMenuId());
                    router.setChildren(children);

                    //父级菜单(parentId=0)组件为Layout
                    if (item.getParentId() == 0L) {
                        router.setComponent("Layout");
                        //type=1：菜单类型，做一层嵌套路由
                        if (item.getType().equals("1")) {
                            router.setRedirect(item.getPath());
                            List<RouterVO> listChild = new ArrayList<>();
                            RouterVO child = new RouterVO();
                            child.setName(item.getName());
                            child.setPath(item.getPath());
                            child.setComponent(item.getUrl());
                            child.setMeta(child.new Meta(
                                    item.getTitle(),
                                    item.getIcon(),
                                    item.getCode().split(",")
                            ));
                            listChild.add(child);
                            router.setChildren(listChild);
                            router.setPath(item.getPath() + "parent");
                            router.setName(item.getName() + "parent");
                        }
                    } else {
                        router.setComponent(item.getUrl());
                    }
                    router.setMeta(router.new Meta(
                            item.getTitle(),
                            item.getIcon(),
                            item.getCode().split(",")
                    ));
                    list.add(router);
                });
        return list;
    }
}
