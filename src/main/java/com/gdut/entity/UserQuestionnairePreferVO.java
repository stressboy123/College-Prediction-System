package com.gdut.entity;

import lombok.Data;

import java.util.List;

/**
 * @author liujunliang
 * @date 2026/2/13
 * 用户问卷偏好结构化VO
 */
@Data
public class UserQuestionnairePreferVO {
    // 二、地域偏好
    private List<Integer> targetProvinceIds; // 目标省份ID列表（本省/华东地区等转成的省份ID）
    private String distanceAccept; // 接受的距离：仅本省/邻省/全国
    // 三、院校偏好
    private String targetSchoolLevel; // 目标院校层次：985工程院校/211工程院校等
    private String targetSchoolNature; // 目标院校性质：公办/民办/中外合作
    private List<String> targetCollegeTypes; // 目标院校类型：综合类/理工类等（列表）
    // 四、专业偏好
    private List<String> targetMajorCategories; // 目标专业大类：工学类/理学类等（列表）
    private String majorSelectPriority; // 专业选择优先级：就业/兴趣/升学等
    private String avoidMajorCategory; // 避开的专业大类
    // 五、就业与升学导向
    private String developDirection; // 发展导向：就业/升学/无规划
    private List<String> targetEmploymentIndustries; // 目标就业行业（列表）
    // 六、约束条件
    private Integer tuitionFeeMax; // 接受的最高学费（元/年，如5000/10000/20000）
    private String majorAdjustAccept; // 是否接受专业调剂：完全接受/部分接受/完全不接受
    private String dormRequire; // 住宿要求
    // 七、补充偏好
    private Boolean focusSpecialPlan; // 是否关注专项计划：true/false
    private String majorHotLevel; // 专业热度偏好
    private Boolean focusEmploymentRate; // 是否关注就业率/考研率
    private Boolean focusCampusEnv; // 是否关注校园环境
    private Boolean acceptDifferentCampus; // 是否接受异地校区
    private String otherPrefer; // 其他偏好
}
