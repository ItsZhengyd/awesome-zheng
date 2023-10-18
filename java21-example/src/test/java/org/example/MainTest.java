package org.example;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 全面的新特性见<a href="https://jdk.java.net/21/">官方文档</a>
 */
class MainTest {

    /**
     * 虚拟线程
     */
    @Test
    void virtualThread() {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(1, 10000).forEach(i ->
                    executor.submit(() -> {
                        Thread.sleep(Duration.ofSeconds(i));
                        System.out.println("Hello World");
                        return i;
                    }));
        }
    }

    /**
     * Record class
     */
    record Person(String name, int age) {
    }

    @Test
    void newRecordClass() {
        Person alice = new Person("Alice", 30);
        assertEquals(30, alice.age);
        assertEquals("Alice", alice.name);
        assertEquals("Person[name=Alice, age=30]", alice.toString());
        check(alice);
    }

    void check(Object o) {
        if (o instanceof Person(String name, int age)) {
            assertEquals(30, age);
            assertEquals("Alice", name);
            assertEquals("Person[name=Alice, age=30]", o.toString());
        }
    }


    /**
     * 增强 switch
     */
    @Test
    void enhancedSwitch() {
        Object[] objects = {"String", 1, new Person("Alice", 30)};
        for (Object object : objects) {
            switch (object) {
                case String s:
                    assertEquals("String", s);
                    break;
                case Integer i:
                    assertEquals(1, i);
                    break;
                case Person(String name, int age):
                    assertEquals(30, age);
                    assertEquals("Alice", name);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + object);
            }
        }
    }

    /**
     * 文本块
     */
    @Test
    void textBlocks() {
        String text = """
                      静夜思
                    窗前明月光，
                    疑是地上霜。
                    举头望明月，
                    低头思故乡。
                """;
        System.out.println(text);
    }

    /**
     * 新的 stream API
     */
    @Test
    void newStreamAPI() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        long count = names.stream().filter(name -> name.startsWith("A")).count();
        assertEquals(1, count);
    }
}