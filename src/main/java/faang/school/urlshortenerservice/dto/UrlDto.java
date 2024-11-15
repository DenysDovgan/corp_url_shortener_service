package faang.school.urlshortenerservice.dto;

import lombok.Builder;

@Builder
public record UrlDto(String hash,
                     String url) {
}
