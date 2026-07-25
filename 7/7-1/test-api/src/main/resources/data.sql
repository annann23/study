INSERT INTO user_level (name) VALUES ('일반회원');
INSERT INTO user_level (name) VALUES ('VIP회원');
INSERT INTO user_level (name) VALUES ('VVIP회원');
INSERT INTO user_level (name) VALUES ('관리자');

INSERT INTO board_type (name) VALUES ('일반');
INSERT INTO board_type (name) VALUES ('갤러리');
INSERT INTO board_type (name) VALUES ('포럼');

-- PERMISSIONS
INSERT INTO permissions (name) VALUES ('BOARD_CREATE');
INSERT INTO permissions (name) VALUES ('BOARD_UPDATE');
INSERT INTO permissions (name) VALUES ('BOARD_DELETE');

INSERT INTO permissions (name) VALUES ('POST_CREATE');
INSERT INTO permissions (name) VALUES ('POST_UPDATE_OWN');
INSERT INTO permissions (name) VALUES ('POST_UPDATE_ANY');
INSERT INTO permissions (name) VALUES ('POST_DELETE_OWN');
INSERT INTO permissions (name) VALUES ('POST_DELETE_ANY');

INSERT INTO permissions (name) VALUES ('COMMENT_CREATE');
INSERT INTO permissions (name) VALUES ('COMMENT_UPDATE_OWN');
INSERT INTO permissions (name) VALUES ('COMMENT_UPDATE_ANY');
INSERT INTO permissions (name) VALUES ('COMMENT_DELETE_OWN');
INSERT INTO permissions (name) VALUES ('COMMENT_DELETE_ANY');

INSERT INTO permissions (name) VALUES ('USER_UPDATE_OWN');
INSERT INTO permissions (name) VALUES ('USER_UPDATE_ANY');
INSERT INTO permissions (name) VALUES ('USER_DELETE_OWN');
INSERT INTO permissions (name) VALUES ('USER_DELETE_ANY');
INSERT INTO permissions (name) VALUES ('USER_LEVEL_ASSIGN');

INSERT INTO permissions (name) VALUES ('ROLE_CREATE');
INSERT INTO permissions (name) VALUES ('ROLE_UPDATE');
INSERT INTO permissions (name) VALUES ('ROLE_DELETE');
INSERT INTO permissions (name) VALUES ('ROLE_ASSIGN');

INSERT INTO permissions (name) VALUES ('LIKE_CREATE');
INSERT INTO permissions (name) VALUES ('LIKE_DELETE');

INSERT INTO permissions (name) VALUES ('ATTACHMENT_CREATE');
INSERT INTO permissions (name) VALUES ('ATTACHMENT_DELETE_OWN');
INSERT INTO permissions (name) VALUES ('ATTACHMENT_DELETE_ANY');

-- ROLES
INSERT INTO roles (name) VALUES ('USER'); --일반사용자
INSERT INTO roles (name) VALUES ('MODERATOR'); --운영자
INSERT INTO roles (name) VALUES ('ADMIN'); --총관리자

-- USER 권한 목록
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'USER' AND p.name IN (
    'POST_CREATE',
    'POST_UPDATE_OWN',
    'POST_DELETE_OWN',
    'COMMENT_CREATE',
    'COMMENT_UPDATE_OWN',
    'COMMENT_DELETE_OWN',
    'LIKE_CREATE',
    'LIKE_DELETE',
    'USER_UPDATE_OWN',
    'USER_DELETE_OWN',
    'ATTACHMENT_CREATE',
    'ATTACHMENT_DELETE_OWN'
);

-- MODERATOR 권한 목록
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'MODERATOR' AND p.name IN (
    'POST_CREATE',
    'POST_UPDATE_ANY',
    'POST_DELETE_ANY',
    'COMMENT_CREATE',
    'COMMENT_UPDATE_ANY',
    'COMMENT_DELETE_ANY',
    'LIKE_CREATE',
    'LIKE_DELETE',
    'USER_UPDATE_OWN',
    'USER_DELETE_OWN',
    'ATTACHMENT_CREATE',
    'ATTACHMENT_DELETE_ANY',
    'USER_LEVEL_ASSIGN'
);

-- ADMIN 권한(전부 가능)
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN';
