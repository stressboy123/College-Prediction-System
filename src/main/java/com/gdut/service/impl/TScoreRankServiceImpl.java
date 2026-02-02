package com.gdut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.TScoreRank;
import com.gdut.mapper.TScoreRankMapper;
import com.gdut.service.ITScoreRankService;
import org.springframework.stereotype.Service;

/**
 * @author liujunliang
 * @date 2026/1/31
 * 一分一段表Service实现
 */
@Service
public class TScoreRankServiceImpl extends ServiceImpl<TScoreRankMapper, TScoreRank> implements ITScoreRankService {}

