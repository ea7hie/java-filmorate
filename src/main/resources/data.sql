INSERT INTO mpa_ratings (mpa_rating_id, description)
SELECT 1, 'G'
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_rating_id = 1);

INSERT INTO mpa_ratings (mpa_rating_id, description)
SELECT 2, 'PG'
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_rating_id = 2);

INSERT INTO mpa_ratings (mpa_rating_id, description)
SELECT 3, 'PG-13'
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_rating_id = 3);

INSERT INTO mpa_ratings (mpa_rating_id, description)
SELECT 4, 'R'
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_rating_id = 4);

INSERT INTO mpa_ratings (mpa_rating_id, description)
SELECT 5, 'NC-17'
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE mpa_rating_id = 5);



INSERT INTO GENRES (genre_id, genre_name)
SELECT 1, 'Комедия'
WHERE NOT EXISTS (SELECT 1 FROM GENRES WHERE genre_id = 1);

INSERT INTO GENRES (genre_id, genre_name)
SELECT 2, 'Драма'
WHERE NOT EXISTS (SELECT 1 FROM GENRES WHERE genre_id = 2);

INSERT INTO GENRES (genre_id, genre_name)
SELECT 3, 'Мультфильм'
WHERE NOT EXISTS (SELECT 1 FROM GENRES WHERE genre_id = 3);

INSERT INTO GENRES (genre_id, genre_name)
SELECT 4, 'Триллер'
WHERE NOT EXISTS (SELECT 1 FROM GENRES WHERE genre_id = 4);

INSERT INTO GENRES (genre_id, genre_name)
SELECT 5, 'Документальный'
WHERE NOT EXISTS (SELECT 1 FROM GENRES WHERE genre_id = 5);

INSERT INTO GENRES (genre_id, genre_name)
SELECT 6, 'Боевик'
WHERE NOT EXISTS (SELECT 1 FROM GENRES WHERE genre_id = 6);