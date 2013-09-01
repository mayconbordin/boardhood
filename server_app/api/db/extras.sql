--
-- POSTGIS
--

-- WGS84
SELECT AddGeometryColumn('public', 'conversations', 'location_wgs84', 4326, 'POINT', 2);

-- SELECT AddGeometryColumn('public', 'conversations', 'location', 32661, 'POINT', 2);
CREATE INDEX index_location_wgs84 ON conversations USING GIST (location_wgs84);


--
-- INDEXES
--
CREATE INDEX index_name_prefix ON interests USING btree ( lower (name) text_pattern_ops);
