package com.gdut.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.TProvince;
import com.gdut.entity.TSubjectCombination;
import com.gdut.entity.TUserInfo;
import com.gdut.entity.UserInfoDTO;
import com.gdut.mapper.TUserInfoMapper;
import com.gdut.service.ITProvinceService;
import com.gdut.service.ITSubjectCombinationService;
import com.gdut.service.ITUserInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 用户信息表Service实现
 */
@Service
public class TUserInfoServiceImpl extends ServiceImpl<TUserInfoMapper, TUserInfo> implements ITUserInfoService {
    @Resource
    private ITProvinceService provinceService;
    @Resource
    private ITSubjectCombinationService subjectCombinationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoDTO addUserInfo(UserInfoDTO userInfoDTO) {
        // 1. 先查是否存在，存在则走更新逻辑
        UserInfoDTO oldDTO = this.getUserInfoByUserId(userInfoDTO.getSysUserId());
        if (oldDTO != null) {
            return this.updateUserInfo(userInfoDTO);
        }

        // 2. 不存在则新增
        TUserInfo entity = new TUserInfo();
        BeanUtils.copyProperties(userInfoDTO, entity);

        // 3. 处理省份：名称→ID（查不到则设为null，前端展示暂无）
        Integer provinceId = null;
        if (StringUtils.hasText(userInfoDTO.getCandidateProvince())) {
            LambdaQueryWrapper<TProvince> provinceWrapper = new LambdaQueryWrapper<TProvince>()
                    .eq(TProvince::getProvinceName, userInfoDTO.getCandidateProvince().trim())
                    .select(TProvince::getId);
            TProvince province = provinceService.getOne(provinceWrapper, false);
            provinceId = province != null ? province.getId() : null;
        }
        entity.setCandidateProvinceId(provinceId);

        // 4. 处理选科组合：first+second→ID（查不到则设为null）
        Integer subjectCombinationId = null;
        if (StringUtils.hasText(userInfoDTO.getFirstSubject()) && StringUtils.hasText(userInfoDTO.getSecondSubject())) {
            LambdaQueryWrapper<TSubjectCombination> subjectWrapper = new LambdaQueryWrapper<TSubjectCombination>()
                    .eq(TSubjectCombination::getFirstSubject, userInfoDTO.getFirstSubject().trim())
                    .eq(TSubjectCombination::getSecondSubject, userInfoDTO.getSecondSubject().trim())
                    .select(TSubjectCombination::getId);
            TSubjectCombination combination = subjectCombinationService.getOne(subjectWrapper, false);
            subjectCombinationId = combination != null ? combination.getId() : null;
        }
        entity.setSubjectCombinationId(subjectCombinationId); // 修正字段名

        // 5. 执行新增
        boolean saveSuccess = this.save(entity);
        if (!saveSuccess) {
            return null;
        }

        // 6. 返回最新数据（新增后查一次，保证数据完整）
        return this.getUserInfoByUserId(userInfoDTO.getSysUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoDTO updateUserInfo(UserInfoDTO userInfoDTO) {
        // 1. 校验旧数据是否存在
        LambdaQueryWrapper<TUserInfo> oldWrapper = new LambdaQueryWrapper<TUserInfo>()
                .eq(TUserInfo::getSysUserId, userInfoDTO.getSysUserId());
        TUserInfo oldEntity = this.getOne(oldWrapper, false);
        if (oldEntity == null) {
            return null; // 无旧数据，更新失败
        }

        // 2. 构建更新条件（按sysUserId更新）
        LambdaUpdateWrapper<TUserInfo> updateWrapper = new LambdaUpdateWrapper<TUserInfo>()
                .eq(TUserInfo::getSysUserId, userInfoDTO.getSysUserId());

        // 3. 逐个更新可修改字段（只更新非null的字段，支持置空）
        // 3.1 基础字段：真实姓名、手机号、年份、总分、排名
        if (userInfoDTO.getRealName() != null) {
            updateWrapper.set(TUserInfo::getRealName, userInfoDTO.getRealName().trim());
        }
        if (userInfoDTO.getPhone() != null) {
            updateWrapper.set(TUserInfo::getPhone, userInfoDTO.getPhone().trim());
        }
        if (userInfoDTO.getCandidateYear() != null) {
            updateWrapper.set(TUserInfo::getCandidateYear, userInfoDTO.getCandidateYear());
        }
        if (userInfoDTO.getGaokaoTotalScore() != null) {
            updateWrapper.set(TUserInfo::getGaokaoTotalScore, userInfoDTO.getGaokaoTotalScore());
        }
        if (userInfoDTO.getProvinceRank() != null) {
            updateWrapper.set(TUserInfo::getProvinceRank, userInfoDTO.getProvinceRank());
        }

        // 3.2 处理省份：名称→ID（支持置空，传空则设为null）
        if (userInfoDTO.getCandidateProvince() != null) {
            Integer provinceId = null;
            if (StringUtils.hasText(userInfoDTO.getCandidateProvince())) {
                LambdaQueryWrapper<TProvince> provinceWrapper = new LambdaQueryWrapper<TProvince>()
                        .eq(TProvince::getProvinceName, userInfoDTO.getCandidateProvince().trim())
                        .select(TProvince::getId);
                TProvince province = provinceService.getOne(provinceWrapper, false);
                provinceId = province != null ? province.getId() : null;
            }
            updateWrapper.set(TUserInfo::getCandidateProvinceId, provinceId);
        }

        // 3.3 处理选科组合：first+second→ID（支持置空）
        if (userInfoDTO.getFirstSubject() != null || userInfoDTO.getSecondSubject() != null) {
            Integer subjectCombinationId = null;
            if (StringUtils.hasText(userInfoDTO.getFirstSubject()) && StringUtils.hasText(userInfoDTO.getSecondSubject())) {
                LambdaQueryWrapper<TSubjectCombination> subjectWrapper = new LambdaQueryWrapper<TSubjectCombination>()
                        .eq(TSubjectCombination::getFirstSubject, userInfoDTO.getFirstSubject().trim())
                        .eq(TSubjectCombination::getSecondSubject, userInfoDTO.getSecondSubject().trim())
                        .select(TSubjectCombination::getId);
                TSubjectCombination combination = subjectCombinationService.getOne(subjectWrapper, false);
                subjectCombinationId = combination != null ? combination.getId() : null;
            }
            updateWrapper.set(TUserInfo::getSubjectCombinationId, subjectCombinationId); // 修正字段名
        }

        // 4. 执行更新
        boolean updateSuccess = this.update(updateWrapper);
        if (!updateSuccess) {
            return null;
        }

        // 5. 查询最新数据，转换为DTO返回（核心：返回最新数据）
        return this.getUserInfoByUserId(userInfoDTO.getSysUserId());
    }

    @Override
    public UserInfoDTO getUserInfoByUserId(Long userId) {
        LambdaQueryWrapper<TUserInfo> queryWrapper = new LambdaQueryWrapper<TUserInfo>()
                .eq(TUserInfo::getSysUserId, userId);
        TUserInfo tUserInfo = this.getOne(queryWrapper, false);
        if (tUserInfo == null) {
            return null;
        }

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(tUserInfo, userInfoDTO);

        // 1. 处理省份：ID→名称（兜底暂无信息）
        String provinceName = "暂无信息";
        if (tUserInfo.getCandidateProvinceId() != null) {
            TProvince province = provinceService.getById(tUserInfo.getCandidateProvinceId());
            if (province != null && StringUtils.hasText(province.getProvinceName())) {
                provinceName = province.getProvinceName();
            }
        }
        userInfoDTO.setCandidateProvince(provinceName);

        // 2. 处理选科组合：ID→first+second（兜底暂无信息）
        String firstSubject = "暂无信息";
        String secondSubject = "暂无信息";
        if (tUserInfo.getSubjectCombinationId() != null) { // 修正字段名
            TSubjectCombination subjectCombination = subjectCombinationService.getById(tUserInfo.getSubjectCombinationId());
            if (subjectCombination != null) {
                if (StringUtils.hasText(subjectCombination.getFirstSubject())) {
                    firstSubject = subjectCombination.getFirstSubject();
                }
                if (StringUtils.hasText(subjectCombination.getSecondSubject())) {
                    secondSubject = subjectCombination.getSecondSubject();
                }
            }
        }
        userInfoDTO.setFirstSubject(firstSubject);
        userInfoDTO.setSecondSubject(secondSubject);

        // 3. 基础字段兜底暂无信息
        userInfoDTO.setRealName(StringUtils.hasText(userInfoDTO.getRealName()) ? userInfoDTO.getRealName() : "暂无信息");
        userInfoDTO.setPhone(StringUtils.hasText(userInfoDTO.getPhone()) ? userInfoDTO.getPhone() : "暂无信息");

        return userInfoDTO;
    }
}

