CREATE OR REPLACE FUNCTION metersBetweenTwoGeographicalPoints(
    point1_longitude DOUBLE PRECISION,
    point1_latitude DOUBLE PRECISION,
    point2_longitude DOUBLE PRECISION,
    point2_latitude DOUBLE PRECISION
)
    RETURNS FLOAT AS
$$
BEGIN
    RETURN ST_DISTANCE(
            st_setsrid(ST_MakePoint(point1_longitude, point1_latitude), 4326)::geography,
            st_setsrid(ST_MakePoint(point2_longitude, point2_latitude), 4326)::geography
           );
END;
$$ LANGUAGE plpgsql;
