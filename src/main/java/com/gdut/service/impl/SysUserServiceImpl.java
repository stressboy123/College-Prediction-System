package com.gdut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gdut.entity.LoginDTO;
import com.gdut.entity.LoginResponseVO;
import com.gdut.entity.RegisterDTO;
import com.gdut.entity.Result;
import com.gdut.entity.ResultCode;
import com.gdut.entity.SysRole;
import com.gdut.entity.SysUser;
import com.gdut.mapper.SysRoleMapper;
import com.gdut.mapper.SysUserMapper;
import com.gdut.mapper.SysUserRoleMapper;
import com.gdut.service.SysUserService;
import com.gdut.utils.JwtUtil;
import com.gdut.utils.SpringContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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
    private JwtUtil jwtUtil;

    // 默认注册角色（普通用户）
    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    public Result<LoginResponseVO> login(LoginDTO loginDTO) {
        // 1. 通过SpringContextHolder获取所需的组件
        AuthenticationManager authenticationManager = SpringContextHolder.getBean(AuthenticationManager.class);

        // 2. 验证用户名密码（Spring Security认证）
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );

        // 3. 查询用户+角色信息
        // 3.1 通过用户名查SysUser（拿到ID和昵称）
        SysUser sysUser = sysUserMapper.selectByUsername(loginDTO.getUsername());
        // 3.2 通过用户ID查角色ID
        List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(sysUser.getId());
        if (roleIds == null || roleIds.isEmpty()) {
            return Result.fail(ResultCode.ROLE_NOT_FOUND); // 无角色直接返回失败
        }
        // 3.3 通过角色ID查角色名
        List<SysRole> roles = sysRoleMapper.selectBatchIds(roleIds);
        List<String> roleNames = roles.stream()
                .map(SysRole::getRoleName)
                .collect(Collectors.toList());

        // 4. 生成带角色的JWT Token
        String token = jwtUtil.generateTokenWithRoles(sysUser.getUsername(), roleNames);

        // 5. 构建返回VO
        LoginResponseVO responseVO = new LoginResponseVO();
        responseVO.setToken(token);
        responseVO.setUsername(sysUser.getUsername());
        responseVO.setNickname(sysUser.getNickname());
        responseVO.setRoles(roleNames);

        // 6. 返回结果
        return Result.successWithCustomMsgAndData("登录成功", responseVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> register(RegisterDTO registerDTO) {
        // 通过SpringContextHolder获取PasswordEncoder
        PasswordEncoder passwordEncoder = SpringContextHolder.getBean(PasswordEncoder.class);
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

    @Override
    public Result<String> logout() {
        return Result.successWithCustomMsg("登出成功");
    }
}
