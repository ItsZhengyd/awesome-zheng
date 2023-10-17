package org.example;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    @Test
    void newRecordClass(){
        record Person(String name, int age) {}
        Person alice = new Person("Alice", 30);
        assertEquals(30, alice.age);
        assertEquals("Alice", alice.name);
        assertEquals("Person[name=Alice, age=30]",alice.toString());
    }


    /**
     * 文本块
     */
    @Test
    void textBlocks(){
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
    void newStreamAPI(){
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        long count = names.stream().filter(name -> name.startsWith("A")).count();
        assertEquals(1, count);
    }
}