package com.gdut.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdut.entity.SysUser;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
public interface SysUserService extends IService<SysUser> {
    boolean existsByUsername(String username);

    void saveUserWithRole(SysUser user, long l);
}
