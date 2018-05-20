-- CUSTOMERS TABLE CREATION
DROP TABLE IF EXISTS CUSTOMERS;
CREATE TABLE CUSTOMERS (
   ID INT NOT NULL,
   NAME VARCHAR(50),
   ADDRESS VARCHAR(500),
   CREATED_DT datetime,
   LAST_UPDATED_DT datetime
);
ALTER TABLE CUSTOMERS ADD CONSTRAINT CUSTOMERS_PK PRIMARY KEY(ID);

-- ACCOUNTS TABLE CREATION
DROP TABLE IF EXISTS ACCOUNTS;
CREATE TABLE ACCOUNTS (
   ID INT NOT NULL,
   CUSTOMER_ID INT NOT NULL,
   ACCOUNT_NUMBER NUMBER(11) NOT NULL,
   BALANCE DECIMAL(10,2) NOT NULL,
   CURRENCY_CODE VARCHAR(3) NOT NULL,
   CREATED_DT datetime,
   LAST_UPDATED_DT datetime,
   FOREIGN KEY(CUSTOMER_ID) REFERENCES CUSTOMERS(ID)
);
ALTER TABLE ACCOUNTS ADD CONSTRAINT ACCOUNT_PK PRIMARY KEY(ID);

-- TRANSFERS TABLE CREATION
DROP TABLE IF EXISTS TRANSFERS;
CREATE TABLE TRANSFERS (
   ID INT NOT NULL,
   FROM_ACCOUNT_NO NUMBER(15) NOT NULL,
   TO_ACCOUNT_NO NUMBER(15) NOT NULL,
   DEBITED_AMOUNT DECIMAL(10,2),
   DEBITED_CURRENCY_CODE VARCHAR(3),
   TRANSFER_AMOUNT DECIMAL(10,2) NOT NULL,
   TRANSFER_CURRENCY_CODE VARCHAR(3) NOT NULL,
   CREDITED_AMOUNT DECIMAL(10,2),
   CREDITED_CURRENCY_CODE VARCHAR(3),
   RATE DECIMAL(10,4),
   STATUS VARCHAR(50) NOT NULL,
   CREATED_DT datetime,
   LAST_UPDATED_DT datetime
);
ALTER TABLE TRANSFERS ADD CONSTRAINT TRANSFER_PK PRIMARY KEY(ID);

DROP TABLE IF EXISTS RATES;
CREATE TABLE RATES(
    ID INT NOT NULL,
    SOURCE_CURRENCY_CODE VARCHAR(3) NOT NULL,
    DESTINATION_CURRENCY_CODE VARCHAR(3) NOT NULL,
    RATE DECIMAL(10,4) NOT NULL,
    EFFECTIVE_DT datetime,
    CREATED_DT datetime,
    LAST_UPDATED_DT datetime
);
ALTER TABLE RATES ADD CONSTRAINT RATE_PK PRIMARY KEY(ID);

-- CREATE SEQUENCES
DROP SEQUENCE IF EXISTS TRANSFERS_SEQ;
CREATE SEQUENCE TRANSFERS_SEQ
    START WITH 1
    INCREMENT BY 1;

DROP SEQUENCE IF EXISTS ACCOUNTS_SEQ;
CREATE SEQUENCE ACCOUNTS_SEQ
    START WITH 1
    INCREMENT BY 1;


DROP SEQUENCE IF EXISTS CUSTOMERS_SEQ;
CREATE SEQUENCE CUSTOMERS_SEQ
    START WITH 1
    INCREMENT BY 1;

DROP SEQUENCE IF EXISTS RATES_SEQ;
CREATE SEQUENCE RATES_SEQ
    START WITH 1
    INCREMENT BY 1;

COMMIT;

-- CUSTOMERS TABLE DATA POPULATION
INSERT INTO CUSTOMERS values (CUSTOMERS_SEQ.nextVal, 'Fortun Allaire', '68, place Maurice-Charretier 08000 CHARLEVILLE-MEZIERES', SYSDATE, null);
INSERT INTO CUSTOMERS values (CUSTOMERS_SEQ.nextVal, 'Nadia R. Odell', '2807 Lakewood Drive Englewood, NJ 07631', SYSDATE, null);
INSERT INTO CUSTOMERS values (CUSTOMERS_SEQ.nextVal, 'Melusina Tan', '71 Wilkie Road 228071 Singapore', SYSDATE, null);
INSERT INTO CUSTOMERS values (CUSTOMERS_SEQ.nextVal, 'Ella Raiwala', '91 Glen William Road GERMANTOWN QLD 4871', SYSDATE, null);
INSERT INTO CUSTOMERS values (CUSTOMERS_SEQ.nextVal, 'Jeanne Deblois', '84, Cours Marechal-Joffre 76200 DIEPPE', SYSDATE, null);
INSERT INTO CUSTOMERS values (CUSTOMERS_SEQ.nextVal, 'Gioacchino Nucci', 'Gartenhof 117 1321 Arnex-sur-Orbe', SYSDATE, null);

-- ACCOUNTS TABLE DATA POPULATION
INSERT INTO ACCOUNTS values (ACCOUNTS_SEQ.nextVal, 1, 12345678901, 500.57, 'EUR', SYSDATE, null);
INSERT INTO ACCOUNTS values (ACCOUNTS_SEQ.nextVal, 2, 23456789012, 909.40, 'USD', SYSDATE, null);
INSERT INTO ACCOUNTS values (ACCOUNTS_SEQ.nextVal, 3, 34567890123, 767.26, 'SGD', SYSDATE, null);
INSERT INTO ACCOUNTS values (ACCOUNTS_SEQ.nextVal, 4, 45678901234, 1493.60, 'AUD', SYSDATE, null);
INSERT INTO ACCOUNTS values (ACCOUNTS_SEQ.nextVal, 5, 56789012345, 1171.06, 'EUR', SYSDATE, null);
INSERT INTO ACCOUNTS values (ACCOUNTS_SEQ.nextVal, 6, 67890123456, 902.99, 'CHF', SYSDATE, null);
INSERT INTO ACCOUNTS values (ACCOUNTS_SEQ.nextVal, 1, 78901234567, 1226.15, 'EUR', SYSDATE, null);
INSERT INTO ACCOUNTS values (ACCOUNTS_SEQ.nextVal, 3, 89012345678, 289.88, 'SGD', SYSDATE, null);
INSERT INTO ACCOUNTS values (ACCOUNTS_SEQ.nextVal, 5, 90123456789, 1673.94, 'GBP', SYSDATE, null);
INSERT INTO ACCOUNTS values (ACCOUNTS_SEQ.nextVal, 1, 01234567890, 1351.12, 'EUR', SYSDATE, null);

-- INSERT FOR UNIT TESTING ONLY - INVALID ACCOUNTS
INSERT INTO ACCOUNTS values (ACCOUNTS_SEQ.nextVal, 3, 900, 289.88, 'aaa', SYSDATE, null);
INSERT INTO ACCOUNTS values (ACCOUNTS_SEQ.nextVal, 5, 800, 1673.94, 'CUC', SYSDATE, null);


-- TRANSFERS RECORD FOR TESTING
INSERT INTO TRANSFERS values (TRANSFERS_SEQ.nextVal, 12345678901, 56789012345, 123.12, 'EUR', 123.12,'EUR', 123.12, 'EUR', 1, 'PROCESSED', TO_DATE('20180510 102359','YYYYMMDD HH24MISS'), TO_DATE('20180510 102523','YYYYMMDD HH24MISS'));

-- RATES TABLE DATA POPULATION
INSERT INTO RATES values (RATES_SEQ.nextVal, 'EUR', 'SGD',1.58 , SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'SGD', 'EUR', 0.63, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'EUR', 'USD', 1.18, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'USD', 'EUR', 0.85, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'AUD', 'SGD',1.01 , SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'SGD', 'AUD', 0.99, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'AUD', 'USD', 0.75, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'USD', 'AUD',1.33 , SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'AUD', 'EUR', 0.64, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'EUR', 'AUD', 1.57, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'CHF', 'EUR', 0.85, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'EUR', 'CHF', 1.18, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'CHF', 'SGD',1.34 , SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'SGD', 'CHF',0.74 , SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'AUD', 'CHF', 0.75, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'CHF', 'AUD',1.33 , SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'CHF', 'USD',1.00 , SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'USD', 'CHF', 1.00, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'GBP', 'SGD', 1.81, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'GBP', 'EUR',1.14 , SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'GBP', 'USD', 1.35, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'GBP', 'AUD', 1.79, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'GBP', 'CHF', 1.34, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'SGD', 'GBP', 0.55, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'EUR', 'GBP',0.88 , SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'USD', 'GBP', 0.74, SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'AUD', 'GBP',0.56 , SYSDATE, SYSDATE, null);
INSERT INTO RATES values (RATES_SEQ.nextVal, 'CHF', 'GBP', 0.74, SYSDATE, SYSDATE, null);
COMMIT;