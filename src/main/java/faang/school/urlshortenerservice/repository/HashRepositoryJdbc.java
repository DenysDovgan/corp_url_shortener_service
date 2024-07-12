package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HashRepositoryJdbc implements HashRepository {

    private final JdbcTemplate jdbcTemplate;

    @Value("${url.hash.save-batch-size}")
    private int saveHashBatchSize;

    @Value("${url.hash.get-batch-size}")
    private int getHashBatchSize;

    @Override
    public List<Long> getUniqueNumbers(long n) {
        log.info("Get {} unique numbers from DB sequence", n);
        return jdbcTemplate.queryForList(String.format("SELECT nextval('unique_number_seq') FROM generate_series(1, %d)", n), Long.class);
    }

    @Transactional
    @Override
    public void save(List<String> hashes) {
        log.info("Save hashes with batch size: {}", saveHashBatchSize);
        jdbcTemplate.batchUpdate("INSERT INTO hash VALUES (?)",
                hashes,
                saveHashBatchSize,
                (ps, argument) -> ps.setString(1, argument));
    }

    @Transactional
    @Override
    public List<String> getHashBatch() {
        log.info("Get {} hashes from DB", getHashBatchSize);
        return jdbcTemplate.queryForList(
                String.format("DELETE FROM hash WHERE hash IN (SELECT hash FROM hash LIMIT %d) RETURNING hash", getHashBatchSize),
                String.class);
    }
}