package com.chaty.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

@Data
public class PageDTO<T> {

    private Integer pageSize = 10;
    private Integer pageNumber = 1;
    private Boolean searchCount = true;

    public IPage<T> page() {
        return new Page<>(pageNumber, pageSize, searchCount);
    }

    public <U> IPage<U> page(Class<U> clazz) {
        return new Page<>(pageNumber, pageSize, searchCount);
    }

}
