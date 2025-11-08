-- ============================================
-- PostgreSQL Schema for OAuth2JWT Application
-- ============================================
-- Bu script'i pgAdmin Query Tool'da çalıştırabilirsiniz
-- Adımlar:
-- 1. pgAdmin'de oauth2jwt veritabanına sağ tıklayın
-- 2. "Query Tool" seçin
-- 3. Bu script'in içeriğini yapıştırın
-- 4. F5 tuşuna basın veya "Execute" butonuna tıklayın
-- ============================================

-- Create users table (IF NOT EXISTS to prevent data loss)
CREATE TABLE IF NOT EXISTS t_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);

-- Create user roles table (IF NOT EXISTS to prevent data loss)
CREATE TABLE IF NOT EXISTS t_user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES t_users(id) ON DELETE CASCADE
);

-- Create products table (IF NOT EXISTS to prevent data loss)
CREATE TABLE IF NOT EXISTS t_products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    stock INTEGER NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES t_users(id) ON DELETE CASCADE
);

-- Create indexes for better performance (IF NOT EXISTS to prevent errors)
CREATE INDEX IF NOT EXISTS idx_users_username ON t_users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON t_users(email);
CREATE INDEX IF NOT EXISTS idx_products_user_id ON t_products(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON t_user_roles(user_id);

-- Create sequence if not exists (PostgreSQL 10+ auto-creates, but for compatibility)
CREATE SEQUENCE IF NOT EXISTS t_users_id_seq;
CREATE SEQUENCE IF NOT EXISTS t_products_id_seq;

