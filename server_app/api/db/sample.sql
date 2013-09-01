-- APPS
INSERT INTO applications(name, key, level) VALUES('Boardhood', '9c7dc77314ca22b8eec94440fa528157f8b8be03', 10);

-- -----------------------------------------------------
-- users
-- STATUS:
-- 0: PENDING
-- 1: ACTIVE
-- 2: BLOCKED
-- 3: REMOVED
-- PASSWORD: teste ou 123456
-- -----------------------------------------------------
INSERT INTO users(username, email, password, status, avatar_url) VALUES('john', 'john@example.com', '$2a$12$r2pAmjkWctt9fE.sYQbqE.X2dedzMGSVFqbP.Ds.Etg3mKXsVehIa', 1, 'https://s3.amazonaws.com/boardhood_user_avatars/sheldoncooper.png');
INSERT INTO users(username, email, password, status, avatar_url) VALUES('paul', 'paul@example.com', '$2a$12$C5ULs1Rd/QCZbyOroaWfBOIwmtXCxBMf1NlV4dxQbxYGrZ2fKym1q', 1, 'https://s3.amazonaws.com/boardhood_user_avatars/sheldoncooper.png');
INSERT INTO users(username, email, password, status, avatar_url) VALUES('jones', 'jones@example.com', '$2a$12$r2pAmjkWctt9fE.sYQbqE.X2dedzMGSVFqbP.Ds.Etg3mKXsVehIa', 1, 'https://s3.amazonaws.com/boardhood_user_avatars/sheldoncooper.png');
INSERT INTO users(username, email, password, status, avatar_url) VALUES('richard', 'richard@example.com', '$2a$12$C5ULs1Rd/QCZbyOroaWfBOIwmtXCxBMf1NlV4dxQbxYGrZ2fKym1q', 1, 'https://s3.amazonaws.com/boardhood_user_avatars/sheldoncooper.png');
INSERT INTO users(username, email, password, status, avatar_url) VALUES('max', 'max@example.com', '$2a$12$r2pAmjkWctt9fE.sYQbqE.X2dedzMGSVFqbP.Ds.Etg3mKXsVehIa', 1, 'https://s3.amazonaws.com/boardhood_user_avatars/sheldoncooper.png');
INSERT INTO users(username, email, password, status, avatar_url) VALUES('pedroluis', 'pedroluis@example.com', '$2a$12$r2pAmjkWctt9fE.sYQbqE.X2dedzMGSVFqbP.Ds.Etg3mKXsVehIa', 1, 'https://s3.amazonaws.com/boardhood_user_avatars/sheldoncooper.png');
INSERT INTO users(username, email, password, status, avatar_url) VALUES('lucas', 'lucas@example.com', '$2a$12$r2pAmjkWctt9fE.sYQbqE.X2dedzMGSVFqbP.Ds.Etg3mKXsVehIa', 1, 'https://s3.amazonaws.com/boardhood_user_avatars/sheldoncooper.png');

-- -----------------------------------------------------
-- interests
-- -----------------------------------------------------
INSERT INTO interests(name) VALUES('Photography');
INSERT INTO interests(name) VALUES('Dance');
INSERT INTO interests(name) VALUES('Television');
INSERT INTO interests(name) VALUES('Poetry');
INSERT INTO interests(name) VALUES('Painting');
INSERT INTO interests(name) VALUES('Movies');
INSERT INTO interests(name) VALUES('Cook');
INSERT INTO interests(name) VALUES('Antiques');
INSERT INTO interests(name) VALUES('Knitting');
INSERT INTO interests(name) VALUES('Sewing');
INSERT INTO interests(name) VALUES('Crafting');
INSERT INTO interests(name) VALUES('Chatting');
INSERT INTO interests(name) VALUES('Games');
INSERT INTO interests(name) VALUES('Magic');
INSERT INTO interests(name) VALUES('Cartoons');
INSERT INTO interests(name) VALUES('Sports');
INSERT INTO interests(name) VALUES('Pens');
INSERT INTO interests(name) VALUES('Fishing');
INSERT INTO interests(name) VALUES('Horse Riding');
INSERT INTO interests(name) VALUES('Computer Games');
INSERT INTO interests(name) VALUES('Biking');
INSERT INTO interests(name) VALUES('Chess');
INSERT INTO interests(name) VALUES('Postcards Collection');
INSERT INTO interests(name) VALUES('Star Gazing');
INSERT INTO interests(name) VALUES('Traveling');
INSERT INTO interests(name) VALUES('Flying Kites');
INSERT INTO interests(name) VALUES('Drawing and Sketching');
INSERT INTO interests(name) VALUES('Zoo');
INSERT INTO interests(name) VALUES('Rock');

-- -----------------------------------------------------
-- users_interests
-- -----------------------------------------------------
INSERT INTO users_interests(user_id, interest_id) VALUES(1, 1);
INSERT INTO users_interests(user_id, interest_id) VALUES(1, 2);
INSERT INTO users_interests(user_id, interest_id) VALUES(1, 3);
INSERT INTO users_interests(user_id, interest_id) VALUES(1, 4);
INSERT INTO users_interests(user_id, interest_id) VALUES(1, 5);
INSERT INTO users_interests(user_id, interest_id) VALUES(1, 6);
INSERT INTO users_interests(user_id, interest_id) VALUES(1, 13);
INSERT INTO users_interests(user_id, interest_id) VALUES(1, 29);

INSERT INTO users_interests(user_id, interest_id) VALUES(2, 1);
INSERT INTO users_interests(user_id, interest_id) VALUES(2, 12);
INSERT INTO users_interests(user_id, interest_id) VALUES(3, 1);
INSERT INTO users_interests(user_id, interest_id) VALUES(4, 1);
INSERT INTO users_interests(user_id, interest_id) VALUES(5, 1);


-- -----------------------------------------------------
-- conversations
-- MySQL: POINT('-27.86403, -54.4593889)')
-- PostgreSQL: PointFromText('POINT('' || longitude || ' ' || latitude || ')', 4269)
-- -----------------------------------------------------
INSERT INTO conversations(latlng, message, interest_id, user_id) VALUES(POINT('-27.86403, -54.4593889'), 'Tem alguém aí?', 1, 1);
INSERT INTO conversations(latlng, message, interest_id, user_id) VALUES(POINT('-27.8724, -54.4951
'), 'Qual o pior filme do Kubrick, ou o menos bom de todos?', 6, 3);
INSERT INTO conversations(latlng, message, interest_id, user_id) VALUES(POINT('-27.8657, -54.4801'), 'AC/DC em Porto Alegre. Quem vai?', 29, 2);
INSERT INTO conversations(latlng, message, interest_id, user_id) VALUES(POINT('-27.8743, -54.4798'), 'Alguém conhece alguma trilha para andar de bicicleta? Alguém afim de ir também?', 21, 1);
INSERT INTO conversations(latlng, message, interest_id, user_id) VALUES(POINT('-27.8728, -54.4717'), 'Server para CS?', 13, 1);

-- replies
INSERT INTO conversations(latlng, message, interest_id, user_id, parent_id) VALUES(POINT('-27.8624, -54.4678'), 'Sim. Curte fotografia?', 1, 4, 1);
INSERT INTO conversations(latlng, message, interest_id, user_id, parent_id) VALUES(POINT('-27.8640, -54.4377'), 'Olha, eu não gosto muito do spartacus. Tipo, é bom, mas é muito demorado, a história poderia ter sido contada em menos tempo.', 6, 5, 2);
INSERT INTO conversations(latlng, message, interest_id, user_id, parent_id) VALUES(POINT('-27.8640, -54.4377'), 'O Spartacus da Starz é melhor. Claro, não dá para fazer uma comparação com relação aos efeitos especiais ou as liberdades dos produtores da série, mas de qualquer forma o desenrolar da história é mais interessante.', 6, 6, 2);
INSERT INTO conversations(latlng, message, interest_id, user_id, parent_id) VALUES(POINT('-27.8612, -54.4440'), 'Mas o Spartacus do Kubrick é um épico, e o da Starz é se beneficia apenas de artifícios que não haviam na época ou que não eram largamente aceitos.', 6, 7, 2);

-- more conversations
INSERT INTO conversations(latlng, message, interest_id, user_id) VALUES(POINT('-27.8720, -54.4951
'), 'Qual o pior filme do Kubrick, ou o menos bom de todos?', 6, 3);
INSERT INTO conversations(latlng, message, interest_id, user_id) VALUES(POINT('-27.8722, -54.4951
'), 'Qual o pior filme do Kubrick, ou o menos bom de todos?', 6, 3);
INSERT INTO conversations(latlng, message, interest_id, user_id) VALUES(POINT('-27.8718, -54.4951
'), 'Qual o pior filme do Kubrick, ou o menos bom de todos?', 6, 3);
INSERT INTO conversations(latlng, message, interest_id, user_id) VALUES(POINT('-27.8715, -54.4951
'), 'Qual o pior filme do Kubrick, ou o menos bom de todos?', 6, 3);
INSERT INTO conversations(latlng, message, interest_id, user_id) VALUES(POINT('-27.8727, -54.4951
'), 'Qual o pior filme do Kubrick, ou o menos bom de todos?', 6, 3);

UPDATE conversations SET location_wgs84 = GeomFromText('POINT(' || latlng[1] || ' ' || latlng[0] || ')', 4326);

--UPDATE conversations SET location = GeomFromText('POINT(' || latlng[1] || ' ' || latlng[0] || ')', 4326);
