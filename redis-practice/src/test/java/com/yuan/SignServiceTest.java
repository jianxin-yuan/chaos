package com.yuan;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author yuan
 * @date 2020/7/20 11:25 上午
 */
public class SignServiceTest {

    private SignService signService = new SignService();

    @Test
    public void doSign() {
        //前10天
        for (int i = 1; i <= 10; i++) {
            signService.doSign(1001, LocalDate.of(2020, 7, i));
        }

        //15号-20号
        for (int i = 15; i <= 20; i++) {
            signService.doSign(1001, LocalDate.of(2020, 7, i));
        }

    }

    @Test
    public void checkSign() {
        boolean sign = signService.checkSign(1001, LocalDate.now());
        System.out.println(LocalDate.now() + "==" + sign);
    }

    @Test
    public void signCount() {
        long count = signService.signCount(1001, LocalDate.now());
        System.out.println("sign count = " + count);
    }

    @Test
    public void getFirstSignDate() {
        LocalDate firstSignDate = signService.getFirstSignDate(1001, LocalDate.now());
        System.out.println("first sign date = " + firstSignDate);
    }

    @Test
    public void getContinuousSignCount() {
        //1047615 ==> 11111111110000111111
        long continuousSignCount = signService.getContinuousSignCount(1001, LocalDate.now());
        System.out.println("continuousSignCount = " + continuousSignCount);
    }

    @Test
    public void getSignInfo() {
        Map<String, Boolean> signInfo = signService.getSignInfo(1001, LocalDate.now());
        signInfo.forEach((k, v) -> System.out.println(k + "=" + v));
    }
}