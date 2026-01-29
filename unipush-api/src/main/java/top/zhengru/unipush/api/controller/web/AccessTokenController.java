package top.zhengru.unipush.api.controller.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.zhengru.unipush.api.service.AccessTokenService;
import top.zhengru.unipush.api.util.JwtUtils;
import top.zhengru.unipush.common.model.vo.ResponseVO;
import top.zhengru.unipush.common.model.vo.TokenInfoVO;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 访问令牌管理控制器
 *
 * @author zhengru
 */
@Tag(name = "访问令牌管理", description = "AccessToken的增删改查操作")
@RestController
@RequestMapping("/api/web/accesstoken")
public class AccessTokenController {

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 查询访问令牌列表
     *
     * @return 令牌列表
     */
    @Operation(summary = "查询访问令牌列表", description = "获取所有访问令牌，令牌值已脱敏处理")
    @GetMapping("/list")
    public ResponseVO<List<TokenInfoVO>> list() {
        List<TokenInfoVO> tokens = accessTokenService.listTokens();
        return ResponseVO.ok(tokens);
    }

    /**
     * 根据ID查询令牌
     *
     * @param id 令牌ID
     * @return 令牌信息
     */
    @Operation(summary = "查询访问令牌详情", description = "根据ID获取访问令牌详细信息，令牌值已脱敏处理")
    @GetMapping("/{id}")
    public ResponseVO<TokenInfoVO> getToken(
            @Parameter(description = "令牌ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        TokenInfoVO token = accessTokenService.getTokenById(id);
        return ResponseVO.ok(token);
    }

    /**
     * 添加访问令牌
     *
     * @param tokenName   令牌名称
     * @param description 描述
     * @param allowedIps  IP白名单
     * @param rateLimit   限流阈值
     * @param expireTime  过期时间
     * @return 操作结果
     */
    @Operation(summary = "创建访问令牌", description = "创建新的访问令牌，返回完整的令牌值（仅此一次），请妥善保存")
    @PostMapping("/add")
    public ResponseVO<TokenInfoVO> add(
            @Parameter(description = "令牌名称", required = true, example = "测试令牌")
            @RequestParam("tokenName") String tokenName,
            @Parameter(description = "令牌描述", example = "用于API测试")
            @RequestParam(value = "description", required = false) String description,
            @Parameter(description = "IP白名单，多个IP用逗号分隔，为空则不限制", example = "192.168.1.1,192.168.1.2")
            @RequestParam(value = "allowedIps", required = false) String allowedIps,
            @Parameter(description = "限流阈值（次/小时）", example = "1000")
            @RequestParam(value = "rateLimit", defaultValue = "1000") Integer rateLimit,
            @Parameter(description = "过期时间，为空则永不过期", example = "2025-12-31T23:59:59")
            @RequestParam(value = "expireTime", required = false) LocalDateTime expireTime,
            HttpServletRequest request) {
        // 从JWT Token中获取创建人ID
        Long creatorId = getUserIdFromToken(request);

        TokenInfoVO token = accessTokenService.createToken(tokenName, description, allowedIps, rateLimit, expireTime, creatorId);
        return ResponseVO.ok(token, "添加成功");
    }

    /**
     * 更新访问令牌
     *
     * @param id          令牌ID
     * @param tokenName   令牌名称
     * @param description 描述
     * @param allowedIps  IP白名单
     * @param rateLimit   限流阈值
     * @param expireTime  过期时间
     * @return 操作结果
     */
    @Operation(summary = "更新访问令牌", description = "更新访问令牌的信息（令牌值本身不可修改）")
    @PutMapping("/update/{id}")
    public ResponseVO<TokenInfoVO> update(
            @Parameter(description = "令牌ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "令牌名称", required = true, example = "测试令牌")
            @RequestParam("tokenName") String tokenName,
            @Parameter(description = "令牌描述", example = "用于API测试")
            @RequestParam(value = "description", required = false) String description,
            @Parameter(description = "IP白名单，多个IP用逗号分隔，为空则不限制", example = "192.168.1.1,192.168.1.2")
            @RequestParam(value = "allowedIps", required = false) String allowedIps,
            @Parameter(description = "限流阈值（次/小时）", example = "1000")
            @RequestParam(value = "rateLimit", required = false) Integer rateLimit,
            @Parameter(description = "过期时间，为空则永不过期", example = "2025-12-31T23:59:59")
            @RequestParam(value = "expireTime", required = false) LocalDateTime expireTime) {
        TokenInfoVO token = accessTokenService.updateToken(id, tokenName, description, allowedIps, rateLimit, expireTime);
        return ResponseVO.ok(token, "更新成功");
    }

    /**
     * 删除访问令牌
     *
     * @param id 令牌ID
     * @return 操作结果
     */
    @Operation(summary = "删除访问令牌", description = "删除指定的访问令牌，删除后不可恢复")
    @DeleteMapping("/delete/{id}")
    public ResponseVO<Void> delete(
            @Parameter(description = "令牌ID", required = true, example = "1")
            @PathVariable("id") Long id) {
        boolean success = accessTokenService.deleteToken(id);
        return success ? ResponseVO.ok(null, "删除成功") : ResponseVO.fail("令牌不存在");
    }

    /**
     * 启用/禁用访问令牌
     *
     * @param id     令牌ID
     * @param status 状态：1-启用 0-禁用
     * @return 操作结果
     */
    @Operation(summary = "更新访问令牌状态", description = "启用或禁用访问令牌")
    @PutMapping("/status/{id}")
    public ResponseVO<Void> updateStatus(
            @Parameter(description = "令牌ID", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "状态：1-启用 0-禁用", required = true, example = "1")
            @RequestParam("status") Integer status) {
        boolean success = accessTokenService.updateTokenStatus(id, status);
        return success ? ResponseVO.ok(null, "更新成功") : ResponseVO.fail("令牌不存在");
    }

    /**
     * 从请求中获取用户ID
     *
     * @param request HTTP请求
     * @return 用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token != null && jwtUtils.validateToken(token)) {
                return jwtUtils.getUserIdFromToken(token);
            }
        } catch (Exception e) {
            // Token解析失败，返回默认用户ID
        }
        return 1L; // 默认返回系统管理员ID
    }

    /**
     * 从请求中提取Token
     *
     * @param request HTTP请求
     * @return Token字符串
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        // 也可以从查询参数中获取
        return request.getParameter("token");
    }
}
