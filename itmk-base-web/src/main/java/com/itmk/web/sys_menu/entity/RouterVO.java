package com.itmk.web.sys_menu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVO {
    private String path;
    private String component;
    private String name;
    private String redirect;
    private Meta meta;
    private List<RouterVO> children =new ArrayList<>();

    @Data
    @AllArgsConstructor
    public class Meta {
        private String title;
        private String icon;
        private Object[] roles;
    }
}
