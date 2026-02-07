package com.gdut.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gdut.entity.Result;
import com.gdut.entity.ResultCode;
import com.gdut.entity.TCollege;
import com.gdut.entity.TMajor;
import com.gdut.service.ITCollegeService;
import com.gdut.service.ITMajorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liujunliang
 * @date 2026/2/7
 */
@RestController
@RequestMapping("/api/data")
public class DataController {

    @Resource
    private ITCollegeService collegeService;

    @Resource
    private ITMajorService majorService;

    /**
     * 分页查询院校数据
     *
     * @param pageNum  页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @return 统一分页结果（基于ResultCode规范）
     */
    @GetMapping("/college/page")
    public Result<Map<String, Object>> pageQueryCollege(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            // 1. 分页参数合法性校验（适配PARAM_ERROR枚举）
            if (pageNum < 1 || pageSize < 1 || pageSize > 100) {
                return Result.failWithCustomMsg(ResultCode.PARAM_ERROR, "分页参数非法：页码≥1，每页条数≥1且≤100");
            }

            // 2. 执行分页查询
            Page<TCollege> page = new Page<>(pageNum, pageSize);
            Page<TCollege> collegePage = collegeService.page(page);

            // 3. 校验查询结果（适配DATA_NOT_FOUND枚举）
            if (collegePage.getRecords().isEmpty() && collegePage.getTotal() == 0) {
                return Result.failWithCustomMsg(ResultCode.DATA_NOT_FOUND, "未查询到院校相关数据");
            }

            // 4. 封装分页数据
            Map<String, Object> pageData = new HashMap<>(4);
            pageData.put("total", collegePage.getTotal());   // 总记录数
            pageData.put("pages", collegePage.getPages());   // 总页数
            pageData.put("current", collegePage.getCurrent()); // 当前页码
            pageData.put("records", collegePage.getRecords()); // 分页数据列表

            // 5. 成功返回（适配SUCCESS枚举）
            return Result.success(pageData);
        } catch (Exception e) {
            // 系统异常返回（适配SYSTEM_ERROR枚举）
            return Result.failWithCustomMsg(ResultCode.SYSTEM_ERROR, "院校分页查询失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询专业数据
     *
     * @param pageNum  页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @return 统一分页结果（基于ResultCode规范）
     */
    @GetMapping("/major/page")
    public Result<Map<String, Object>> pageQueryMajor(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            // 分页参数校验
            if (pageNum < 1 || pageSize < 1 || pageSize > 100) {
                return Result.failWithCustomMsg(ResultCode.PARAM_ERROR, "分页参数非法：页码≥1，每页条数≥1且≤100");
            }

            // 执行分页查询
            Page<TMajor> page = new Page<>(pageNum, pageSize);
            Page<TMajor> majorPage = majorService.page(page);

            // 校验查询结果
            if (majorPage.getRecords().isEmpty() && majorPage.getTotal() == 0) {
                return Result.failWithCustomMsg(ResultCode.DATA_NOT_FOUND, "未查询到专业相关数据");
            }

            // 封装分页数据
            Map<String, Object> pageData = new HashMap<>(4);
            pageData.put("total", majorPage.getTotal());
            pageData.put("pages", majorPage.getPages());
            pageData.put("current", majorPage.getCurrent());
            pageData.put("records", majorPage.getRecords());

            // 成功返回
            return Result.success(pageData);
        } catch (Exception e) {
            // 系统异常返回
            return Result.failWithCustomMsg(ResultCode.SYSTEM_ERROR, "专业分页查询失败：" + e.getMessage());
        }
    }

//    /**
//     * 带条件的专业分页查询（规范适配版）
//     *
//     * @param pageNum    页码
//     * @param pageSize   每页条数
//     * @param collegeId  所属院校ID（可选）
//     * @param majorName  专业名称模糊查询（可选）
//     * @return 统一分页结果
//     */
//    @GetMapping("/major/page/condition")
//    public Result<Map<String, Object>> pageQueryMajorByCondition(
//            @RequestParam(defaultValue = "1") Integer pageNum,
//            @RequestParam(defaultValue = "10") Integer pageSize,
//            @RequestParam(required = false) Long collegeId,
//            @RequestParam(required = false) String majorName) {
//        try {
//            // 分页参数校验
//            if (pageNum < 1 || pageSize < 1 || pageSize > 100) {
//                return Result.failWithCustomMsg(ResultCode.PARAM_ERROR, "分页参数非法：页码≥1，每页条数≥1且≤100");
//            }
//
//            // 执行条件分页查询（依赖Service扩展方法）
//            Page<TMajor> page = new Page<>(pageNum, pageSize);
//            Page<TMajor> majorPage = majorService.pageQueryByCondition(page, collegeId, majorName);
//
//            // 校验查询结果
//            if (majorPage.getRecords().isEmpty() && majorPage.getTotal() == 0) {
//                return Result.failWithCustomMsg(ResultCode.DATA_NOT_FOUND, "未查询到符合条件的专业数据");
//            }
//
//            // 封装分页数据
//            Map<String, Object> pageData = new HashMap<>(4);
//            pageData.put("total", majorPage.getTotal());
//            pageData.put("pages", majorPage.getPages());
//            pageData.put("current", majorPage.getCurrent());
//            pageData.put("records", majorPage.getRecords());
//
//            // 成功返回
//            return Result.success(pageData);
//        } catch (Exception e) {
//            // 区分异常类型：数据库异常返回DB_ERROR，其他系统异常返回SYSTEM_ERROR
//            if (e.getMessage().contains("SQL") || e.getMessage().contains("数据库")) {
//                return Result.failWithCustomMsg(ResultCode.DB_ERROR, "专业条件查询失败：数据库操作异常");
//            }
//            return Result.failWithCustomMsg(ResultCode.SYSTEM_ERROR, "专业条件查询失败：" + e.getMessage());
//        }
//    }
}
