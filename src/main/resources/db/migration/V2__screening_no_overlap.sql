CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE screenings ADD COLUMN end_time TIMESTAMP(6);


UPDATE screenings s
SET end_time = s.start_time + m.duration * INTERVAL '1 minute'
FROM movies m
WHERE m.movie_id = s.movie_id;


ALTER TABLE screenings ALTER COLUMN end_time SET NOT NULL;

ALTER TABLE screenings ADD CONSTRAINT ex_screenings_no_overlap 
EXCLUDE USING GIST (hall_id WITH =, tsrange(start_time, end_time, '[)') WITH &&)