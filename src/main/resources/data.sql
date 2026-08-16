-- Jeu de donnees initial du catalogue, charge au demarrage (base H2 en memoire).
INSERT INTO article (id, name, category, price, created_at, updated_at) VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Soccer Ball',          'Team Sports',   29.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Tennis Racket',        'Racket Sports', 89.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('c3d4e5f6-a7b8-9012-cdef-123456789012', 'Running Shoes',        'Running',      119.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('d4e5f6a7-b8c9-0123-def0-234567890123', 'Yoga Mat',             'Fitness',       24.90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('e5f6a7b8-c9d0-1234-ef01-345678901234', 'Mountain Bike Helmet', 'Cycling',       59.95, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
