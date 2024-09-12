package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, :n)
            """)
    List<Long> getUniqueNumbers(long n);

    @Transactional
    @Query(nativeQuery = true, value = """
            DELETE FROM hash WHERE id IN (
                SELECT id FROM hash ORDER BY id ASC LIMIT :n
            ) RETURNING *
            """)
    List<String> getHashBatch(long n);
}
