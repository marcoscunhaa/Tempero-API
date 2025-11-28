-- ================================
-- 1) Criar banco de dados
-- ================================
CREATE DATABASE IF NOT EXISTS development
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- ================================
-- 2) Criar usuário dev/admin (se não existir)
-- ================================
CREATE USER IF NOT EXISTS 'dev'@'localhost' IDENTIFIED BY 'admin';

-- ================================
-- 3) Dar todas as permissões no banco
-- ================================
GRANT ALL PRIVILEGES ON development.* TO 'dev'@'localhost';

-- ================================
-- 4) Aplicar mudanças
-- ================================
FLUSH PRIVILEGES;

-- ================================
-- 5) Usar o banco (Hibernate cria as tabelas)
-- ================================
USE development;

-- Pronto!
-- Agora é só rodar sua aplicação Spring Boot.
-- O Hibernate criará as tabelas automaticamente.
