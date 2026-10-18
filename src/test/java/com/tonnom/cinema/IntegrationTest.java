package com.tonnom.cinema;


import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import (TestcontainersConfiguration.class)
public abstract class IntegrationTest {

    @Autowired protected StringRedisTemplate redis;
    @Autowired protected JdbcTemplate jdbc;

    protected  static final Long SCREENING = 1L;
    protected  static final Long USER1 = 1L;
    protected  static final Long USER2 = 2L;

    @BeforeEach 
    void reset(){
        redis.getConnectionFactory().getConnection().serverCommands().flushAll();
        jdbc.execute("TRUNCATE booking_seats, bookings RESTART IDENTITY");
    }

}
