package com.chaty.dto;

import java.util.Map;

import lombok.Data;

@Data
public class FunctionDTO {

    private String name;

    private String description;

    private Parameters parameters;

    private String[] required;

    @Data
    public static class Parameters {
        private String type;
        private Map<String, Property> properties;

        @Data
        public static class Property {
            private String type;
            private String description;
            private String[] required;
        }
    }
}
