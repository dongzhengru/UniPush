package top.zhengru.unipush.api.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.zhengru.unipush.api.service.AccessTokenService;
import top.zhengru.unipush.common.model.vo.ResponseVO;
import top.zhengru.unipush.common.model.vo.TokenInfoVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 访问令牌管理控制器
 *
 * @author zhengru
 */
@RestController
@RequestMapping("/api/web/accesstoken")
public class AccessTokenController {

    @Autowired
    private AccessTokenService accessTokenService;

    /**
     * 查询访问令牌列表
     *
     * @return 令牌列表
     */
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
    @GetMapping("/{id}")
    public ResponseVO<TokenInfoVO> getToken(@PathVariable("id") Long id) {
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
    @PostMapping("/add")
    public ResponseVO<TokenInfoVO> add(@RequestParam("tokenName") String tokenName,
                                        @RequestParam(value = "description", required = false) String description,
                                        @RequestParam(value = "allowedIps", required = false) String allowedIps,
                                        @RequestParam(value = "rateLimit", defaultValue = "1000") Integer rateLimit,
                                        @RequestParam(value = "expireTime", required = false) LocalDateTime expireTime) {
        // TODO: 从JWT Token中获取创建人ID
        Long creatorId = 1L;

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
    @PutMapping("/update/{id}")
    public ResponseVO<TokenInfoVO> update(@PathVariable("id") Long id,
                                           @RequestParam("tokenName") String tokenName,
                                           @RequestParam(value = "description", required = false) String description,
                                           @RequestParam(value = "allowedIps", required = false) String allowedIps,
                                           @RequestParam(value = "rateLimit", required = false) Integer rateLimit,
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
    @DeleteMapping("/delete/{id}")
    public ResponseVO<Void> delete(@PathVariable("id") Long id) {
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
    @PutMapping("/status/{id}")
    public ResponseVO<Void> updateStatus(@PathVariable("id") Long id,
                                         @RequestParam("status") Integer status) {
        boolean success = accessTokenService.updateTokenStatus(id, status);
        return success ? ResponseVO.ok(null, "更新成功") : ResponseVO.fail("令牌不存在");
    }
}
