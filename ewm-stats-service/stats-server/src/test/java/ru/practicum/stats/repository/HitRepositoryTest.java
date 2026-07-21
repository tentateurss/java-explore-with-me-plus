package ru.practicum.stats.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.stats.model.Hit;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class HitRepositoryTest {

    @Autowired
    private HitRepository hitRepository;

    @Test
    void testSaveHit() {
        // создаем объект
        Hit hit = new Hit();
        hit.setApp("test-app");
        hit.setUri("/test");
        hit.setIp("127.0.0.1");
        hit.setTimestamp(LocalDateTime.now());

        // сохраняем в бд
        Hit saved = hitRepository.save(hit);

        // проверяем что сохранилось
        assertNotNull(saved.getId());
        assertEquals("test-app", saved.getApp());
        assertEquals("/test", saved.getUri());
        assertEquals("127.0.0.1", saved.getIp());
        assertNotNull(saved.getTimestamp());
    }

    @Test
    void testFindAll() {
        // создаём и сохраняем объект
        Hit hit = new Hit();
        hit.setApp("test-app");
        hit.setUri("/test");
        hit.setIp("127.0.0.1");
        hit.setTimestamp(LocalDateTime.now());
        hitRepository.save(hit);

        // получаем все записи
        var all = hitRepository.findAll();

        // проверяем, что не пусто
        assertFalse(all.isEmpty());
        assertEquals(1, all.size());
    }
}
