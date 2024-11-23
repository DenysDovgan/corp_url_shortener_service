package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.config.cache.CacheProperties;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private HashRepository hashRepository;

    @Mock
    private CacheProperties cacheProperties;

    @Mock
    private HashCache hashCache;

    @Mock
    private UrlMapper urlMapper;

    @Mock
    private RedisTemplate<String, Url> redisTemplate;

    private static final String HASH = "hash";
    private static final String URL = "url";
    private ValueOperations<String, Url> valueOperations;
    private List<String> existingHashes;
    private Url url;
    private UrlDto longUrlDto;
    private UrlDto shortUrlDto;

    @BeforeEach
    public void init() {
        longUrlDto = UrlDto.builder()
                .url(URL)
                .build();
        url = Url.builder()
                .hash(HASH)
                .url(URL)
                .build();
        shortUrlDto = UrlDto.builder()
                .url(HASH)
                .build();
        existingHashes = Arrays.asList(HASH);
        ReflectionTestUtils.setField(cacheProperties, "nonWorkingUrlTime", 1);
        valueOperations = mock(ValueOperations.class);
    }

    @Test
    @DisplayName("Success when clean DB")
    public void whenCleanDBThenDeleteOldUrlAndGetHashes() {
        when(urlRepository.getHashesAndDeleteExpiredUrls(cacheProperties.getNonWorkingUrlTime())).thenReturn(existingHashes);
        doNothing().when(hashRepository).saveAllHashesBatched(anyList());

        urlService.releaseExpiredHashes();

        verify(urlRepository).getHashesAndDeleteExpiredUrls(cacheProperties.getNonWorkingUrlTime());
        verify(hashRepository).saveAllHashesBatched(anyList());
    }

    @Test
    @DisplayName("Success when get long url by Redis cache")
    public void whenGetLongUrlByRedisCacheThenReturnLongUrl() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(HASH)).thenReturn(url);

        String result = urlService.getLongUrl(HASH);

        assertNotNull(result);
        assertEquals(URL, result);
        verify(redisTemplate.opsForValue()).get(HASH);
    }

    @Test
    @DisplayName("Success when get long url by urlRepository")
    public void whenGetLongUrlByUrlRepositoryThenReturnLongUrl() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(HASH)).thenReturn(null);
        when(urlRepository.findUrlByHash(HASH)).thenReturn(Optional.of(url));

        String result = urlService.getLongUrl(HASH);

        assertNotNull(result);
        assertEquals(URL, result);
        verify(redisTemplate.opsForValue()).get(HASH);
        verify(urlRepository).findUrlByHash(HASH);
    }

    @Test
    @DisplayName("Exception when get long url")
    public void whenGetLongUrlThenThrowException() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForValue().get(HASH)).thenReturn(null);
        when(urlRepository.findUrlByHash(HASH)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> urlService.getLongUrl(HASH));
    }

    @Test
    @DisplayName("Success when create short url")
    public void whenCreateShortUrlThenReturnUrlDto() {
        when(hashCache.getHash()).thenReturn(HASH);
        when(urlRepository.findUrlByHash(HASH)).thenReturn(Optional.empty());
        when(urlRepository.save(url)).thenReturn(url);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(url.getHash(), url);
        when(urlMapper.toEntity(longUrlDto, HASH)).thenReturn(url);
        when(urlMapper.toDto(url)).thenReturn(shortUrlDto);

        UrlDto result = urlService.createShortUrl(longUrlDto);

        assertNotNull(result);
        assertEquals(HASH, result.getUrl());
        verify(hashCache).getHash();
        verify(urlMapper).toEntity(longUrlDto, HASH);
        verify(urlRepository).findUrlByHash(HASH);
        verify(urlRepository).save(url);
        verify(urlMapper).toDto(url);
    }
}