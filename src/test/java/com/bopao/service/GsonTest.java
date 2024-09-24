package com.bopao.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
public class GsonTest {
    @Test
    public static void main(String[] args) {
//        List<String> tagNameList = Arrays.asList("Java", "Spring Boot", "Redis");
//        System.out.println(tagNameList);
//        Gson gson=new Gson();
//        String tag = gson.toJson(tagNameList);
//        System.out.println(tag);


        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("ab3s");
        arrayList.add("ab3s");
        arrayList.add("ab3s");
        arrayList.add("ab3s");
        arrayList.add("ab3s");
        arrayList.add("ab3s");
        arrayList.add("ab32312cs");
        arrayList.add("ab31232cs");
        arrayList.add("ab3cs");
        arrayList.add("ab32132131cs");
        arrayList.add("ab32cs");
        arrayList.add("abc31121212s");
        arrayList.add("abc31s321321");
        arrayList.add("abc3132321312s");
        arrayList.add("abc313213213s");
        arrayList.add("abc332132131s");
        Set<String> newList = new HashSet<>();
        newList = arrayList.stream().filter(e -> e.length() <7).map(String::toUpperCase).collect(Collectors.toSet());

        System.out.println(newList);
    }


    /**
     * 给你一个集合  arrayList size 大于 7的数据给使用 stream 流过滤出来，返回一个新的 List   大于 7 的所有元素转为大写输出
     */
    @Test
    public static void toU(){
//
//        return userList.stream().filter(user /**User 是不是就是 List 中的每个对象**/ -> { // userList 集合，使用 stream 流 filter（英语看得懂吗） 过滤
//            String tagsStr = user.getTags();
//            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
//            }.getType());
//            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
//            for (String tagName : tagNameList) {
//                if (!tempTagNameSet.contains(tagName)) {
//                    return false;
//                }
//            }
//            return true;
//        }).map(this::getSafetyUser).collect(Collectors.toList());
        // 多态



    }
}
