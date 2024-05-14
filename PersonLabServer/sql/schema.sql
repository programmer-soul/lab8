CREATE SCHEMA IF NOT EXISTS public
    AUTHORIZATION s408215;

COMMENT ON SCHEMA public
    IS 'standard public schema';

GRANT USAGE ON SCHEMA public TO PUBLIC;

GRANT ALL ON SCHEMA public TO s408215;