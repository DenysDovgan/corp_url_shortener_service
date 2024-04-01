package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.HashCache;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Data
@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final UrlMapper urlMapper;

    @Transactional
    public String saveUrl(UrlDto urlDto) {
        String hash = hashCache.getHash();
        Url url = urlMapper.toEntity(urlDto);
        url.setHash(hash);
        urlRepository.save(url);
        urlCacheRepository.save(url);
        return url.getHash();
    }

    public Url getOriginalUrl(String hash) {
        if (urlCacheRepository.get(hash).isPresent()) {
            return urlCacheRepository.get(hash).get();
        } else {
            return urlRepository.findById(hash).orElseThrow(
                    () -> new EntityNotFoundException("Utl with hash " + hash + " doesnt exists."));
        }
    }

    public void removeOldHashes() {
        List<Hash> hashes = urlRepository.deleteOldHashes();
        if (hashes.size() > 0) {
            hashCache.saveHashes(hashes);
        }
    }
}