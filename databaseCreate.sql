SET search_path TO crypto;

-- 1. userrole
CREATE TABLE userrole (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL
);

-- 2. appuser
CREATE TABLE appuser (
    user_id SERIAL PRIMARY KEY,
    role_id INT NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    CONSTRAINT fk_appuser_role FOREIGN KEY (role_id) REFERENCES userrole(role_id) ON DELETE CASCADE
);

-- 3. portfolio
CREATE TABLE portfolio (
    portfolio_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    portfolio_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_portfolio_user FOREIGN KEY (user_id) REFERENCES appuser(user_id) ON DELETE CASCADE
);

-- 4. analytic
CREATE TABLE analytic (
    portfolio_id INT PRIMARY KEY,
    expected_return NUMERIC(10,6) NOT NULL,
    risk NUMERIC(10,6) NOT NULL,
    CONSTRAINT fk_analytic_portfolio FOREIGN KEY (portfolio_id) REFERENCES portfolio(portfolio_id) ON DELETE CASCADE
);

-- 5. crypto
CREATE TABLE crypto (
    crypto_id SERIAL PRIMARY KEY,
    crypto_name VARCHAR(255) NOT NULL,
    symbol VARCHAR(50) NOT NULL UNIQUE
);

-- 6. investment
CREATE TABLE investment (
    investment_id SERIAL PRIMARY KEY,
    portfolio_id INT NOT NULL,
    crypto_id INT NOT NULL,
    amount NUMERIC(20,8) NOT NULL,
    purchase_price NUMERIC(20,8) NOT NULL,
    CONSTRAINT fk_investment_portfolio FOREIGN KEY (portfolio_id) REFERENCES portfolio(portfolio_id) ON DELETE CASCADE,
    CONSTRAINT fk_investment_crypto FOREIGN KEY (crypto_id) REFERENCES crypto(crypto_id) ON DELETE CASCADE
);

-- 7. cryptohistory
CREATE TABLE cryptohistory (
    history_id SERIAL PRIMARY KEY,
    crypto_id INT NOT NULL,
    date_changed DATE NOT NULL,
    price NUMERIC(20,8) NOT NULL,
    CONSTRAINT fk_cryptohistory_crypto FOREIGN KEY (crypto_id) REFERENCES crypto(crypto_id) ON DELETE CASCADE
);