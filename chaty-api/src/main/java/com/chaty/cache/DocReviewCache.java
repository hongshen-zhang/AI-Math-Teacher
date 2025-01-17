package com.chaty.cache;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocReviewCache {
    
    private String docReviewId;
    private Integer questionNum;
    private Integer reviewedNum;
}
