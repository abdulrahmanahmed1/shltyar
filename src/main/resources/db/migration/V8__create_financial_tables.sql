-- Migration: Create financial tables
-- Tables: financial_transactions, driver_earnings, sales_commissions, company_commissions
-- Requirements: 11.1, 11.2, 11.3, 20.1, 20.2, 21.1, 53.1

-- Table: financial_transactions
-- Master table for all financial transactions with complete audit trail
-- Tracks all monetary movements including driver earnings, sales commissions, company commissions, and subscription payments
CREATE TABLE financial_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_type VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    order_id BIGINT NULL,
    subscription_id BIGINT NULL,
    user_id BIGINT NULL,
    restaurant_id BIGINT NULL,
    description TEXT NULL,
    reference_number VARCHAR(100) UNIQUE NOT NULL,
    status VARCHAR(20) DEFAULT 'COMPLETED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_financial_transactions_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_financial_transactions_subscription FOREIGN KEY (subscription_id) REFERENCES subscriptions(id),
    CONSTRAINT fk_financial_transactions_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_financial_transactions_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
    CONSTRAINT chk_transaction_type CHECK (transaction_type IN ('DRIVER_EARNING', 'SALES_COMMISSION', 'COMPANY_COMMISSION', 'SUBSCRIPTION_PAYMENT')),
    CONSTRAINT chk_transaction_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REVERSED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_financial_transactions_type ON financial_transactions(transaction_type);
CREATE INDEX idx_financial_transactions_order_id ON financial_transactions(order_id);
CREATE INDEX idx_financial_transactions_user_id ON financial_transactions(user_id);
CREATE INDEX idx_financial_transactions_restaurant_id ON financial_transactions(restaurant_id);
CREATE INDEX idx_financial_transactions_created_at ON financial_transactions(created_at);
CREATE INDEX idx_financial_transactions_reference ON financial_transactions(reference_number);
CREATE INDEX idx_financial_transactions_status ON financial_transactions(status);

-- Table: driver_earnings
-- Records driver earnings from completed deliveries with commission breakdown
-- Links to financial_transactions for complete audit trail
CREATE TABLE driver_earnings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    driver_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    transaction_id BIGINT NOT NULL,
    delivery_price DECIMAL(10, 2) NOT NULL,
    company_commission DECIMAL(10, 2) NOT NULL,
    driver_earning DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_driver_earnings_driver FOREIGN KEY (driver_id) REFERENCES users(id),
    CONSTRAINT fk_driver_earnings_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_driver_earnings_transaction FOREIGN KEY (transaction_id) REFERENCES financial_transactions(id),
    CONSTRAINT chk_delivery_price_positive CHECK (delivery_price >= 0),
    CONSTRAINT chk_company_commission_positive CHECK (company_commission >= 0),
    CONSTRAINT chk_driver_earning_positive CHECK (driver_earning >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_driver_earnings_driver_id ON driver_earnings(driver_id);
CREATE INDEX idx_driver_earnings_order_id ON driver_earnings(order_id);
CREATE INDEX idx_driver_earnings_transaction_id ON driver_earnings(transaction_id);
CREATE INDEX idx_driver_earnings_created_at ON driver_earnings(created_at);

-- Table: sales_commissions
-- Records sales representative commissions from restaurant subscriptions
-- Differentiates between new restaurant and renewal commissions
CREATE TABLE sales_commissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sales_rep_id BIGINT NOT NULL,
    restaurant_id BIGINT NOT NULL,
    subscription_id BIGINT NOT NULL,
    transaction_id BIGINT NOT NULL,
    subscription_amount DECIMAL(10, 2) NOT NULL,
    commission_percentage DECIMAL(5, 2) NOT NULL,
    commission_amount DECIMAL(10, 2) NOT NULL,
    commission_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sales_commissions_sales_rep FOREIGN KEY (sales_rep_id) REFERENCES users(id),
    CONSTRAINT fk_sales_commissions_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
    CONSTRAINT fk_sales_commissions_subscription FOREIGN KEY (subscription_id) REFERENCES subscriptions(id),
    CONSTRAINT fk_sales_commissions_transaction FOREIGN KEY (transaction_id) REFERENCES financial_transactions(id),
    CONSTRAINT chk_commission_type CHECK (commission_type IN ('NEW_RESTAURANT', 'RENEWAL')),
    CONSTRAINT chk_subscription_amount_positive CHECK (subscription_amount >= 0),
    CONSTRAINT chk_commission_percentage_valid CHECK (commission_percentage >= 0 AND commission_percentage <= 100),
    CONSTRAINT chk_commission_amount_positive CHECK (commission_amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_sales_commissions_sales_rep_id ON sales_commissions(sales_rep_id);
CREATE INDEX idx_sales_commissions_restaurant_id ON sales_commissions(restaurant_id);
CREATE INDEX idx_sales_commissions_subscription_id ON sales_commissions(subscription_id);
CREATE INDEX idx_sales_commissions_transaction_id ON sales_commissions(transaction_id);
CREATE INDEX idx_sales_commissions_created_at ON sales_commissions(created_at);
CREATE INDEX idx_sales_commissions_commission_type ON sales_commissions(commission_type);

-- Table: company_commissions
-- Records company commission from each delivery order
-- Supports both fixed per-order and percentage-based commission models
CREATE TABLE company_commissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    transaction_id BIGINT NOT NULL,
    delivery_price DECIMAL(10, 2) NOT NULL,
    commission_type VARCHAR(20) NOT NULL,
    commission_value DECIMAL(10, 2) NOT NULL,
    commission_amount DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_company_commissions_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_company_commissions_transaction FOREIGN KEY (transaction_id) REFERENCES financial_transactions(id),
    CONSTRAINT chk_company_commission_type CHECK (commission_type IN ('FIXED_PER_ORDER', 'PERCENTAGE')),
    CONSTRAINT chk_delivery_price_positive_cc CHECK (delivery_price >= 0),
    CONSTRAINT chk_commission_value_positive CHECK (commission_value >= 0),
    CONSTRAINT chk_commission_amount_positive_cc CHECK (commission_amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_company_commissions_order_id ON company_commissions(order_id);
CREATE INDEX idx_company_commissions_transaction_id ON company_commissions(transaction_id);
CREATE INDEX idx_company_commissions_created_at ON company_commissions(created_at);
CREATE INDEX idx_company_commissions_commission_type ON company_commissions(commission_type);
