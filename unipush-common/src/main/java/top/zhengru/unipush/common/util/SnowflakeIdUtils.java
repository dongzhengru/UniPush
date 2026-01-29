package top.zhengru.unipush.common.util;

/**
 * 雪花ID生成器
 *
 * @author zhengru
 */
public class SnowflakeIdUtils {

    /**
     * 起始时间戳 (2023-01-01 00:00:00)
     */
    private static final long START_TIMESTAMP = 1672531200000L;

    /**
     * 数据中心ID所占位数
     */
    private static final long DATACENTER_ID_BITS = 5L;

    /**
     * 工作机器ID所占位数
     */
    private static final long WORKER_ID_BITS = 5L;

    /**
     * 序列所占位数
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * 数据中心ID最大值
     */
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

    /**
     * 工作机器ID最大值
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /**
     * 序列最大值
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /**
     * 工作机器ID左移位数
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 数据中心ID左移位数
     */
    private static final long DATACENTER_ID_SHIFT = WORKER_ID_BITS + SEQUENCE_BITS;

    /**
     * 时间戳左移位数
     */
    private static final long TIMESTAMP_SHIFT = DATACENTER_ID_BITS + WORKER_ID_BITS + SEQUENCE_BITS;

    /**
     * 数据中心ID
     */
    private final long datacenterId;

    /**
     * 工作机器ID
     */
    private final long workerId;

    /**
     * 毫秒内序列
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间戳
     */
    private long lastTimestamp = -1L;

    /**
     * 单例实例
     */
    private static volatile SnowflakeIdUtils instance;

    /**
     * 构造函数
     *
     * @param datacenterId 数据中心ID (0~31)
     * @param workerId     工作ID (0~31)
     */
    private SnowflakeIdUtils(long datacenterId, long workerId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("datacenter Id can't be greater than %d or less than 0", MAX_DATACENTER_ID));
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    /**
     * 获取单例实例
     *
     * @param datacenterId 数据中心ID
     * @param workerId     工作ID
     * @return SnowflakeIdUtils实例
     */
    public static SnowflakeIdUtils getInstance(long datacenterId, long workerId) {
        if (instance == null) {
            synchronized (SnowflakeIdUtils.class) {
                if (instance == null) {
                    instance = new SnowflakeIdUtils(datacenterId, workerId);
                }
            }
        }
        return instance;
    }

    /**
     * 获取默认实例（datacenterId=0, workerId=0）
     *
     * @return SnowflakeIdUtils实例
     */
    public static SnowflakeIdUtils getInstance() {
        return getInstance(0, 0);
    }

    /**
     * 生成下一个ID（线程安全）
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = getCurrentTimestamp();

        // 如果当前时间小于上次生成ID的时间，说明系统时钟回退，抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                            lastTimestamp - timestamp));
        }

        // 如果是同一毫秒内生成的
        if (timestamp == lastTimestamp) {
            // 序列自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 如果序列溢出，则等待下一毫秒
            if (sequence == 0) {
                timestamp = waitNextMillis(timestamp);
            }
        } else {
            // 不同毫秒，序列重置为0
            sequence = 0L;
        }

        // 更新上次生成ID的时间戳
        lastTimestamp = timestamp;

        // 生成ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 获取当前时间戳
     *
     * @return 当前时间戳（毫秒）
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 等待下一毫秒
     *
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 新的时间戳
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }

    /**
     * 生成字符串类型的ID
     *
     * @return 字符串类型的ID
     */
    public String nextIdStr() {
        return String.valueOf(nextId());
    }
}
