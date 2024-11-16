package faang.school.urlshortenerservice.listener;

import faang.school.urlshortenerservice.model.UrlCache;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RedisKeyExpiredEventListener {

    private final Set<String> hashesSet = ConcurrentHashMap.newKeySet();

    @EventListener(RedisKeyExpiredEvent.class)
    public void handleRedisKeyExpiredEvent(RedisKeyExpiredEvent<UrlCache> event) {
        UrlCache urlCache = (UrlCache) event.getValue();
        if (urlCache != null) {
            hashesSet.add(urlCache.getHash());
        }
    }

    public List<String> getHashesToUpdate() {
        List<String> hashes = new ArrayList<>(hashesSet);
        hashesSet.clear();
        return hashes;
    }
}
