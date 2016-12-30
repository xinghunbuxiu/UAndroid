package com.lixh.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class TagFormat {


    public static TagFormat from(String format) {

        return new TagFormat(format);

    }

    private final String format;

    private final Map<String, Object> tags = new LinkedHashMap<String, Object>();

    private TagFormat(String format) {
        this.format = format;
    }

    public TagFormat with(String key, Object value) {
        tags.put("\\{" + key + "\\}", value);
        return this;
    }

    public String format() {
        String formatted = format;
        for (Map.Entry<String, Object> tag : tags.entrySet()) {

            formatted = formatted.replaceAll(tag.getKey(), tag.getValue().toString());
        }
        return formatted;

    }

}