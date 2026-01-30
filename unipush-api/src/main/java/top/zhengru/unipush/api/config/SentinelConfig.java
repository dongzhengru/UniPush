package top.zhengru.unipush.api.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel规则配置
 *
 * @author zhengru
 */
@Configuration
public class SentinelConfig {

    private static final Logger log = LoggerFactory.getLogger(SentinelConfig.class);

    /**
     * 初始化Sentinel流控规则
     */
    @PostConstruct
    public void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();

        // ========== 开放接口流控规则 ==========

        // 发送单条消息接口 - QPS限流
        FlowRule rule1 = new FlowRule();
        rule1.setResource("send-message");
        rule1.setGrade(RuleConstant.FLOW_GRADE_QPS); // QPS限流
        rule1.setCount(100); // 每秒最多100个请求
        rule1.setLimitApp("default");
        rules.add(rule1);

        // 批量发送消息接口 - QPS限流
        FlowRule rule2 = new FlowRule();
        rule2.setResource("send-batch-message");
        rule2.setGrade(RuleConstant.FLOW_GRADE_QPS); // QPS限流
        rule2.setCount(50); // 每秒最多50个请求（批量接口限制更严格）
        rule2.setLimitApp("default");
        rules.add(rule2);

        // 发送单条消息接口 - 并发线程数限流
        FlowRule rule3 = new FlowRule();
        rule3.setResource("send-message");
        rule3.setGrade(RuleConstant.FLOW_GRADE_THREAD); // 线程数限流
        rule3.setCount(50); // 最多50个并发线程
        rule3.setLimitApp("default");
        rules.add(rule3);

        // 批量发送消息接口 - 并发线程数限流
        FlowRule rule4 = new FlowRule();
        rule4.setResource("send-batch-message");
        rule4.setGrade(RuleConstant.FLOW_GRADE_THREAD); // 线程数限流
        rule4.setCount(20); // 最多20个并发线程
        rule4.setLimitApp("default");
        rules.add(rule4);

        // ========== Web接口流控规则 ==========

        // Web发送单条消息接口 - QPS限流
        FlowRule rule5 = new FlowRule();
        rule5.setResource("web-send-message");
        rule5.setGrade(RuleConstant.FLOW_GRADE_QPS); // QPS限流
        rule5.setCount(200); // 每秒最多200个请求（Web接口可以更宽松）
        rule5.setLimitApp("default");
        rules.add(rule5);

        // Web批量发送消息接口 - QPS限流
        FlowRule rule6 = new FlowRule();
        rule6.setResource("web-send-batch-message");
        rule6.setGrade(RuleConstant.FLOW_GRADE_QPS); // QPS限流
        rule6.setCount(100); // 每秒最多100个请求
        rule6.setLimitApp("default");
        rules.add(rule6);

        // Web发送单条消息接口 - 并发线程数限流
        FlowRule rule7 = new FlowRule();
        rule7.setResource("web-send-message");
        rule7.setGrade(RuleConstant.FLOW_GRADE_THREAD); // 线程数限流
        rule7.setCount(100); // 最多100个并发线程
        rule7.setLimitApp("default");
        rules.add(rule7);

        // Web批量发送消息接口 - 并发线程数限流
        FlowRule rule8 = new FlowRule();
        rule8.setResource("web-send-batch-message");
        rule8.setGrade(RuleConstant.FLOW_GRADE_THREAD); // 线程数限流
        rule8.setCount(50); // 最多50个并发线程
        rule8.setLimitApp("default");
        rules.add(rule8);

        FlowRuleManager.loadRules(rules);
        log.info("Sentinel流控规则加载完成，共 {} 条规则", rules.size());
    }

    /**
     * 初始化Sentinel熔断降级规则
     */
    @PostConstruct
    public void initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();

        // ========== 开放接口熔断降级规则 ==========

        // 发送单条消息接口 - 熔断降级
        DegradeRule rule1 = new DegradeRule();
        rule1.setResource("send-message");
        rule1.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT); // 异常计数策略
        rule1.setCount(10); // 当异常数超过10次
        rule1.setTimeWindow(60); // 在60秒时间窗口内
        rule1.setMinRequestAmount(20); // 最小请求数
        rule1.setStatIntervalMs(60000); // 统计时长60秒
        rules.add(rule1);

        // 批量发送消息接口 - 熔断降级
        DegradeRule rule2 = new DegradeRule();
        rule2.setResource("send-batch-message");
        rule2.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT); // 异常计数策略
        rule2.setCount(5); // 当异常数超过5次
        rule2.setTimeWindow(60); // 在60秒时间窗口内
        rule2.setMinRequestAmount(10); // 最小请求数
        rule2.setStatIntervalMs(60000); // 统计时长60秒
        rules.add(rule2);

        // ========== Web接口熔断降级规则 ==========

        // Web发送单条消息接口 - 熔断降级
        DegradeRule rule3 = new DegradeRule();
        rule3.setResource("web-send-message");
        rule3.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT); // 异常计数策略
        rule3.setCount(20); // 当异常数超过20次
        rule3.setTimeWindow(60); // 在60秒时间窗口内
        rule3.setMinRequestAmount(50); // 最小请求数
        rule3.setStatIntervalMs(60000); // 统计时长60秒
        rules.add(rule3);

        // Web批量发送消息接口 - 熔断降级
        DegradeRule rule4 = new DegradeRule();
        rule4.setResource("web-send-batch-message");
        rule4.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT); // 异常计数策略
        rule4.setCount(10); // 当异常数超过10次
        rule4.setTimeWindow(60); // 在60秒时间窗口内
        rule4.setMinRequestAmount(20); // 最小请求数
        rule4.setStatIntervalMs(60000); // 统计时长60秒
        rules.add(rule4);

        DegradeRuleManager.loadRules(rules);
        log.info("Sentinel熔断降级规则加载完成，共 {} 条规则", rules.size());
    }
}
