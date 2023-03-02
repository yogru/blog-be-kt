INSERT INTO role (id, created_at, updated_at)
VALUES ('ADMIN', '2022-02-27T21:20', '2022-02-27T21:20');

INSERT INTO role (id, created_at, updated_at)
VALUES ('USER', '2022-02-24T21:20', '2022-02-24T21:20');

INSERT INTO tag(id, created_at, updated_at)
VALUES ('All', '2022-02-24T21:20', '2022-02-24T21:20');

INSERT INTO user_entity(id, account, password, nick_name, status, created_at, updated_at)
VALUES (UNHEX(REPLACE('f3503729-afb2-4890-984e-edd1575fb1c0', '-', '')),
        'test@kyb.pe.kr',
        '{bcrypt}$2a$10$BH8p9dIDsPE/rPawjQ7iC.k1ClxXq6lrhTJczumygf2ABjvV/ZyE2',
        'test_user', 'NORMAL', '2022-02-24T21:20', '2022-02-24T21:20');


INSERT INTO user_role(id, user_id, role_id, created_at, updated_at)
VALUES (UNHEX(REPLACE('6e2a86ff-696c-4a0a-92b8-d6c15d523da5', '-', '')),
        UNHEX(REPLACE('f3503729-afb2-4890-984e-edd1575fb1c0', '-', '')),
        'USER',
        '2022-02-24T21:20',
        '2022-02-24T21:20');

