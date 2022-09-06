package com.zhangyu.esdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.comparator.Comparators;

import java.util.*;

/**
 * @author zhangyu
 * @date 2022/9/3 09:42
 */
@SpringBootTest
public class JiQiTest {
    @Test
    void test1() {
        System.out.println(pingpang(30, 31));
    }

    int pingpang(int a, int b) {
        if (b <= 9) {
            return 11 - a;
        }
        return b - a + 2;
    }

    @Test
    void test2() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        // arrayList.add(0);
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(3);
        arrayList.add(4);
        // arrayList.add(2);
        System.out.println(mex(arrayList));
        for (int i = 0; i < arrayList.size(); i++) {
            ArrayList<Integer> temp = new ArrayList<>();
            temp.addAll(arrayList.subList(0, i));
            temp.addAll(arrayList.subList(i + 1, arrayList.size()));

            System.out.println(temp);
            System.out.println(mex(temp));
        }
    }

    int mex(List<Integer> list) {
        int n = list.size();
        for (int i = 0; i < n; i++) {
            if (!list.contains(i)) {
                return i;
            }
        }
        return n;
    }

    @Test
    void test3() {
        int n = 3, m = 3, k = 10;
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        List<Integer> list1 = new ArrayList<>();
        list1.addAll(list);
        Collections.reverse(list);
        list1.addAll(list);

        System.out.println(list1.get(k % (2 * n) - 1));

        // ArrayList<Integer> result = new ArrayList<>();
        // n 个数， 镜像 m 次，第 k 个数是几
        // process(1, m, list, result);
        // result.addAll(list);

        // 结果：
        // System.out.println(result.get(k - 1));

    }

    void process(int i, int m, List<Integer> list, List<Integer> result) {
        if (i > m) {
            return;
        }

        process(i + 1, m, list, result);
        result.addAll(list);

        Collections.reverse(list);
        process(i + 1, m, list, result);
    }

    @Test
    void test4() {
        ArrayList<Integer> list = new ArrayList<>();
        // list.add(1399);
        // list.add(1411);
        // list.add(2452);
        // list.add(1377);
        // list.add(1284);
        // list.add(1348);
        list.add(901);
        list.add(925);
        list.add(920);
        list.add(895);
        list.add(853);
        list.add(805);
        list.add(894);
        list.add(805);
        list.add(858);
        list.add(845);
        list.add(826);
        list.add(859);
        list.add(775);
        list.add(784);
        list.add(838);

        int n = 15;
        int k = 4;

        int[] result = new int[n - k + 1];

        for (int i = 0; i <= n - k; i++) {
            List<Integer> integers = new ArrayList<>(list.subList(i, k + i));
            result[i] = getMid(integers);
        }

        System.out.println(Arrays.toString(Arrays.stream(result).toArray()));

    }

    int getMid(List<Integer> list) {
        list.sort(Comparators.comparable());
        int rightIndex = list.size() - 1;
        int midIndex = rightIndex >> 1;

        double mid;
        if (rightIndex % 2 == 0) {
            mid = list.get(midIndex);
        } else {
            mid = (double) (list.get(midIndex) + list.get(midIndex + 1)) / 2;
        }

        return list.get(rightIndex) - mid > mid - list.get(0) ? list.get(rightIndex) : list.get(0);
    }

    @Test
    void test5() {
        double c = 3.3213;
        double a = 1.2331;
        double b = 2.3423;
        System.out.println(b -a  >c - a);
    }


}
