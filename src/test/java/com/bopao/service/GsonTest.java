package com.bopao.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class GsonTest {
    @Test
    public static void main(String[] args) {
        List<String> tagNameList = Arrays.asList("Java", "Spring Boot", "Redis");
        System.out.println(tagNameList);
        Gson gson=new Gson();
        String tag = gson.toJson(tagNameList);
        System.out.println(tag);
    }
}
