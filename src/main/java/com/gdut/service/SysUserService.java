package com.gdut.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gdut.entity.LoginDTO;
import com.gdut.entity.LoginResponseVO;
import com.gdut.entity.RegisterDTO;
import com.gdut.entity.Result;
import com.gdut.entity.SysUser;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
public interface SysUserService extends IService<SysUser> {
    // 登录
    Result<LoginResponseVO> login(LoginDTO loginDTO);
    // 注册
    Result<String> register(RegisterDTO registerDTO);
    // 根据用户名查询用户
    SysUser getUserByUsername(String username);
    Result<String> logout();
}
