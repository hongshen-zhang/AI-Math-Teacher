package com.chaty.common;

import com.github.pagehelper.Page;

import lombok.Data;

@Data
public class BasePage {
    
    private Integer pageSize = 10;
    private Integer pageNumber = 1;

    public Page<?> page() {
        return new Page<>(pageNumber, pageSize);
    }

    public <T> com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> mpPage() {
        return new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNumber, pageSize);
    }

}
