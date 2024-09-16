package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestHashCache {
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private Queue<String> hashesCache;
    @Mock
    private AtomicBoolean running;
    @InjectMocks
    private HashCache hashCache;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @BeforeEach
    public void setup() {
        hashCache = new HashCache(hashGenerator, executorService);
        List<String> hashes = new ArrayList<>(Arrays.asList("hash1", "hash2", "hash3", "hash4", "hash5", "hash6"));
//        when(hashGenerator.getHashes(anyInt())).thenReturn(hashes);
//        hashCache.init();
        this.hashesCache.addAll(hashes);
    }

    @Test
    public void getHashLowPercentageTest() {
        ReflectionTestUtils.setField(hashCache, "capacity", 10);
        ReflectionTestUtils.setField(hashCache, "lowFillPercentage", 20);
        hashesCache.add("hash1");
        List<String> hashes = List.of("hash1", "hash2", "hash3", "hash4", "hash5", "hash6");
        when(hashGenerator.getHashes(10)).thenReturn(hashes);
        String result = hashCache.getHash();
        assertEquals("hash1", result);
        assertEquals("hash2", hashCache.getHash());
        assertEquals("hash3", hashCache.getHash());
        assertEquals("hash4", hashCache.getHash());
    }

    @Test
    public void getHashTest() {
        running = new AtomicBoolean(false);
        hashesCache = new ArrayDeque<>(10);
        hashesCache.add("hash1");
        hashesCache.add("hash2");
        hashesCache.add("hash3");
        hashesCache.add("hash4");
        hashesCache.add("hash5");
        ReflectionTestUtils.setField(hashCache, "capacity", 10);
        ReflectionTestUtils.setField(hashCache, "lowFillPercentage", 20);
        when(hashesCache.poll()).thenReturn("hash1");
        String result = hashCache.getHash();
        assertEquals("hash1", result);
    }
}
