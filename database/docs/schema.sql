-- ==============================================================
-- GMA SCHOOL MANAGEMENT SYSTEM - CONSOLIDATED SCHEMA
-- ==============================================================

DROP DATABASE IF EXISTS gma_school;
CREATE DATABASE gma_school CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gma_school;

-- ==============================================================
-- LOOKUP TABLES
-- ==============================================================

CREATE TABLE move_categories (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Move categories (optional, helps UI grouping)',
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE member_types (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '(''Regular''),(''Prospect''),(''WSD''),(''Instructor''),(''Workshop''),(''Parent'');',
    name VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE roles (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Roles for RBAC,  ADMIN, INSTRUCTOR, VIEWER',
    name VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- CORE ENTITIES
-- ==============================================================

CREATE TABLE students (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    external_code  VARCHAR(30) UNIQUE,
    first_name     VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    email          VARCHAR(255) UNIQUE,
    phone          VARCHAR(30),
    member_type_id BIGINT NOT NULL,
    is_active      TINYINT(1) NOT NULL DEFAULT '1',
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_student_active (is_active),
    KEY idx_student_external (external_code),
    KEY fk_student_member_type (member_type_id),
    CONSTRAINT fk_student_member_type FOREIGN KEY (member_type_id) 
        REFERENCES member_types(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE levels (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT ' Levels (belts/ranks)',
    code         VARCHAR(20) NOT NULL UNIQUE,
    display_name VARCHAR(50) NOT NULL,
    order_seq    INT NOT NULL,
    description  TEXT,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_level_order (order_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE moves (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Moves (the atomic techniques)',
    name                 VARCHAR(100) NOT NULL UNIQUE,
    description          TEXT,
    move_categories_id   BIGINT NOT NULL,
    created_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY fk_moves_move_categories1_idx (move_categories_id),
    CONSTRAINT fk_moves_move_categories1 FOREIGN KEY (move_categories_id) 
        REFERENCES move_categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE level_requirements (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Requirements (the checklist items for a level)',
    level_id             BIGINT NOT NULL,
    move_id              BIGINT NOT NULL,
    sort_order           INT NOT NULL,
    level_specific_notes TEXT,
    is_required          TINYINT(1) DEFAULT '1',
    created_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY level_id (level_id, move_id),
    KEY fk_lr_move (move_id),
    KEY idx_requirement_sort (level_id, sort_order),
    CONSTRAINT fk_lr_level FOREIGN KEY (level_id) REFERENCES levels(id) ON DELETE CASCADE,
    CONSTRAINT fk_lr_move FOREIGN KEY (move_id) REFERENCES moves(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE student_levels (
    student_id  BIGINT PRIMARY KEY COMMENT 'Student ⇢ Level (current rank for each student)',
    level_id    BIGINT NOT NULL,
    assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY fk_sl_level (level_id),
    CONSTRAINT fk_sl_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_sl_level FOREIGN KEY (level_id) REFERENCES levels(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE attendances (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Attendance (immutable class sessions)',
    class_date DATE NOT NULL,
    notes      TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_attendance_date (class_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE attendance_entries (
    attendance_id BIGINT NOT NULL COMMENT 'Attendance entries (which students were present)',
    student_id    BIGINT NOT NULL,
    PRIMARY KEY (attendance_id, student_id),
    KEY idx_attendance_student (student_id),
    CONSTRAINT fk_attendance FOREIGN KEY (attendance_id) REFERENCES attendances(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_attendance FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE student_progress (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Student progress (completion of each requirement)',
    student_id           BIGINT NOT NULL,
    level_requirement_id BIGINT NOT NULL,
    completed_at         DATETIME DEFAULT NULL,
    instructor_id        BIGINT DEFAULT NULL,
    attempts             INT DEFAULT '0',
    notes                TEXT,
    created_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY student_id (student_id, level_requirement_id),
    KEY fk_sp_instructor (instructor_id),
    KEY idx_progress_student (student_id),
    KEY idx_progress_req (level_requirement_id),
    CONSTRAINT fk_sp_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_sp_requirement FOREIGN KEY (level_requirement_id) REFERENCES level_requirements(id) ON DELETE CASCADE,
    CONSTRAINT fk_sp_instructor FOREIGN KEY (instructor_id) REFERENCES students(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- AUTHENTICATION
-- ==============================================================

CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Users (login credentials)',
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(200) DEFAULT NULL,
    student_id    BIGINT DEFAULT NULL,
    is_active     TINYINT(1) NOT NULL DEFAULT '1',
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY fk_user_student (student_id),
    KEY idx_user_email (email),
    CONSTRAINT fk_user_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL COMMENT 'Bridge table: which user has which role(s)',
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    KEY fk_ur_role (role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- AUDIT LOG
-- ==============================================================

CREATE TABLE audit_log (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL COMMENT 'who performed the action',
    action      VARCHAR(50) NOT NULL COMMENT 'INSERT, UPDATE, DELETE, LOGIN',
    entity      VARCHAR(50) NOT NULL COMMENT 'table name (students, attendances)',
    entity_id   BIGINT DEFAULT NULL COMMENT 'PK of the affected row (nullable for bulk ops)',
    description TEXT,
    user_agent  VARCHAR(255) DEFAULT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY fk_audit_user (user_id),
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- INITIAL DATA INSERTS
-- ============================================================================

-- Default roles
INSERT INTO roles (name) VALUES 
('ADMIN'),
('INSTRUCTOR'), 
('DIRECTOR'),
('STUDENT');
