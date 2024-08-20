package com.bopao.readexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;


/**
 * 最简单的读
 * <p>
 * 1. 创建excel对应的实体对象 参照{@link UserData}
 * <p>
 * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link UserDataListener}
 * <p>
 * 3. 直接读即可
 */
public class UserExcelRead {

    // 写法1：JDK8+ ,不用额外写一个DemoDataListener
    // since: 3.0.0-beta1
    public static void main(String[] args) {
        String fileName = "D:\\JAVA\\SpringBoot\\BoPao\\bopao-backend\\bopao-backend\\src\\main\\resources\\testExcel.xlsx";
//        listenerRead(fileName);
        directlyRead(fileName);
    }
        public static void listenerRead (String fileName) {
            // 这里默认读取第一个sheet
            EasyExcel.read(fileName, UserData.class, new UserDataListener()).sheet().doRead();
        }
        public  static void directlyRead (String fileName) {
            // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
            // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
            EasyExcel.read(fileName, UserData.class, new PageReadListener<UserData>(dataList -> {
                for (UserData userData : dataList) {
                    System.out.println(userData);
                }
            })).sheet().doRead();
        }
    }
