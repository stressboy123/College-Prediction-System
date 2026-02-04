package com.gdut.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.gdut.entity.ExcelAdmissionDataEntity;
import com.gdut.entity.ExcelCollegeDetailEntity;
import com.gdut.entity.ExcelCollegeEntity;
import com.gdut.entity.ExcelMajorEntity;
import com.gdut.entity.ExcelRawData;
import com.gdut.entity.Result;
import com.gdut.entity.TAdmissionData;
import com.gdut.entity.TCollege;
import com.gdut.entity.TEnrollmentPlan;
import com.gdut.entity.TMajor;
import com.gdut.entity.TProvince;
import com.gdut.service.ExcelService;
import com.gdut.service.ITAdmissionDataService;
import com.gdut.service.ITCollegeService;
import com.gdut.service.ITEnrollmentPlanService;
import com.gdut.service.ITMajorService;
import com.gdut.service.ITProvinceService;
import com.gdut.utils.ExcelReadUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author liujunliang
 * @date 2026/1/31
 */
@Service
public class ExcelServiceImpl implements ExcelService {
    // 定义首选科目集合（固定：物理/历史）
    private static final Set<String> FIRST_SUBJECTS = Collections.unmodifiableSet(
            new HashSet<String>() {{
                add("物理");
                add("历史");
            }}
    );
    // 定义再选科目集合（固定：思想政治/地理/化学/生物）
    private static final Set<String> SECOND_SUBJECTS = Collections.unmodifiableSet(
            new HashSet<String>() {{
                add("思想政治");
                add("地理");
                add("化学");
                add("生物");
            }}
    );
    @Resource
    private ITProvinceService provinceService;
    @Resource
    private ITCollegeService collegeService;
    @Resource
    private ITMajorService majorService;
    @Resource
    private ITEnrollmentPlanService enrollmentPlanService;
    @Resource
    private ITAdmissionDataService admissionDataService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<String> addCollege() {
        try {
            List<TProvince> list = provinceService.list();
            Map<String, Integer> provinceMap = new HashMap<>();
            for (TProvince tProvince : list) {
                String name = tProvince.getProvinceName();
                if ("内蒙古自治区".equals(name) || "黑龙江省".equals(name)) {
                    provinceMap.put(name.substring(0, 3), tProvince.getId());
                } else {
                    provinceMap.put(name.substring(0, 2), tProvince.getId());
                }
            }

            // 初始大量导入写死路径，后续不使用这种方式导入
            String collegePath = "D:/教材/毕业论文/毕业论文数据/院校数据/2025全国普通高等学校名单.xls";
            File collegeFile = new File(collegePath);
            String collegeDetailPath = "D:/教材/毕业论文/毕业论文数据/院校数据/院校库.xlsx";
            File collegeDetailFile = new File(collegeDetailPath);
            String add = "D:/教材/毕业论文/毕业论文数据/院校数据/补充.xlsx";
            File addFile = new File(add);
            // 院校主数据
            List<ExcelCollegeEntity> mainTable = ExcelReadUtil.readForExcelCollege(collegeFile, 0, 3, ExcelTypeEnum.XLS);
            // 院校详细数据
            List<ExcelCollegeDetailEntity> detail = ExcelReadUtil.readForExcelCollegeDetail(collegeDetailFile, 0, 1, ExcelTypeEnum.XLSX);
            // 院校补充数据
            List<ExcelRawData> data = ExcelReadUtil.readForExcelAllSheetOrigin(addFile, 1, ExcelTypeEnum.XLSX);
            Map<String, ExcelCollegeDetailEntity> map = detail.stream().collect(Collectors.toMap(ExcelCollegeDetailEntity::getCollegeName, Function.identity()));
            List<TCollege> collegeList = new ArrayList<>();
            String curProvince = "";
            for (ExcelCollegeEntity excelCollegeEntity : mainTable) {
                if (excelCollegeEntity.getCollegeName() == null) {
                    String name = excelCollegeEntity.getSerialNumber();
                    if (name.contains("内蒙古自治区") || name.contains("黑龙江省")) {
                        curProvince = name.substring(0, 3);
                    } else {
                        curProvince = name.substring(0, 2);
                    }
                    continue;
                }
                // 序号	学校名称	学校标识码	主管部门	所在地	办学层次	备注
                // 学校名称	教育行政主管部门	院校特性	所在地	详细地址	官方网址	招生网址	官方电话	院校满意度	专业满意度	专业推荐人数	专业推荐指数
                // 院校名称	国标代码	层次	办学性质	特殊说明
                TCollege college = new TCollege();
                String collegeCode = excelCollegeEntity.getCollegeCode();
                college.setCollegeCode(collegeCode.substring(collegeCode.length() - 5));
                college.setCollegeName(excelCollegeEntity.getCollegeName());
                college.setCompetentAuthority(excelCollegeEntity.getCollegeDept());
                if (provinceMap.containsKey(curProvince)) {
                    college.setProvinceId(provinceMap.get(curProvince));
                }
                college.setSchoolLevel(excelCollegeEntity.getCollegeLevel());
                college.setSchoolNature(excelCollegeEntity.getCollegeRemark() == null ? "公办" : excelCollegeEntity.getCollegeRemark());
                ExcelCollegeDetailEntity detailEntity = map.get(excelCollegeEntity.getCollegeName());
                if (detailEntity != null) {
                    college.setCollegeType(detailEntity.getCollegeCharacter());
                    college.setDetailedAddress(detailEntity.getCollegeAddress());
                    college.setOfficialWebsite(detailEntity.getCollegeWebsite());
                    college.setEnrollmentWebsite(detailEntity.getCollegeEnrollWebsite());
                    college.setOfficialPhone(detailEntity.getCollegePhone());
                    college.setCollegeSatisfaction(detailEntity.getCollegeSatisfaction());
                    college.setMajorSatisfaction(detailEntity.getCollegeMajorSatisfaction());
                    college.setMajorRecommendCount(detailEntity.getCollegeMajorRecommendNum());
                    college.setMajorRecommendIndex(detailEntity.getCollegeMajorRecommendIndex());
                }
                collegeList.add(college);
            }
            for (ExcelRawData excelRawData : data) {
                String collegeName = excelRawData.getCol0();
                if (map.containsKey(collegeName)) {
                    TCollege college = new TCollege();
                    ExcelCollegeDetailEntity detailEntity = map.get(collegeName);
                    college.setCollegeCode(excelRawData.getCol1());
                    college.setCollegeName(detailEntity.getCollegeName());
                    college.setCompetentAuthority(detailEntity.getCollegeDept());
                    if (provinceMap.containsKey(detailEntity.getCollegeLocation())) {
                        college.setProvinceId(provinceMap.get(detailEntity.getCollegeLocation()));
                    }
                    college.setSchoolLevel(excelRawData.getCol2());
                    college.setSchoolNature(excelRawData.getCol3());
                    college.setCollegeType(detailEntity.getCollegeCharacter());
                    college.setDetailedAddress(detailEntity.getCollegeAddress());
                    college.setOfficialWebsite(detailEntity.getCollegeWebsite());
                    college.setEnrollmentWebsite(detailEntity.getCollegeEnrollWebsite());
                    college.setOfficialPhone(detailEntity.getCollegePhone());
                    college.setCollegeSatisfaction(detailEntity.getCollegeSatisfaction());
                    college.setMajorSatisfaction(detailEntity.getCollegeMajorSatisfaction());
                    college.setMajorRecommendCount(detailEntity.getCollegeMajorRecommendNum());
                    college.setMajorRecommendIndex(detailEntity.getCollegeMajorRecommendIndex());
                    collegeList.add(college);
                }
            }
            return collegeService.saveBatch(collegeList) ? Result.success() : Result.failWithOnlyMsg("插入失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failWithOnlyMsg("执行失败：" + e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<String> addMajor() {
        try {
            List<TMajor> majors = new ArrayList<>();
            String chineseFolderPath = "D:/教材/毕业论文/毕业论文数据/专业数据/掌上高考";
            File folder = new File(chineseFolderPath);
            if (!folder.exists()) {
                return Result.failWithOnlyMsg("文件夹不存在：" + chineseFolderPath);
            }
            if (!folder.isDirectory()) {
                return Result.failWithOnlyMsg("路径不是文件夹：" + chineseFolderPath);
            }
            File[] allFiles = folder.listFiles();
            if (allFiles == null || allFiles.length == 0) {
                return Result.failWithOnlyMsg("当前文件夹无任何文件：" + chineseFolderPath);
            }
            for (File file : allFiles) {
                // 专业数据
                List<ExcelMajorEntity> excelData = ExcelReadUtil.readForExcelMajor(file, 0, 1, ExcelTypeEnum.XLSX);
                for (ExcelMajorEntity excelMajorEntity : excelData) {
                    TMajor major = new TMajor();
                    major.setMajorCategory(excelMajorEntity.getMajorCategory());
                    major.setMajorType(excelMajorEntity.getMajorClass() == null ? "" : excelMajorEntity.getMajorClass());
                    major.setMajorName(excelMajorEntity.getMajorName());
                    major.setMajorCode(excelMajorEntity.getMajorCode());
                    major.setEducationLength(excelMajorEntity.getMajorYears());
                    major.setDegreeAwarded(excelMajorEntity.getMajorDegree());
                    major.setAverageSalary(excelMajorEntity.getMajorSalary());
                    major.setPostgraduateDirection(excelMajorEntity.getMajorKaoYan());
                    major.setMajorIntro(excelMajorEntity.getMajorIntro());
                    major.setComprehensiveSatisfaction(new BigDecimal(excelMajorEntity.getMajorSatisfaction() == null ? "0.0" :excelMajorEntity.getMajorSatisfaction()));
                    major.setSchoolCondition(new BigDecimal(excelMajorEntity.getMajorCondition() == null ? "0.0" :excelMajorEntity.getMajorCondition()));
                    major.setTeachingQuality(new BigDecimal(excelMajorEntity.getMajorQuality() == null ? "0.0" :excelMajorEntity.getMajorQuality()));
                    major.setEmploymentSituation(new BigDecimal(excelMajorEntity.getMajorEmployment() == null ? "0.0" :excelMajorEntity.getMajorEmployment()));
                    major.setThreeYearEmploymentRate(excelMajorEntity.getMajorEmploymentRate());
                    majors.add(major);
                }
            }
            return majorService.saveBatch(majors) ? Result.success() : Result.failWithOnlyMsg("插入失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failWithOnlyMsg("执行失败：" + e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<String> addEnrollmentPlan() {
        try {
            List<TProvince> list = provinceService.list();
            Map<String, Integer> map = list.stream().collect(Collectors.toMap(TProvince::getProvinceName, TProvince::getId));
            List<TEnrollmentPlan> enrollmentPlans = new ArrayList<>();
            String path = "D:/教材/毕业论文/毕业论文数据/当前传输";
            File folder = new File(path);
            if (!folder.exists()) {
                return Result.failWithOnlyMsg("文件夹不存在：" + path);
            }
            if (!folder.isDirectory()) {
                return Result.failWithOnlyMsg("路径不是文件夹：" + path);
            }
            File[] allFiles = folder.listFiles();
            if (allFiles == null || allFiles.length == 0) {
                return Result.failWithOnlyMsg("当前文件夹无任何文件：" + path);
            }
            for (File excelFile : allFiles) {
                String name = excelFile.getName();
                List<ExcelRawData> data = ExcelReadUtil.readForExcelAllSheetOrigin(excelFile, 1, ExcelTypeEnum.XLSX);
                for (ExcelRawData excelRawData : data) {
                    TEnrollmentPlan tEnrollmentPlan = new TEnrollmentPlan();
                    if (name.contains("2024")) {
                        tEnrollmentPlan.setYear(2024);
                        tEnrollmentPlan.setProvinceId(map.get("黑龙江省"));
                        // 批次
                        String batchRemark = excelRawData.getCol0();
                        String batch = "";
                        if (batchRemark.contains("本科批")) {
                            batch = "本科批";
                        } else if (batchRemark.contains("本科提前批")) {
                            batch = "本科提前批";
                        } else if (batchRemark.contains("高职(专科)批")) {
                            batch = "专科批";
                        } else if (batchRemark.contains("高职(专科)提前批")) {
                            batch = "专科提前批";
                        }
                        tEnrollmentPlan.setBatch(batch);
                        tEnrollmentPlan.setBatchRemark(batchRemark);
                        // 学校名称
                        tEnrollmentPlan.setCollegeName(excelRawData.getCol1());
                        // 学校代码
                        tEnrollmentPlan.setCollegeCode(excelRawData.getCol2());
                        // 学校方向
                        // 学校招生数
                        // 专业组代码
                        tEnrollmentPlan.setMajorGroupCode(excelRawData.getCol5());
                        // 专业组名称
                        // 专业组人数
                        // 专业组再选
                        String col8 = excelRawData.getCol8();
                        String subjectRequirement = "不限";
                        if ("化学".equals(col8) || "化学和生物".equals(col8) || "生物".equals(col8) || "生物和地理".equals(col8) || "地理".equals(col8) || "思想政治和地理".equals(col8) || "思想政治".equals(col8)) {
                            subjectRequirement = col8;
                        }
                        tEnrollmentPlan.setSubjectRequirement(subjectRequirement);
                        // 专业代码
                        tEnrollmentPlan.setMajorCode(excelRawData.getCol9());
                        // 专业名称
                        tEnrollmentPlan.setMajorName(excelRawData.getCol10() == null ? "" : excelRawData.getCol10());
                        // 学制
                        tEnrollmentPlan.setSchoolSystem(excelRawData.getCol11() == null ? 0 : Integer.parseInt(excelRawData.getCol11()));
                        // 计划人数
                        tEnrollmentPlan.setPlanCount(excelRawData.getCol12() == null ? 0 : Integer.parseInt(excelRawData.getCol12()));
                        // 学费
                        tEnrollmentPlan.setTuitionFee(excelRawData.getCol13() == null ? "" : excelRawData.getCol13());
                        // 专业备注
                        tEnrollmentPlan.setMajorRemark(excelRawData.getCol14());
                        // 选科
                        tEnrollmentPlan.setSubjectType(excelRawData.getCol15());
                    } else if (name.contains("2025")) {
                        tEnrollmentPlan.setYear(2025);
                        tEnrollmentPlan.setProvinceId(map.get("黑龙江省"));
                        // 批次名称
                        String batchRemark = excelRawData.getCol0();
                        String batch = "";
                        if (batchRemark.contains("本科批")) {
                            batch = "本科批";
                        } else if (batchRemark.contains("高职(专科)批")) {
                            batch = "专科批";
                        } else if (batchRemark.contains("高职(专科)提前批") || "省内公安院校".equals(batchRemark)) {
                            batch = "专科提前批";
                        } else {
                            batch = "本科提前批";
                        }
                        tEnrollmentPlan.setBatch(batch);
                        tEnrollmentPlan.setBatchRemark(batchRemark);
                        // 本专
                        // 文理
                        tEnrollmentPlan.setSubjectType(excelRawData.getCol2());
                        // 院校代码
                        tEnrollmentPlan.setCollegeCode(excelRawData.getCol3());
                        // 专业组代码
                        String col4 = excelRawData.getCol4();
                        String majorGroupCode = col4.replaceAll("\\D+", "");
                        tEnrollmentPlan.setMajorGroupCode(majorGroupCode);
                        // 院校名称
                        tEnrollmentPlan.setCollegeName(excelRawData.getCol5());
                        // 专业代码
                        tEnrollmentPlan.setMajorCode(excelRawData.getCol6());
                        // 专业名称
                        tEnrollmentPlan.setMajorName(excelRawData.getCol7());
                        // 备注
                        tEnrollmentPlan.setMajorRemark(excelRawData.getCol8() == null ? "" : excelRawData.getCol8());
                        // 选科
                        tEnrollmentPlan.setSubjectRequirement(excelRawData.getCol9() == null ? "不限" : excelRawData.getCol9());
                        // 计划人数
                        tEnrollmentPlan.setPlanCount(excelRawData.getCol10() == null ? 0 : Integer.parseInt(excelRawData.getCol10()));
                        // 学制
                        String col11 = excelRawData.getCol11();
                        int schoolSystem = 0;
                        if (col11.contains("一")) {
                            schoolSystem = 1;
                        } else if (col11.contains("二")) {
                            schoolSystem = 2;
                        } else if (col11.contains("三")) {
                            schoolSystem = 3;
                        } else if (col11.contains("四")) {
                            schoolSystem = 4;
                        } else if (col11.contains("五")) {
                            schoolSystem = 5;
                        } else if (col11.contains("六")) {
                            schoolSystem = 6;
                        } else if (col11.contains("七")) {
                            schoolSystem = 7;
                        } else if (col11.contains("八")) {
                            schoolSystem = 8;
                        } else if (col11.contains("九")) {
                            schoolSystem = 9;
                        }
                        tEnrollmentPlan.setSchoolSystem(schoolSystem);
                        // 学费
                        tEnrollmentPlan.setTuitionFee(excelRawData.getCol12() == null ? "" : excelRawData.getCol12());
                    }
                    enrollmentPlans.add(tEnrollmentPlan);
                }
            }
            List<TEnrollmentPlan> temp = new ArrayList<>();
            try {
                for (int i = 0; i < enrollmentPlans.size(); i++) {
                    temp.add(enrollmentPlans.get(i));
                    if (temp.size() >= 1000 || i == enrollmentPlans.size() - 1) {
                        enrollmentPlanService.saveBatch(temp);
                        temp.clear();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Result.failWithOnlyMsg("执行失败：" + e.getMessage());
            }
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failWithOnlyMsg("执行失败：" + e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<String> getAdmissionData() {
        try {
            List<TAdmissionData> admissionData = new ArrayList<>();
            List<TProvince> list = provinceService.list();
            Map<String, Integer> map = list.stream().collect(Collectors.toMap(TProvince::getProvinceName, TProvince::getId));
            String path = "D:/教材/毕业论文/毕业论文数据/当前传输";
            String province = "广东省";
            int year = 2023;
            String batch = "本科批";
            String batchRemark = "本科";
            String subjectType = "物理";
            int headRowNum = 2;
            ExcelTypeEnum type = ExcelTypeEnum.XLSX;
            String folderPath = path + "/" + province + "/" + year + "/" + batch;
            File folder = new File(folderPath);
            if (!folder.exists()) {
                return Result.failWithOnlyMsg("文件夹不存在：" + folderPath);
            }
            if (!folder.isDirectory()) {
                return Result.failWithOnlyMsg("路径不是文件夹：" + folderPath);
            }
            File[] allFiles = folder.listFiles();
            if (allFiles == null || allFiles.length == 0) {
                return Result.failWithOnlyMsg("当前文件夹无任何文件：" + folderPath);
            }
            for (File file : allFiles) {
                List<ExcelAdmissionDataEntity> data = ExcelReadUtil.readForExcelAdmissionDataAllSheets(file, headRowNum, type);
                for (ExcelAdmissionDataEntity excelAdmissionDataEntity : data) {
                    TAdmissionData admissionDataEntity = new TAdmissionData();
                    admissionDataEntity.setYear(year);
                    admissionDataEntity.setProvinceId(map.get(province));
                    admissionDataEntity.setBatch(batch);
                    admissionDataEntity.setBatchRemark(batchRemark);
                    admissionDataEntity.setSubjectType(subjectType);
                    admissionDataEntity.setCollegeCode(excelAdmissionDataEntity.getCollegeCode());
                    admissionDataEntity.setCollegeName(excelAdmissionDataEntity.getCollegeName());
                    admissionDataEntity.setMajorGroupCode(excelAdmissionDataEntity.getMajorGroup());
                    admissionDataEntity.setMajorCode(excelAdmissionDataEntity.getMajorGroup());
                    admissionDataEntity.setMajorName(excelAdmissionDataEntity.getMajorName());
                    admissionDataEntity.setLowestAdmissionScore(new BigDecimal(excelAdmissionDataEntity.getLowestScore()));
                    admissionDataEntity.setLowestAdmissionRank(Integer.parseInt(excelAdmissionDataEntity.getLowestRank()));
                    admissionData.add(admissionDataEntity);
                }
            }
            System.out.println("数据条数：" + admissionData.size());
            return Result.success();
//            return admissionDataService.saveBatch(admissionData) ? Result.success() : Result.failWithOnlyMsg("插入失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failWithOnlyMsg("执行失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> addScoreRank() {
        try {
            String chineseExcelPath = "D:/教材/毕业论文/毕业论文数据/院校数据/院校库.xlsx";
            File excelFile = new File(chineseExcelPath);
            // 一分一段
            List<ExcelRawData> data = ExcelReadUtil.readForExcelAllSheetOrigin(excelFile, 2, ExcelTypeEnum.XLSX);
        } catch (Exception e) {
            System.out.println("执行失败：" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
