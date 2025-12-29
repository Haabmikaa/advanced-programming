-- Database Configuration and User Setup

-- Create database user for the application
CREATE USER IF NOT EXISTS 'quizapp_user'@'localhost' IDENTIFIED BY 'quizapp_password123';
GRANT ALL PRIVILEGES ON quizdb.* TO 'quizapp_user'@'localhost';
FLUSH PRIVILEGES;

-- Show grants for the user
SHOW GRANTS FOR 'quizapp_user'@'localhost';

-- Database Configuration Recommendations
/*
1. Set character set to UTF-8
2. Set timezone to UTC
3. Enable strict mode
4. Set appropriate isolation level
*/

SET GLOBAL character_set_server = 'utf8mb4';
SET GLOBAL collation_server = 'utf8mb4_unicode_ci';
SET GLOBAL time_zone = '+00:00';
SET GLOBAL sql_mode = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';
SET GLOBAL transaction_isolation = 'READ-COMMITTED';

-- Show current configuration
SHOW VARIABLES LIKE 'character_set%';
SHOW VARIABLES LIKE 'collation%';
SHOW VARIABLES LIKE 'time_zone';
SHOW VARIABLES LIKE 'sql_mode';
SHOW VARIABLES LIKE 'transaction_isolation';

-- Create backup user (for admin purposes)
CREATE USER IF NOT EXISTS 'quizapp_backup'@'localhost' IDENTIFIED BY 'backup_password123';
GRANT SELECT, LOCK TABLES, SHOW VIEW ON quizdb.* TO 'quizapp_backup'@'localhost';

-- Create read-only user (for reports)
CREATE USER IF NOT EXISTS 'quizapp_report'@'localhost' IDENTIFIED BY 'report_password123';
GRANT SELECT ON quizdb.* TO 'quizapp_report'@'localhost';

-- List all users
SELECT user, host FROM mysql.user WHERE user LIKE 'quizapp%';