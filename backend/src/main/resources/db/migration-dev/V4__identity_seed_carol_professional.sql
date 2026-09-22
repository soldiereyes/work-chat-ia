-- Second PROFESSIONAL on Account Alpha (integration tests: non-author edit 403)
-- password: secret123
INSERT INTO users (id, account_id, email, password_hash, display_name) VALUES
    ('22222222-2222-2222-2222-222222222204', '11111111-1111-1111-1111-111111111101', 'carol@workchat.test',
     '$2b$10$LqwMcoXXifCYkSjcQwK.dudE9icjDOWTxyzwwLEmBBJjjXw9uMyMy', 'Carol Professional');

INSERT INTO user_roles (user_id, role_id) VALUES
    ('22222222-2222-2222-2222-222222222204', '33333333-3333-3333-3333-333333333301');
