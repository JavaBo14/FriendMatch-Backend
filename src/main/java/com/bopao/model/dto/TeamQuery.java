package com.bopao.model.dto;


import com.bopao.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;


/**
 * 队伍查询封装类
 *
 * @author <a href="https://github.com/JavaBo14">Bo</a>
 * @from https://github.com/JavaBo14/Matching
 */
@EqualsAndHashCode(callSuper = true)
//当你的类是某个类的子类，并且你想在 equals 和 hashCode 方法中包含超类（父类）的字段时，可以使用 @EqualsAndHashCode(callSuper = true)。
@Data
public class TeamQuery extends PageRequest {
    /**
     * id
     */
    private Long id;

    /**
     * id 列表
     */
    private List<Long> idList;

    /**
     * 搜索关键词（同时对队伍名称和描述搜索）
     */
    private String searchText;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;
}
