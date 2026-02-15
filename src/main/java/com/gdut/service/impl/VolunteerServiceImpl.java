package com.gdut.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.gdut.entity.CollegeMajorGroupVO;
import com.gdut.entity.EnrollmentGroupVO;
import com.gdut.entity.TAdmissionData;
import com.gdut.entity.TCollege;
import com.gdut.entity.TEnrollmentPlan;
import com.gdut.entity.TProvince;
import com.gdut.entity.TQuestionnaire;
import com.gdut.entity.TQuestionnaireAnswer;
import com.gdut.entity.TScoreRank;
import com.gdut.entity.UserInfoDTO;
import com.gdut.entity.UserQuestionnairePreferVO;
import com.gdut.entity.VolunteerFeatureVO;
import com.gdut.service.ITAdmissionDataService;
import com.gdut.service.ITCollegeService;
import com.gdut.service.ITEnrollmentPlanService;
import com.gdut.service.ITProvinceService;
import com.gdut.service.ITQuestionnaireAnswerService;
import com.gdut.service.ITQuestionnaireService;
import com.gdut.service.ITScoreRankService;
import com.gdut.service.VolunteerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author liujunliang
 * @date 2026/2/12
 */
@Service
public class VolunteerServiceImpl implements VolunteerService {
    @Resource
    private ITAdmissionDataService admissionDataService;

    @Resource
    private ITEnrollmentPlanService enrollmentPlanService;

    @Resource
    private ITCollegeService collegeService;

    @Resource
    private ITScoreRankService scoreRankService;

    @Resource
    private ITProvinceService provinceService;

    @Resource
    private ITQuestionnaireAnswerService questionnaireAnswerService;

    @Resource
    private ITQuestionnaireService questionnaireService;

    @Override
    @Transactional(readOnly = true)
    public void predict(UserInfoDTO dto) {
        List<TProvince> provinces = cleanProvince();
        Map<String, TProvince> provinceMap = provinces.stream().collect(Collectors.toMap(TProvince::getProvinceName, t -> t));
        // 1. 校验用户输入核心内容
        if (dto.getCandidateProvince() == null || dto.getFirstSubject() == null
                || dto.getSecondSubject() == null || dto.getCandidateYear() == null
                || dto.getGaokaoTotalScore() == null || dto.getGaokaoTotalScore() < 0
                || dto.getProvinceRank() == null || dto.getProvinceRank() < 0) {
            return;
        }
        // 2. 结构化问卷偏好
        UserQuestionnairePreferVO preferVO = questionnaireAnswerConvertToVO(dto.getSysUserId());

        // 3. 筛选后的院校专业组候选集
        List<CollegeMajorGroupVO> collegeMajorGroupVOS = selectCollegeMajorGroupVO(dto, preferVO, provinceMap);

        // 4. 特征提取和标准化
        List<VolunteerFeatureVO> featureList = featureExtraction(dto, preferVO, collegeMajorGroupVOS, provinceMap);
        List<VolunteerFeatureVO> standardList = normalizeFeatures(featureList);
    }

    /**
     * 特征标准化，所有连续型特征缩放到0-1 区间（标准化公式：Xscaled = (X - Xmin) / (Xmax - Xmin)）
     * @param featureList 特征数据
     * @return 标准化特征
     */
    private List<VolunteerFeatureVO> normalizeFeatures(List<VolunteerFeatureVO> featureList) {
        // 初始化各特征的全局最小值和最大值
        double minAvgAdmissionRank = Double.MAX_VALUE;
        double maxAvgAdmissionRank = Double.MIN_VALUE;
        double minRankFluctuation = Double.MAX_VALUE;
        double maxRankFluctuation = Double.MIN_VALUE;
        double minPlanChangeRate = Double.MAX_VALUE;
        double maxPlanChangeRate = Double.MIN_VALUE;
        double minProvinceCandidateChangeRate = Double.MAX_VALUE;
        double maxProvinceCandidateChangeRate = Double.MIN_VALUE;
        double minBatchPlanTotalChangeRate = Double.MAX_VALUE;
        double maxBatchPlanTotalChangeRate = Double.MIN_VALUE;

        // 第一遍遍历：计算每个特征的最小值和最大值
        for (VolunteerFeatureVO vo : featureList) {
            // avgAdmissionRank
            Double val = vo.getAvgAdmissionRank();
            if (val != null) {
                if (val < minAvgAdmissionRank) {
                    minAvgAdmissionRank = val;
                }
                if (val > maxAvgAdmissionRank) {
                    maxAvgAdmissionRank = val;
                }
            }
            // rankFluctuation
            val = vo.getRankFluctuation();
            if (val != null) {
                if (val < minRankFluctuation) {
                    minRankFluctuation = val;
                }
                if (val > maxRankFluctuation) {
                    maxRankFluctuation = val;
                }
            }
            // planChangeRate
            val = vo.getPlanChangeRate();
            if (val != null) {
                if (val < minPlanChangeRate) {
                    minPlanChangeRate = val;
                }
                if (val > maxPlanChangeRate) {
                    maxPlanChangeRate = val;
                }
            }
            // provinceCandidateChangeRate
            val = vo.getProvinceCandidateChangeRate();
            if (val != null) {
                if (val < minProvinceCandidateChangeRate) {
                    minProvinceCandidateChangeRate = val;
                }
                if (val > maxProvinceCandidateChangeRate) {
                    maxProvinceCandidateChangeRate = val;
                }
            }
            // batchPlanTotalChangeRate
            val = vo.getBatchPlanTotalChangeRate();
            if (val != null) {
                if (val < minBatchPlanTotalChangeRate) {
                    minBatchPlanTotalChangeRate = val;
                }
                if (val > maxBatchPlanTotalChangeRate) {
                    maxBatchPlanTotalChangeRate = val;
                }
            }
        }

        // 第二遍遍历：应用标准化公式
        List<VolunteerFeatureVO> normalizedList = new ArrayList<>(featureList.size());
        for (VolunteerFeatureVO vo : featureList) {
            VolunteerFeatureVO normalized = new VolunteerFeatureVO();

            // 复制非连续型特征（保持原值）
            normalized.setSubjectMatchRate(vo.getSubjectMatchRate());
            normalized.setSchoolLevelScore(vo.getSchoolLevelScore());
            normalized.setMajorHotScore(vo.getMajorHotScore());
            normalized.setUserProvinceRank(vo.getUserProvinceRank());

            // 标准化连续型特征
            normalized.setAvgAdmissionRank(scale(vo.getAvgAdmissionRank(), minAvgAdmissionRank, maxAvgAdmissionRank));
            normalized.setRankFluctuation(scale(vo.getRankFluctuation(), minRankFluctuation, maxRankFluctuation));
            normalized.setPlanChangeRate(scale(vo.getPlanChangeRate(), minPlanChangeRate, maxPlanChangeRate));
            normalized.setProvinceCandidateChangeRate(scale(vo.getProvinceCandidateChangeRate(), minProvinceCandidateChangeRate, maxProvinceCandidateChangeRate));
            normalized.setBatchPlanTotalChangeRate(scale(vo.getBatchPlanTotalChangeRate(), minBatchPlanTotalChangeRate, maxBatchPlanTotalChangeRate));

            normalizedList.add(normalized);
        }
        return normalizedList;
    }

    /**
     * 更新最小最大值
     * @param value 当前值
     * @param min 当前最大
     * @param max 当前最小
     */
    private void updateMinMax(Double value, double min, double max) {
        if (value != null) {
            if (value < min) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
        }
    }

    /**
     * 最小-最大缩放，若区间为0则返回0
     * @param value 当前值
     * @param min 当前最大
     * @param max 当前最小
     */
    private Double scale(Double value, double min, double max) {
        if (value == null) {
            return null;
        }
        if (max == min) {
            return 0.0; // 所有值相同，标准化后为0
        }
        return (value - min) / (max - min);
    }

    /**
     * 特征提取
     * @param dto 用户输入
     * @param preferVO 用户输入
     * @param currentCandidateList 院校专业组候选集
     * @return 特征数据
     */
    private List<VolunteerFeatureVO> featureExtraction(UserInfoDTO dto, UserQuestionnairePreferVO preferVO, List<CollegeMajorGroupVO> currentCandidateList, Map<String, TProvince> provinceMap) {
        List<VolunteerFeatureVO> featureVOList = new ArrayList<>();
        // 1. 用户特征提取
        Integer userProvinceRank = dto.getProvinceRank();
        // 选科匹配度由于之前的筛选已经把部分匹配和不匹配的都筛了，故都是1.0
        Double subjectMatchRate = 1.0;

        // 2. 环境特征提取
        Double candidateChangeRate = calculateProvinceCandidateChangeRate(dto.getCandidateProvince(), dto.getFirstSubject(), provinceMap);
        Double batchPlanChangeRate = calculateBatchPlanTotalChangeRate(currentCandidateList);

        // 3. 专业组特征提取：按专业组聚合近3年数据，逐一组计算院校专业组特征
        Map<String, List<CollegeMajorGroupVO>> mg3YearMap = currentCandidateList.stream()
                .collect(Collectors.groupingBy(mg -> mg.getCollegeCode() + "_" + mg.getMajorGroupCode()));
        // 遍历每个专业组
        for (Map.Entry<String, List<CollegeMajorGroupVO>> entry : mg3YearMap.entrySet()) {
            List<CollegeMajorGroupVO> mg3YearList = entry.getValue();
            CollegeMajorGroupVO mgVO = mg3YearList.get(0); // 取任意一个，获取专业组基础信息
            // 1. 计算院校专业组特征
            Long avgAdmissionRank = calculateAvgAdmissionRank(mg3YearList);
            Double rankFluctuation = calculateRankFluctuationNative(mg3YearList);
            Double planChangeRate = calculatePlanChangeRate(mg3YearList);
            Double schoolLevelScore = convertSchoolLevelToScore(mgVO.getSchoolLevel(), mgVO.getSchoolNature(), mgVO.getCollegeType());
            Double majorHotScore = convertMajorHotToScore(preferVO.getMajorHotLevel());
            // 2. 组装VolunteerFeatureVO
            VolunteerFeatureVO featureVO = new VolunteerFeatureVO();
            // 用户特征
            featureVO.setUserProvinceRank(userProvinceRank);
            featureVO.setSubjectMatchRate(subjectMatchRate);
            // 院校专业组特征
            featureVO.setAvgAdmissionRank(avgAdmissionRank.doubleValue());
            featureVO.setRankFluctuation(rankFluctuation);
            featureVO.setPlanChangeRate(planChangeRate);
            featureVO.setSchoolLevelScore(schoolLevelScore);
            featureVO.setMajorHotScore(majorHotScore);
            // 环境特征
            featureVO.setProvinceCandidateChangeRate(candidateChangeRate);
            featureVO.setBatchPlanTotalChangeRate(batchPlanChangeRate);
            // 添加到列表
            featureVOList.add(featureVO);
        }
        return featureVOList;
    }

    /**
     * 考生专业热度偏好转量化分（毕业项目轻量化方案）
     * @param majorHotLevel 考生热度偏好（如优先热门专业）
     * @return 量化分（Double，0.4-1.0）
     */
    private Double convertMajorHotToScore(String majorHotLevel) {
        if (StringUtils.isBlank(majorHotLevel)) {
            return 0.5;
        }
        switch (majorHotLevel) {
            case "优先热门专业":
                return 1.0;
            case "中等热度专业（竞争适中）":
                return 0.7;
            case "冷门专业（竞争小，易录取）":
                return 0.4;
            default:
                return 0.5; // 无偏好
        }
    }

    /**
     * 院校层次文本转量化分
     *
     * @param schoolLevel  院校层次
     * @param schoolNature 院校性质
     * @param collegeType 院校类型
     * @return 量化分（Double，0.3-3.0）
     */
    private Double convertSchoolLevelToScore(String schoolLevel, String schoolNature, String collegeType) {
        if (StringUtils.isBlank(schoolLevel)) {
            return 1.0;
        }
        double score;
        if ("本科".equals(schoolLevel)) {
            score = 1.0;
        } else {
            score = 0.3;
        }
        if (!"公办".equals(schoolNature)) {
            score -= 0.5;
        }
        if (collegeType != null) {
            score += 2.0;
        }
        return score;
    }

    /**
     * 计算2025相对2023招生计划变化率
     * @param mg3YearList 单专业组近3年CollegeMajorGroupVO
     * @return 变化率（Double，如0.10=10%，-0.05=-5%）
     */
    private Double calculatePlanChangeRate(List<CollegeMajorGroupVO> mg3YearList) {
        // 1. 提取2023/2025年的计划数
        int plan2023 = mg3YearList.stream()
                .filter(mg -> mg.getYear() == 2023)
                .map(CollegeMajorGroupVO::getTotalPlanCount)
                .findFirst().orElse(0);
        int plan2025 = mg3YearList.stream()
                .filter(mg -> mg.getYear() == 2025)
                .map(CollegeMajorGroupVO::getTotalPlanCount)
                .findFirst().orElse(0);

        // 兜底：2023年计划数为0，取2024和2025年计算
        if (plan2023 == 0) {
            plan2023 = mg3YearList.stream()
                    .filter(mg -> mg.getYear() == 2024)
                    .map(CollegeMajorGroupVO::getTotalPlanCount)
                    .findFirst().orElse(0);
        }
        // 双重兜底：仍为0，变化率为0
        if (plan2023 == 0 || plan2025 == 0) {
            return 0.0;
        }

        // 2. 计算变化率
        BigDecimal plan3 = BigDecimal.valueOf(plan2023);
        BigDecimal plan5 = BigDecimal.valueOf(plan2025);
        BigDecimal changeRate = plan5.subtract(plan3).divide(plan3, 4, RoundingMode.HALF_UP);
        // 保留2位小数
        return changeRate.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 计算样本标准差
     * @param mg3YearList 单专业组近3年CollegeMajorGroupVO
     * @return 波动值（Double）
     */
    private Double calculateRankFluctuationNative(List<CollegeMajorGroupVO> mg3YearList) {
        List<Long> rankList = mg3YearList.stream()
                .map(CollegeMajorGroupVO::getLowestAdmissionRank)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        int n = rankList.size();
        if (n < 2) {
            return 0.0; // 数据不足，波动值为0
        }

        // 计算平均值
        double avg = rankList.stream().mapToDouble(Long::doubleValue).average().getAsDouble();
        // 计算每个数据与平均值的差的平方和
        double sumSq = rankList.stream()
                .mapToDouble(r -> Math.pow(r - avg, 2))
                .sum();
        // 样本标准差公式：√(sumSq/(n-1))
        double std = Math.sqrt(sumSq / (n - 1));
        // 保留2位小数
        return BigDecimal.valueOf(std).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 计算近3年录取排名平均值
     * @param mg3YearList 单专业组近3年CollegeMajorGroupVO
     * @return 平均值（Long型，排名为整数）
     */
    private Long calculateAvgAdmissionRank(List<CollegeMajorGroupVO> mg3YearList) {
        // 1. 提取近3年录取排名
        List<Long> rankList = mg3YearList.stream()
                .map(CollegeMajorGroupVO::getLowestAdmissionRank)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        // 2. 计算总和
        BigDecimal sum = rankList.stream()
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 3. 计算平均值（年份数为实际数据量）
        int yearCount = rankList.size();
        BigDecimal avg = sum.divide(BigDecimal.valueOf(yearCount), 0, RoundingMode.HALF_UP);
        return avg.longValue();
    }

    /**
     * 计算考生批次近3年招生计划总数变化率，变化率 = (2025 批次总数 - 2023 批次总数) / 2023 批次总数 × 100%；
     * @param currentCandidateList 初选后的考生批次专业组VO列表
     * @return 变化率（Double）
     */
    private Double calculateBatchPlanTotalChangeRate(List<CollegeMajorGroupVO> currentCandidateList) {
        // 1. 按年份聚合批次总计划数
        Map<Integer, Integer> batchPlanYearMap = currentCandidateList.stream()
                .collect(Collectors.groupingBy(
                        CollegeMajorGroupVO::getYear,
                        Collectors.summingInt(CollegeMajorGroupVO::getTotalPlanCount)
                ));
        // 提取2023/2024/2025年总数
        Integer plan2023 = batchPlanYearMap.getOrDefault(2023, 0);
        Integer plan2024 = batchPlanYearMap.getOrDefault(2024, 0);
        Integer plan2025 = batchPlanYearMap.getOrDefault(2025, 0);
        // 兜底
        if (plan2025 != 0) {
            if (plan2023 != 0) {
                // 2. 计算变化率
                BigDecimal p3 = BigDecimal.valueOf(plan2023);
                BigDecimal p5 = BigDecimal.valueOf(plan2025);
                BigDecimal changeRate = p5.subtract(p3).divide(p3, 4, RoundingMode.HALF_UP);
                return changeRate.setScale(2, RoundingMode.HALF_UP).doubleValue();
            } else if (plan2024 != 0) {
                // 2. 计算变化率
                BigDecimal p4 = BigDecimal.valueOf(plan2024);
                BigDecimal p5 = BigDecimal.valueOf(plan2025);
                BigDecimal changeRate = p5.subtract(p4).divide(p4, 4, RoundingMode.HALF_UP);
                return changeRate.setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
        }
        return 0.0;
    }

    /**
     * 计算考生省份近3年高考总人数变化率，变化率 = (2025 总人数 - 2023 总人数) / 2023 总人数 × 100%；
     * @param province 考生省份
     * @param subjectType 考生科类（物理/历史）
     * @param provinceMap 省份映射map
     * @return 变化率（Double）
     */
    private Double calculateProvinceCandidateChangeRate(String province, String subjectType, Map<String, TProvince> provinceMap) {
        Integer provinceId = provinceMap.get(province).getId();

        // 1. 提取2023/2025年高考总人数（200分以后无计算意义）
        Integer total2023 = scoreRankService.lambdaQuery()
                .eq(TScoreRank::getProvinceId, provinceId)
                .eq(TScoreRank::getYear, 2023)
                .eq(TScoreRank::getSubjectType, subjectType)
                .eq(TScoreRank::getScore, 200)
                .last("LIMIT 1")
                .one()
                .getCumulativeCount();
        Integer total2024 = scoreRankService.lambdaQuery()
                .eq(TScoreRank::getProvinceId, provinceId)
                .eq(TScoreRank::getYear, 2024)
                .eq(TScoreRank::getSubjectType, subjectType)
                .eq(TScoreRank::getScore, 200)
                .last("LIMIT 1")
                .one()
                .getCumulativeCount();
        Integer total2025 = scoreRankService.lambdaQuery()
                .eq(TScoreRank::getProvinceId, provinceId)
                .eq(TScoreRank::getYear, 2025)
                .eq(TScoreRank::getSubjectType, subjectType)
                .eq(TScoreRank::getScore, 200)
                .last("LIMIT 1")
                .one()
                .getCumulativeCount();
        // 兜底：数据缺失则变化率为0
        if (total2025 != 0) {
            if (total2023 != 0) {
                // 2. 计算变化率：变化率 = (2025 总人数 - 2023 总人数) / 2023 总人数 × 100%；
                BigDecimal t3 = BigDecimal.valueOf(total2023);
                BigDecimal t5 = BigDecimal.valueOf(total2025);
                BigDecimal changeRate = t5.subtract(t3).divide(t3, 4, RoundingMode.HALF_UP);
                return changeRate.setScale(2, RoundingMode.HALF_UP).doubleValue();
            } else if (total2024 != 0) {
                // 2. 计算变化率：变化率 = (2025 总人数 - 2024 总人数) / 2024 总人数 × 100%；
                BigDecimal t4 = BigDecimal.valueOf(total2024);
                BigDecimal t5 = BigDecimal.valueOf(total2025);
                BigDecimal changeRate = t5.subtract(t4).divide(t4, 4, RoundingMode.HALF_UP);
                return changeRate.setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
        }
        return 0.0;
    }

    /**
     * 根据用户过滤基础结果集，根据问卷数据再筛选
     * @param dto 用户输入
     * @param preferVO 结构化问卷偏好
     * @return 最终结果集
     */
    private List<CollegeMajorGroupVO> selectCollegeMajorGroupVO(UserInfoDTO dto, UserQuestionnairePreferVO preferVO, Map<String, TProvince> provinceMap) {
        // 1. 查询当前省份是否是新高考省份
        TProvince tProvince = provinceMap.get(dto.getCandidateProvince());
        if (tProvince == null) {
            return null;
        }

        // 优化查询：招生省份和科类匹配提前在数据库筛选，提高查询效率
        List<CollegeMajorGroupVO> candidateList = aggregateCollegeMajorGroupVO(tProvince.getId(), dto.getFirstSubject());
        // 2. 用户输入硬过滤符合条件的院校专业组
        List<CollegeMajorGroupVO> hardFilterList = candidateList.stream()
                .filter(vo -> {
//                    // 条件1：招生省份 = 考生省份（VO.provinceId = TUserInfo.candidateProvinceId）
//                    if (!vo.getProvinceId().equals(tProvince.getId())) {
//                        return false;
//                    }
//                    // 条件2：科类匹配 = 考生首选科目（VO.subjectType = 考生firstSubject（物理/历史））
//                    if (!vo.getSubjectType().equals(dto.getFirstSubject())) {
//                        return false;
//                    }
                    // 条件3：选科要求匹配 → 考生选科组合包含专业组选科要求
                    return matchSubjectRequirement(vo.getSubjectRequirement(), dto.getSecondSubject());
                })
                .collect(Collectors.toList());

        // 3. 个性化偏好软过滤
        return hardFilterList.stream()
                .filter(vo -> {
                    // 1. 地域偏好过滤：院校所属省份在用户目标省份ID列表中（VO.collegeProvinceId ∈ preferVO.targetProvinceIds）
                    if (preferVO.getTargetProvinceIds() != null && !preferVO.getTargetProvinceIds().isEmpty()) {
                        if (!preferVO.getTargetProvinceIds().contains(vo.getCollegeProvinceId())) {
                            return false;
                        }
                    }
                    // 2. 院校层次过滤：用户有明确偏好则匹配（VO.schoolLevel = preferVO.targetSchoolLevel）
                    if (StringUtils.isNotBlank(preferVO.getTargetSchoolLevel()) && !"无明确层次偏好".equals(preferVO.getTargetSchoolLevel())) {
                        if (!vo.getSchoolLevel().equals(preferVO.getTargetSchoolLevel())) {
                            return false;
                        }
                    }
                    // 3. 院校性质过滤：用户优先公办则剔除民办/中外合作（VO.schoolNature 匹配偏好）
                    if (StringUtils.isNotBlank(preferVO.getTargetSchoolNature()) && !"无偏好".equals(preferVO.getTargetSchoolNature())) {
                        if (!vo.getSchoolNature().contains(preferVO.getTargetSchoolNature())) {
                            return false;
                        }
                    }
                    // 4. 学费过滤：院校最低学费 ≤ 用户接受的最高学费（VO.tuitionFeeMin ≤ preferVO.tuitionFeeMax）
                    if (preferVO.getTuitionFeeMax() != null && vo.getTuitionFeeMin() > preferVO.getTuitionFeeMax()) {
                        return false;
                    }
                    // 5. 专业大类过滤：用户有目标专业大类则匹配（VO.majorCategory ∈ preferVO.targetMajorCategories）
                    // 6. 避开专业大类过滤：用户有避开的专业则剔除
                    // 其他问卷偏好过滤（如是否接受异地校区、院校类型等，现在不加，后续可以补充）
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * 选科要求匹配
     * @param subjectRequirement 选科要求
     * @param secondSubject 输入科目
     * @return 是否匹配
     */
    private boolean matchSubjectRequirement(String subjectRequirement, String secondSubject) {
        // 1. 不限则直接通过
        if ("不限".equals(subjectRequirement)) {
            return true;
        }

        // 2. 处理输入选科组合：按逗号分割并去除空格
        List<String> inputSubjects = Arrays.stream(secondSubject.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        // 3. 解析要求中的科目和连接词
        List<String> requiredSubjects;
        boolean isAnd; // true表示“且”，false表示“或”

        if (subjectRequirement.contains("且")) {
            requiredSubjects = Arrays.asList(subjectRequirement.split("且"));
            isAnd = true;
        } else if (subjectRequirement.contains("或")) {
            requiredSubjects = Arrays.asList(subjectRequirement.split("或"));
            isAnd = false;
        } else {
            // 单个科目（如“化学”）
            requiredSubjects = Collections.singletonList(subjectRequirement);
            isAnd = true; // 单个视为“且”
        }

        // 去除要求中可能的空格
        requiredSubjects = requiredSubjects.stream()
                .map(String::trim)
                .collect(Collectors.toList());

        // 4. 执行匹配
        if (isAnd) {
            // 必须包含所有要求科目
            return inputSubjects.containsAll(requiredSubjects);
        } else {
            // 至少包含一个要求科目
            for (String req : requiredSubjects) {
                if (inputSubjects.contains(req)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 问卷答案的结构化转换
     * @param userId 系统用户ID
     * @return 转化后VO
     */
    private UserQuestionnairePreferVO questionnaireAnswerConvertToVO(Long userId) {
        // 1.1. 从问卷答案表查询当前用户的所有答案
        List<TQuestionnaireAnswer> userAnswerList = questionnaireAnswerService.lambdaQuery()
                .eq(TQuestionnaireAnswer::getUserId, userId)
                .list();
        // 1.2. 从问卷问题获取详细内容
        Map<Integer, TQuestionnaire> questionnaireMap = questionnaireService.list().stream()
                .collect(Collectors.toMap(TQuestionnaire::getId, Function.identity()));
        // 2. 初始化偏好VO
        UserQuestionnairePreferVO preferVO = new UserQuestionnairePreferVO();
        // 3. 遍历答案，按questionnaireId转换（示例：问题2=地域偏好，问题4=院校层次，问题12=学费）
        for (TQuestionnaireAnswer answer : userAnswerList) {
            Integer qId = answer.getQuestionnaireId();
            TQuestionnaire tQuestionnaire = questionnaireMap.get(qId);
            Integer sort = tQuestionnaire.getSort();
            String answerContent = answer.getAnswerContent();
            switch (sort) {
                case 2: // 问题2：地域偏好 → 转目标省份ID列表
                    List<Integer> provinceIds = convertAreaToProvinceIds(answerContent);
                    preferVO.setTargetProvinceIds(provinceIds);
                    break;
                case 3: // 问题3：接受的距离 → 直接赋值
                    preferVO.setDistanceAccept(answerContent);
                    break;
                case 4: // 问题4：院校层次 → 直接赋值
                    preferVO.setTargetSchoolLevel(answerContent);
                    break;
                case 5: // 问题5：目标院校性质 → 直接赋值
                    preferVO.setTargetSchoolNature(answerContent);
                    break;
                case 6: // 问题6：目标院校类型 → 分开即可
                    preferVO.setTargetCollegeTypes(Arrays.asList(answerContent.split(",")));
                    break;
                case 7: // 问题7：目标专业大类 → 分开即可
                    preferVO.setTargetMajorCategories(Arrays.asList(answerContent.split(",")));
                    break;
                case 8: // 问题8：专业选择优先级 → 直接赋值
                    preferVO.setMajorSelectPriority(answerContent);
                    break;
                case 9: // 问题9：避开的专业大类 → 直接赋值
                    preferVO.setAvoidMajorCategory(answerContent);
                    break;
                case 10: // 问题10：发展导向 → 直接赋值
                    preferVO.setDevelopDirection(answerContent);
                    break;
                case 11: // 问题11：目标就业行业 → 分开即可
                    preferVO.setTargetEmploymentIndustries(Arrays.asList(answerContent.split(",")));
                    break;
                case 12: // 问题12：学费范围 → 转最高学费数值
                    Integer maxTuition = convertTuitionToNum(answerContent); // 自定义方法：“5000及以下”→5000，“20001及以上”→Integer.MAX_VALUE
                    preferVO.setTuitionFeeMax(maxTuition);
                    break;
                case 13: // 问题10：是否接受专业调剂 → 直接赋值
                    preferVO.setMajorAdjustAccept(answerContent);
                    break;
                case 14: // 问题14：住宿要求 → 直接赋值
                    preferVO.setDormRequire(answerContent);
                    break;
                case 15: // 问题15：是否关注专项计划 → 直接判断
                    preferVO.setFocusSpecialPlan(answerContent.contains("非常"));
                    break;
                case 16: // 问题16：专业热度偏好 → 直接赋值
                    preferVO.setMajorHotLevel(answerContent);
                    break;
                case 17: // 问题17：是否关注就业率/考研率 → 直接判断
                    preferVO.setFocusEmploymentRate(!answerContent.contains("不"));
                    break;
                case 18: // 问题18：是否关注校园环境 → 直接赋值
                    preferVO.setFocusCampusEnv(!answerContent.contains("不"));
                    break;
                case 19: // 问题10：是否接受异地校区 → 直接赋值
                    preferVO.setAcceptDifferentCampus(!answerContent.contains("不"));
                    break;
                case 20: // 问题20：其他偏好 → 直接赋值
                    preferVO.setOtherPrefer(answerContent);
                    break;
                default:
                    break;
            }
        }
        return preferVO;
    }

    /**
     * 地区转换
     * @param answer 地区回答
     * @return 具体地区id
     */
    private List<Integer> convertAreaToProvinceIds(String answer) {
        if (answer.contains("无明确地域偏好")) {
            return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34);
        }
        List<Integer> ans = new ArrayList<>();
        if (answer.contains("港澳台")) {
            ans.addAll(Arrays.asList(32, 33, 34));
        }
        if (answer.contains("华东地区（苏浙皖沪闽赣鲁）")) {
            ans.addAll(Arrays.asList(9, 10, 11, 12, 13, 14, 15));
        }
        if (answer.contains("华北地区（京津冀晋蒙）")) {
            ans.addAll(Arrays.asList(1, 2, 3, 4, 5));
        }
        if (answer.contains("华南地区（粤桂琼）")) {
            ans.addAll(Arrays.asList(19, 20, 21));
        }
        if (answer.contains("华中地区（豫鄂湘）")) {
            ans.addAll(Arrays.asList(16, 17, 18));
        }
        if (answer.contains("西南地区（川渝云贵藏）")) {
            ans.addAll(Arrays.asList(22, 23, 24, 25, 26));
        }
        if (answer.contains("西北地区（陕甘青宁新）")) {
            ans.addAll(Arrays.asList(27, 28, 29, 30, 31));
        }
        if (answer.contains("东北地区（黑吉辽）")) {
            ans.addAll(Arrays.asList(6, 7, 8));
        }
        // 如果没有匹配任何地区，则返回所有省份
        if (ans.isEmpty()) {
            return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34);
        }
        return ans;
    }

    /**
     * 学费转换
     * @param answer 学费回答
     * @return 具体学费
     */
    private Integer convertTuitionToNum(String answer) {
        if ("5000元及以下".equals(answer)) {
            return 5000;
        } else if ("5001-10000元".equals(answer)) {
            return 10000;
        } else if ("10001-20000元".equals(answer)) {
            return 20000;
        } else if ("20001元及以上".equals(answer)) {
            return Integer.MAX_VALUE;
        } else {
            return Integer.MAX_VALUE; // 无限制
        }
    }

    /**
     * 将录取数据、招生计划、院校信息通过唯一标识关联，后续所有处理都基于这个 VO，避免多次联表查询，提升效率
     * @param provinceId 省份ID
     * @param subjectType 考生科类
     * @return 院校专业组基础信息对象VO
     */
    private List<CollegeMajorGroupVO> aggregateCollegeMajorGroupVO(Integer provinceId, String subjectType) {
        Map<String, Map<Integer, TScoreRank>> scoreRankMap = cleanScoreRank(provinceId, subjectType);
        List<TAdmissionData> cleanAdmissionList = cleanAdmissionData(scoreRankMap, provinceId, subjectType);
        List<TEnrollmentPlan> cleanPlanList = cleanEnrollmentPlan(provinceId, subjectType);
        List<TCollege> cleanCollegeList = cleanCollege();
        Map<String, TCollege> collegeMap = cleanCollegeList.stream()
                .collect(Collectors.toMap(TCollege::getCollegeCode, college -> college, (v1, v2) -> v1));
        // 按聚合主键聚合录取数据为专业组维度的录取数据（Map的key为聚合主键字符串，value为聚合后的录取数据）
        Map<String, TAdmissionData> admissionGroupMap = cleanAdmissionList.stream()
                .collect(Collectors.toMap(
                        // 生成聚合主键字符串：省份ID-年份-科类-批次-院校代码-专业组代码
                        data -> data.getProvinceId() + "-" + data.getYear() + "-" + data.getSubjectType() + "-"
                                + data.getBatch() + "-" + data.getCollegeCode() + "-" + data.getMajorGroupCode(),
                        // 初始值：当前数据
                        data -> data,
                        // 聚合规则：若同一聚合主键有多个数据（专业粒度），取最低分/最低排名的那条
                        (data1, data2) -> {
                            TAdmissionData result = new TAdmissionData();
                            // 核心：取组内最低投档分、最低投档排名（高考专业组投档线定义）
                            result.setLowestAdmissionScore(Math.min(data1.getLowestAdmissionScore(), data2.getLowestAdmissionScore()));
                            result.setLowestAdmissionRank(Math.min(data1.getLowestAdmissionRank(), data2.getLowestAdmissionRank()));
                            // 复用其他统一字段（聚合主键字段）
                            result.setProvinceId(data1.getProvinceId());
                            result.setYear(data1.getYear());
                            result.setSubjectType(data1.getSubjectType());
                            result.setBatch(data1.getBatch());
                            result.setCollegeCode(data1.getCollegeCode());
                            result.setMajorGroupCode(data1.getMajorGroupCode());
                            //其他数据（最终聚合VO不需要，后续会忽略）
                            result.setBatchRemark(data1.getBatchRemark());
                            result.setCollegeName(data1.getCollegeName());
                            result.setMajorCode(data1.getMajorCode());
                            result.setMajorName(data1.getMajorName());
                            return result;
                        }
                ));
        // 转换为聚合后的专业组维度录取数据列表
        List<TAdmissionData> groupAdmissionList = new ArrayList<>(admissionGroupMap.values());
        // 按聚合主键聚合招生计划成EnrollmentGroupVO
        Map<String, EnrollmentGroupVO> planGroupMap = cleanPlanList.stream()
                .collect(Collectors.toMap(
                        // 生成和录取数据一致的聚合主键字符串
                        plan -> plan.getProvinceId() + "-" + plan.getYear() + "-" + plan.getSubjectType() + "-"
                                + plan.getBatch() + "-" + plan.getCollegeCode() + "-" + plan.getMajorGroupCode(),
                        // 初始值：转换为EnrollmentGroupVO
                        plan -> {
                            EnrollmentGroupVO groupVO = new EnrollmentGroupVO();
                            // 聚合主键字段
                            groupVO.setProvinceId(plan.getProvinceId());
                            groupVO.setYear(plan.getYear());
                            groupVO.setSubjectType(plan.getSubjectType());
                            groupVO.setBatch(plan.getBatch());
                            groupVO.setCollegeCode(plan.getCollegeCode());
                            groupVO.setMajorGroupCode(plan.getMajorGroupCode());
                            // 聚合字段初始化
                            groupVO.setTotalPlanCount(plan.getPlanCount());
                            groupVO.setSubjectRequirement(plan.getSubjectRequirement());
                            groupVO.setTuitionFeeMin(Integer.parseInt(plan.getTuitionFee()));
                            groupVO.setTuitionFeeMax(Integer.parseInt(plan.getTuitionFee()));
                            groupVO.setTuitionFeeAvg(new BigDecimal(plan.getTuitionFee()));
                            groupVO.setSchoolSystem(plan.getSchoolSystem());
                            return groupVO;
                        },
                        // 聚合规则：同一聚合主键，累加计划数、更新学费最值、计算平均值
                        (vo1, vo2) -> {
                            // 计划数求和
                            vo1.setTotalPlanCount(vo1.getTotalPlanCount() + vo2.getTotalPlanCount());
                            // 学费取最值
                            vo1.setTuitionFeeMin(Math.min(vo1.getTuitionFeeMin(), vo2.getTuitionFeeMin()));
                            vo1.setTuitionFeeMax(Math.max(vo1.getTuitionFeeMax(), vo2.getTuitionFeeMax()));
                            // 学费平均值：直接累加后求平均
                            vo1.setTuitionFeeAvg(vo1.getTuitionFeeAvg().add(vo2.getTuitionFeeAvg()).divide(new BigDecimal(2), 2, RoundingMode.HALF_UP));
                            // 学制取主流值（简化：取第一个，毕业项目无需复杂处理）
                            return vo1;
                        }
                ));
        // 多表关联：按聚合主键匹配录取数据和招生计划，按院校代码匹配院校信息
        List<CollegeMajorGroupVO> finalCollegeMajorGroupVOList = groupAdmissionList.stream()
                .map(admission -> {
                    // 1. 生成聚合主键，匹配对应的招生计划聚合VO
                    String groupKey = admission.getProvinceId() + "-" + admission.getYear() + "-" + admission.getSubjectType() + "-"
                            + admission.getBatch() + "-" + admission.getCollegeCode() + "-" + admission.getMajorGroupCode();
                    EnrollmentGroupVO planVO = planGroupMap.get(groupKey);
                    if (planVO == null) {
                        return null; // 无招生计划的专业组，直接剔除
                    }

                    // 2. 按院校代码匹配院校信息
                    TCollege college = collegeMap.get(admission.getCollegeCode());
                    if (college == null) {
                        return null; // 无院校信息的，直接剔除
                    }

                    // 3. 组装修正后的CollegeMajorGroupVO
                    CollegeMajorGroupVO vo = new CollegeMajorGroupVO();
                    // 院校信息
                    vo.setCollegeCode(college.getCollegeCode());
                    vo.setCollegeName(college.getCollegeName());
                    vo.setCollegeProvinceId(college.getProvinceId());
                    vo.setSchoolLevel(college.getSchoolLevel());
                    vo.setSchoolNature(college.getSchoolNature());
                    vo.setCollegeType(college.getCollegeType());
                    // 专业组核心信息（聚合主键+招生计划）
                    vo.setMajorGroupCode(admission.getMajorGroupCode());
                    vo.setProvinceId(admission.getProvinceId());
                    vo.setYear(admission.getYear());
                    vo.setBatch(admission.getBatch());
                    vo.setSubjectType(admission.getSubjectType());
                    vo.setSubjectRequirement(planVO.getSubjectRequirement());
                    vo.setTotalPlanCount(planVO.getTotalPlanCount());
                    vo.setTuitionFeeMin(planVO.getTuitionFeeMin());
                    vo.setTuitionFeeMax(planVO.getTuitionFeeMax());
                    vo.setTuitionFeeAvg(planVO.getTuitionFeeAvg());
                    // 录取核心信息
                    vo.setLowestAdmissionScore(admission.getLowestAdmissionScore());
                    vo.setLowestAdmissionRank(admission.getLowestAdmissionRank());

                    return vo;
                })
                .filter(Objects::nonNull) // 剔除关联失败的空数据
                .collect(Collectors.toList());
        return finalCollegeMajorGroupVOList;
    }

    /**
     * 录取数据清洗：分数不能为0/Null，年份为近3年（2023-2025，预测2026用），批次/科类为空
     * @param scoreRankMap 一分一段映射map
     * @param provinceId 省份ID
     * @param subjectType 考生科类
     * @return 2023-2025录取数据
     */
    private List<TAdmissionData> cleanAdmissionData(Map<String, Map<Integer, TScoreRank>> scoreRankMap, Integer provinceId, String subjectType) {
        List<TAdmissionData> admissionList = admissionDataService.lambdaQuery()
                .eq(TAdmissionData::getProvinceId, provinceId)
                .eq(TAdmissionData::getSubjectType, subjectType)
                .in(TAdmissionData::getYear, 2023, 2024, 2025)
                .isNotNull(TAdmissionData::getBatch)
                .isNotNull(TAdmissionData::getSubjectType)
                .list();
        return admissionList.stream()
                .filter(data -> data.getLowestAdmissionScore() != null && data.getLowestAdmissionScore() != 0)
                .peek(data -> {
                    // 补充缺失的投档最低排位
                    if (data.getLowestAdmissionRank() == null || data.getLowestAdmissionRank() == 0) {
                        // 构建外层Map的Key：provinceId_year_subjectType_batch
                        String key = data.getProvinceId() + "_" + data.getYear() + "_"
                                + data.getSubjectType() + "_" + data.getBatch();
                        Map<Integer, TScoreRank> scoreToRankMap = scoreRankMap.get(key);
                        if (scoreToRankMap != null) {
                            TScoreRank rankInfo = scoreToRankMap.get(data.getLowestAdmissionScore());
                            if (rankInfo != null) {
                                //最低排位 = 累计人数 - 本段人数 + 1
                                data.setLowestAdmissionRank(rankInfo.getCumulativeCount() - rankInfo.getScoreSegmentCount() + 1);
                            }
                        }
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 招生计划清洗：计划人数≥1，选科要求/学费不为空，近3年数据，批次不为空，科类不为空
     * @param provinceId 省份ID
     * @param subjectType 考生科类
     * @return 2023-2025招生计划数据
     */
    private List<TEnrollmentPlan> cleanEnrollmentPlan(Integer provinceId, String subjectType) {
        List<TEnrollmentPlan> planList = enrollmentPlanService.lambdaQuery()
                .eq(TEnrollmentPlan::getProvinceId, provinceId)
                .eq(TEnrollmentPlan::getSubjectType, subjectType)
                .in(TEnrollmentPlan::getYear, 2023, 2024, 2025)
                .in(TEnrollmentPlan::getSubjectType, "物理", "历史")
                .ge(TEnrollmentPlan::getPlanCount, 1)
                .isNotNull(TEnrollmentPlan::getSubjectRequirement)
                .isNotNull(TEnrollmentPlan::getTuitionFee)
                .list();
        return planList.stream()
                .peek(plan -> {
                    // 2. 标准化：学费转数值型（原字段是String，比如“5000”/“12000”，转Integer方便后续学费筛选）
                    try {
                        // 处理特殊情况（后续筛选时跳过）
                        String originalTuition = plan.getTuitionFee().trim();
                        String pureNum = extractDigits(originalTuition);
                        plan.setTuitionFee(pureNum.isEmpty() ? "0" : pureNum);
                    } catch (Exception e) {
                        plan.setTuitionFee("0");
                    }
                    // 3. 选科要求标准化：统一格式如“化学且生物”或者是“化学或生物”
                    String originalReq = plan.getSubjectRequirement();
                    String standardReq = standardizeSubject(originalReq);
                    plan.setSubjectRequirement(standardReq);
                })
                .collect(Collectors.toList());
    }

    /**
     * 过滤collegeCode/schoolLevel/schoolNature/collegeType为空的院校，核心字段无异常值；
     * @return 名称映射详情：
     * key是院校名称（已判断唯一）
     * value是院校详细
     */
    private List<TCollege> cleanCollege() {
        List<TCollege> colleges = collegeService.list();
        return colleges.stream()
                .filter(college -> StringUtils.isNotBlank(college.getCollegeCode())
                        && StringUtils.isNotBlank(college.getSchoolLevel())
                        && StringUtils.isNotBlank(college.getSchoolNature()))
                .collect(Collectors.toList());
    }

    /**
     * 过滤score/cumulativeCount为 0/Null 的数据，按provinceId+year+subjectType+batch分组，保证同维度分数排名唯一；
     * @param provinceId 省份ID
     * @param subjectType 考生科类
     * @return 分组后数据：
     * 外层key是provinceId_year_subjectType_batch，外层value是当前分组数据：
     * 内层key是score，内层value是当前行值
     */
    private Map<String, Map<Integer, TScoreRank>> cleanScoreRank(Integer provinceId, String subjectType) {
        List<TScoreRank> scoreRanks = scoreRankService.lambdaQuery()
                .eq(TScoreRank::getProvinceId, provinceId)
                .eq(TScoreRank::getSubjectType, subjectType)
                .in(TScoreRank::getYear, 2023, 2024, 2025)
                .isNotNull(TScoreRank::getProvinceId)
                .isNotNull(TScoreRank::getSubjectType)
                .isNotNull(TScoreRank::getBatch)
                .ge(TScoreRank::getScore, 1)        // 分数 >0
                .ge(TScoreRank::getCumulativeCount, 1) // 累计人数 >0
                .list();

        return scoreRanks.stream()
                // 分组并保留每组唯一数据
                .collect(Collectors.groupingBy(
                        // 外层键：拼接维度字符串（provinceId_year_subjectType_batch）
                        rank -> String.format("%d_%d_%s_%s",
                                rank.getProvinceId(),
                                rank.getYear(),
                                rank.getSubjectType(),
                                rank.getBatch()
                        ),
                        // 内层：按score去重并构建分数-记录Map（重复分数保留第一条）
                        Collectors.toMap(
                                TScoreRank::getScore,  // 内层键：score（分数）
                                t -> t,               // 内层值：TScoreRank记录
                                (t1, t2) -> t1        // 重复分数策略：保留第一条
                        )
                ));
    }

    /**
     * 保留isNewGaokao=1（新高考省份）的数据，贴合 3+1+2 新高考场景。
     * @return id映射详情：
     * key是id
     * value是省份详情
     */
    private List<TProvince> cleanProvince() {
        List<TProvince> provinces = provinceService.list();
        return provinces.stream()
                .filter(province -> province.getIsNewGaokao() != 0
                ).collect(Collectors.toList());
    }

    /**
     * 工具方法：仅提取字符串中的纯数字（剔除所有非数字字符），无数字返回空字符串
     * @param str 原始学费字符串
     * @return 纯数字字符串，无数字则返回空
     */
    private String extractDigits(String str) {
        if (str == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (c >= '0' && c <= '9') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 选科标准化
     * @param originalReq 原始选考要求
     * @return 标准化后的选考要求
     */
    private String standardizeSubject(String originalReq) {
        // 去所有空格（避免空格影响contains匹配）
        String cleanReq = originalReq.replaceAll("\\s+", "");

        // 收集存在的科目
        StringBuilder subjectSb = new StringBuilder();
        if (cleanReq.contains("化")) {
            subjectSb.append("化学,"); // 匹配化/化学
        }
        if (cleanReq.contains("生")) {
            subjectSb.append("生物,"); // 匹配生/生物/生物学
        }
        if (cleanReq.contains("政")) {
            subjectSb.append("政治,"); // 匹配政/政治/思想政治
        }
        if (cleanReq.contains("地")) {
            subjectSb.append("地理,"); // 匹配地/地理
        }

        // 空科目判断→返回不限
        if (subjectSb.length() == 0) {
            return "不限";
        }

        // 处理科目字符串，去末尾逗号，转列表
        String subjectStr = subjectSb.substring(0, subjectSb.length() - 1);
        List<String> subjectList = Arrays.asList(subjectStr.split(","));

        // contains判断或/∕，识别连接符类型
        boolean hasOr = cleanReq.contains("或") || cleanReq.contains("/");

        // 按规则拼接→单科目直显、多科目有或拼或、无或拼且
        if (subjectList.size() == 1) {
            return subjectList.get(0);
        } else if (hasOr) {
            return String.join("或", subjectList);
        } else {
            return String.join("且", subjectList);
        }
    }
}
