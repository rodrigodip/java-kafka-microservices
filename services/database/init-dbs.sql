-- ==========================================
-- 1. CRIAÇÃO DOS USUÁRIOS (ROLES) E SENHAS
-- ==========================================
CREATE USER user_produtos WITH PASSWORD 'senha_produtos_123';
CREATE USER user_clientes WITH PASSWORD 'senha_clientes_123';

-- ==========================================
-- 2. CRIAÇÃO DOS BANCOS DE DADOS (COM SEUS DONOS)
-- ==========================================
CREATE DATABASE products OWNER user_produtos;
CREATE DATABASE clients OWNER user_clientes;

-- ==========================================
-- 3. GARANTIA DE PERMISSÕES TOTAIS
-- ==========================================
GRANT ALL PRIVILEGES ON DATABASE products TO user_produtos;
GRANT ALL PRIVILEGES ON DATABASE clients TO user_clientes;