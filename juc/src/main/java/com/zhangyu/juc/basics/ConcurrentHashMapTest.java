package com.zhangyu.juc.basics;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author zhangyu
 * @date 2023/2/6 17:34
 */
@Slf4j
public class ConcurrentHashMapTest {
    @Test
    public void test1() {
        demo(() -> new ConcurrentHashMap<String, LongAdder>(),
                (map, words) -> {
                    for (String word : words) {
                        // 如果缺少一个 key,则计算生成一个 value, 然后将 key value 放入 map
                        LongAdder longAdder = map.computeIfAbsent(word, (key) -> new LongAdder());
                        // 执行累加
                        longAdder.increment();

                        /*// 检查 key 有没有
                        Integer counter = map.get(word);
                        int newValue = counter == null ? 1 : counter + 1;
                        // 没有则 put
                        map.put(word, newValue);*/
                    }
                });
    }

    public <V> void demo(Supplier<Map<String, V>> supplier,
                         BiConsumer<Map<String, V>, List<String>> consumer) {
        Map<String, V> countMap = supplier.get();
        List<Thread> ts = new ArrayList<>();
        for (int i = 0; i < 26; ++i) {
            int idx = i;
            Thread thread = new Thread(() -> {
                List<String> words = readFromFile(idx);
                consumer.accept(countMap, words);
            });
            ts.add(thread);
        }

        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println(countMap);
    }

    private List<String> readFromFile(int index) {
        return null;
    }
}
