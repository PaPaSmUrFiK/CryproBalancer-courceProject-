-- Таблица userrole (Role)
CREATE TABLE crypto_balancer.userrole (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
);

-- Таблица appuser (User)
CREATE TABLE crypto_balancer.appuser (
    user_id SERIAL PRIMARY KEY,
    role_id INT NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(60) NOT NULL, -- Увеличено до 60 для поддержки BCrypt
    FOREIGN KEY (role_id) REFERENCES crypto_balancer.userrole(role_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Таблица portfolio (Portfolio)
CREATE TABLE crypto_balancer.portfolio (
    portfolio_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    portfolio_name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES crypto_balancer.appuser(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Таблица analytic (Analytic)
CREATE TABLE crypto_balancer.analytic (
    portfolio_id INT PRIMARY KEY,
    risk NUMERIC(10, 6) NOT NULL,
    expected_return NUMERIC(10, 6) NOT NULL,
    FOREIGN KEY (portfolio_id) REFERENCES crypto_balancer.portfolio(portfolio_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Таблица crypto (Crypto)
CREATE TABLE crypto_balancer.crypto (
    crypto_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    symbol VARCHAR(10) NOT NULL UNIQUE
);

-- Таблица cryptohistory (CryptoHistory)
CREATE TABLE crypto_balancer.cryptohistory (
    history_id SERIAL PRIMARY KEY,
    crypto_id INT NOT NULL,
    date_changed DATE NOT NULL,
    price NUMERIC(20, 8) NOT NULL,
    FOREIGN KEY (crypto_id) REFERENCES crypto_balancer.crypto(crypto_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Таблица investment (Investment)
CREATE TABLE crypto_balancer.investment (
    investment_id SERIAL PRIMARY KEY,
    portfolio_id INT NOT NULL,
    crypto_id INT NOT NULL,
    purchase_price NUMERIC(20, 8) NOT NULL,
    amount NUMERIC(20, 8) NOT NULL,
    FOREIGN KEY (portfolio_id) REFERENCES crypto_balancer.portfolio(portfolio_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (crypto_id) REFERENCES crypto_balancer.crypto(crypto_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Индексы для оптимизации запросов
CREATE INDEX idx_appuser_role_id ON crypto_balancer.appuser(role_id);
CREATE INDEX idx_portfolio_user_id ON crypto_balancer.portfolio(user_id);
CREATE INDEX idx_cryptohistory_crypto_id ON crypto_balancer.cryptohistory(crypto_id);
CREATE INDEX idx_investment_portfolio_id ON crypto_balancer.investment(portfolio_id);
CREATE INDEX idx_investment_crypto_id ON crypto_balancer.investment(crypto_id);
