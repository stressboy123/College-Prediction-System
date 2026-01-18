package com.gdut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.LoginDTO;
import com.gdut.entity.RegisterDTO;
import com.gdut.entity.Result;
import com.gdut.entity.ResultCode;
import com.gdut.entity.SysUser;
import com.gdut.mapper.SysRoleMapper;
import com.gdut.mapper.SysUserMapper;
import com.gdut.mapper.SysUserRoleMapper;
import com.gdut.service.SysUserService;
import com.gdut.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author liujunliang
 * @date 2026/1/15
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private JwtUtil jwtUtil;

    // 默认注册角色（普通用户）
    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    public Result<String> login(LoginDTO loginDTO) {
        // 1. 验证用户名密码（Spring Security认证）
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );
        // 2. 生成JWT令牌
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getUsername());
        String token = jwtUtil.generateToken(userDetails.getUsername());
        // 3. 返回结果
        return Result.successWithCustomMsgAndData("登录成功", token);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> register(RegisterDTO registerDTO) {
        // 1. 校验用户名是否已存在
        if (sysUserMapper.selectByUsername(registerDTO.getUsername()) != null) {
            return Result.fail(ResultCode.USERNAME_EXISTS);
        }
        // 2. 密码加密
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        // 3. 构建用户实体
        SysUser sysUser = new SysUser();
        sysUser.setUsername(registerDTO.getUsername());
        sysUser.setPassword(encodedPassword);
        sysUser.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        // 4. 保存用户（createTime和status自动填充）
        sysUserMapper.insert(sysUser);
        // 5. 分配默认角色
        Long roleId = sysRoleMapper.selectIdByRoleName(DEFAULT_ROLE);
        if (roleId == null) {
            return Result.fail(ResultCode.ROLE_NOT_FOUND);
        }
        sysUserRoleMapper.insertUserRole(sysUser.getId(), roleId);
        // 6. 返回结果
        return Result.successWithCustomMsg("注册成功，请登录");
    }

    @Override
    public SysUser getUserByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }
}
