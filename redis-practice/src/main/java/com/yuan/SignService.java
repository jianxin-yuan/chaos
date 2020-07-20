package com.yuan;

import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuan
 * @date 2020/7/20 9:59 上午
 * 使用redis bitmap 实现用户签到统计功能(以月为单位统计)
 * 注意: 因为redis协议中有符号整型最大支持64位，而无符号整型最大支持63位,
 *      如果以年为单位统计,则`BITFIELD u:sign:1001:202007 GET u365 0` 命令会报错,因为超出了范围
 *      所以只能循环BITFIELD u:sign:1001:202007 GET u63 0,
 *                BITFIELD u:sign:1001:202007 GET u63 64
 *             这样多取几次
 * <p>
 * 1 用户签到
 * 2 检查签到
 * 3 统计签到次数
 * 4 获取当月连续签到次数
 * 5 获取当月首次签到日期
 * 6 获取当月签到情况
 */
public class SignService {

    private Jedis jedis = new Jedis();

    /**
     * 用户签到
     *
     * @param userId    用户id
     * @param localDate 日期
     * @return true / false
     */
    public boolean doSign(int userId, LocalDate localDate) {
        //当前日期-1,表示位图中的坐标
        int offset = localDate.getDayOfMonth() - 1;
        //SETBIT u:sign:1001:202007 12 1
        return jedis.setbit(getKey(userId, localDate), offset, true);
    }

    /**
     * 检查是否签到
     *
     * @param userId
     * @param localDate
     * @return
     */
    public boolean checkSign(int userId, LocalDate localDate) {
        int offset = localDate.getDayOfMonth() - 1;
        //GETBIT u:sign:1001:202007 9
        return jedis.getbit(getKey(userId, localDate), offset);
    }

    /**
     * 统计签到天数
     *
     * @param userId
     * @param localDate
     * @return
     */
    public long signCount(int userId, LocalDate localDate) {
        //BITCOUNT u:sign:1001:202007
        return jedis.bitcount(getKey(userId, localDate));
    }

    /**
     * 获取当月首次签到日期
     *
     * @param userId
     * @param localDate
     * @return
     */
    public LocalDate getFirstSignDate(int userId, LocalDate localDate) {
        //BITPOS u:sign:1001:202007 1 : 首次出现1的位,从0开始,加1正好表示天数
        Long bit = jedis.bitpos(getKey(userId, localDate), true);
        return bit < 0 ? null : localDate.withDayOfMonth((int) (bit + 1));
    }


    /**
     * 统计连续签到天数
     *
     * @param uid
     * @param localDate
     * @return
     */
    public long getContinuousSignCount(int uid, LocalDate localDate) {
        int signCount = 0;
        //获取当天之前的数据
        //BITFIELD u:sign:1001:202007 GET u20 0 :从offset=0开始,获取前20天无符号数
        String type = String.format("u%d", localDate.getDayOfMonth());
        List<Long> list = jedis.bitfield(getKey(uid, localDate), "GET", type, "0");
        if (list != null && list.size() > 0) {
            // 取低位连续不为0的个数即为连续签到次数，需考虑当天尚未签到的情况
            long v = list.get(0) == null ? 0 : list.get(0);
            for (int i = 0; i < localDate.getDayOfMonth(); i++) {
                // 右移一位,再左移一位,如果等于原来的数,说明末位为0
                if (v >> 1 << 1 == v) {
                    // 低位为0且非当天说明连续签到中断了
                    if (i > 0) {
                        break;
                    }
                } else {
                    signCount += 1;
                }
                //右移一位
                v >>= 1;
            }
        }
        return signCount;
    }

    /**
     * 获取签到情况
     *
     * @param uid
     * @param localDate
     * @return
     */
    public Map<String, Boolean> getSignInfo(int uid, LocalDate localDate) {
        Map<String, Boolean> map = new LinkedHashMap<>();
        //获取当月所有的数据:前31位数据
        //BITFIELD u:sign:1001:202007 GET u31 0
        String type = String.format("u%d", localDate.lengthOfMonth());
        List<Long> list = jedis.bitfield(getKey(uid, localDate), "GET", type, "0");
        if (list != null && list.size() > 0) {
            long v = list.get(0) == null ? 0 : list.get(0);
            for (int i = localDate.lengthOfMonth(); i > 0; i--) {
                LocalDate date = localDate.withDayOfMonth(i);
                map.put(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), v >> 1 << 1 != v);
                v >>= 1;
            }
        }
        return map;
    }

    /**
     * 获取缓存key
     *
     * @param userId
     * @param localDate
     * @return
     */
    private String getKey(int userId, LocalDate localDate) {
        // u:sign:100:202007
        return String.format("u:sign:%d:%s", userId, localDate.format(DateTimeFormatter.ofPattern("yyyyMM")));
    }


}
