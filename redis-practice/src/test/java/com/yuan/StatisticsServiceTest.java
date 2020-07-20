package com.yuan;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author yuan
 * @date 2020/7/20 2:30 下午
 */
public class StatisticsServiceTest {

    private StatisticsService statisticsService = new StatisticsService();
    private static final String homeIndex = "home";
    private static final String userList = "user-list";

    @Test
    public void addCount() {
        //100个首页访问
        for (int i = 0; i < 100; i++) {
            statisticsService.addCount(i + 1, homeIndex);
        }
        //模拟重复访问
        for (int i = 0; i < 100; i++) {
            statisticsService.addCount(i + 1, homeIndex);
        }
        //30个用户列表页面访问
        for (int i = 0; i < 30; i++) {
            statisticsService.addCount(i + 1, userList);
        }
    }

    @Test
    public void getCount() {
        System.out.println("home index uv =  "+statisticsService.getCount(homeIndex));
        System.out.println("user list uv =  "+statisticsService.getCount(userList));
    }
}