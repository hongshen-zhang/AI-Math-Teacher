package com.chaty.dto;

import com.chaty.entity.DocCorrectConfigPackage;
import lombok.Data;

@Data
public class DocCorrectConfigPackageDTO extends DocCorrectConfigPackage {

    private PageDTO<DocCorrectConfigPackage> page;

}
