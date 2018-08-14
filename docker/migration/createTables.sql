\c tweets

CREATE TABLE public.nba
(
    id SERIAL PRIMARY KEY,
    message varchar NOT NULL
);

ALTER TABLE public.nba
    OWNER to postgres;