package top.zhengru.unipush.api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.zhengru.unipush.api.mapper.PushChannelMapper;
import top.zhengru.unipush.common.model.entity.PushChannel;
import top.zhengru.unipush.common.model.vo.ChannelInfoVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 推送渠道服务
 *
 * @author zhengru
 */
@Service
public class ChannelService {

    private static final Logger logger = LoggerFactory.getLogger(ChannelService.class);

    @Autowired
    private PushChannelMapper pushChannelMapper;

    /**
     * 查询所有渠道
     *
     * @return 渠道列表
     */
    public List<ChannelInfoVO> listChannels() {
        LambdaQueryWrapper<PushChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PushChannel::getPriority)
               .orderByDesc(PushChannel::getCreateTime);
        List<PushChannel> channels = pushChannelMapper.selectList(wrapper);

        return channels.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 根据ID获取渠道
     *
     * @param id 渠道ID
     * @return 渠道信息
     */
    public ChannelInfoVO getChannelById(Long id) {
        PushChannel channel = pushChannelMapper.selectById(id);
        return channel != null ? convertToVO(channel) : null;
    }

    /**
     * 根据渠道编码获取渠道
     *
     * @param channelCode 渠道编码
     * @return 渠道信息
     */
    public PushChannel getChannelByCode(String channelCode) {
        LambdaQueryWrapper<PushChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushChannel::getChannelCode, channelCode);
        return pushChannelMapper.selectOne(wrapper);
    }

    /**
     * 创建渠道
     *
     * @param channelCode  渠道编码
     * @param channelName  渠道名称
     * @param description  描述
     * @param config       配置（JSON）
     * @param priority     优先级
     * @param creatorId    创建人ID
     * @return 渠道信息
     */
    public ChannelInfoVO createChannel(String channelCode, String channelName, String description,
                                        String config, Integer priority, Long creatorId) {
        // 检查渠道编码是否已存在
        PushChannel existing = getChannelByCode(channelCode);
        if (existing != null) {
            throw new RuntimeException("渠道编码已存在");
        }

        PushChannel channel = new PushChannel();
        channel.setChannelCode(channelCode);
        channel.setChannelName(channelName);
        channel.setDescription(description);
        channel.setConfig(config);
        channel.setEnabled(1);
        channel.setPriority(priority != null ? priority : 0);
        channel.setCreatorId(creatorId);

        pushChannelMapper.insert(channel);
        logger.info("创建渠道成功: channelCode={}", channelCode);

        return convertToVO(channel);
    }

    /**
     * 更新渠道
     *
     * @param id           渠道ID
     * @param channelName  渠道名称
     * @param description  描述
     * @param config       配置（JSON）
     * @param priority     优先级
     * @return 渠道信息
     */
    public ChannelInfoVO updateChannel(Long id, String channelName, String description,
                                        String config, Integer priority) {
        PushChannel channel = new PushChannel();
        channel.setId(id);
        channel.setChannelName(channelName);
        channel.setDescription(description);
        channel.setConfig(config);
        channel.setPriority(priority);

        pushChannelMapper.updateById(channel);
        logger.info("更新渠道成功: id={}", id);

        return getChannelById(id);
    }

    /**
     * 删除渠道
     *
     * @param id 渠道ID
     * @return 是否成功
     */
    public boolean deleteChannel(Long id) {
        PushChannel channel = pushChannelMapper.selectById(id);
        if (channel == null) {
            return false;
        }

        pushChannelMapper.deleteById(id);
        logger.info("删除渠道成功: id={}", id);

        return true;
    }

    /**
     * 更新渠道状态
     *
     * @param id      渠道ID
     * @param enabled 是否启用：1-启用 0-禁用
     * @return 是否成功
     */
    public boolean updateChannelStatus(Long id, Integer enabled) {
        PushChannel channel = new PushChannel();
        channel.setId(id);
        channel.setEnabled(enabled);

        int rows = pushChannelMapper.updateById(channel);
        logger.info("更新渠道状态: id={}, enabled={}", id, enabled);

        return rows > 0;
    }

    /**
     * 转换为VO
     *
     * @param channel 渠道实体
     * @return 渠道VO
     */
    private ChannelInfoVO convertToVO(PushChannel channel) {
        ChannelInfoVO vo = new ChannelInfoVO();
        vo.setId(channel.getId());
        vo.setChannelCode(channel.getChannelCode());
        vo.setChannelName(channel.getChannelName());
        vo.setDescription(channel.getDescription());
        vo.setConfig(channel.getConfig());
        vo.setEnabled(channel.getEnabled());
        vo.setPriority(channel.getPriority());
        vo.setCreateTime(channel.getCreateTime());
        return vo;
    }
}
