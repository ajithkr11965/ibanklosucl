-- Migration script to add SAL_YEAR and SAL_AMOUNT columns to VEHICLE_LOAN_PROGRAM_SALARY table
-- This is required to support the last 3 months payslip validation feature

-- Add SAL_YEAR column
ALTER TABLE VEHICLE_LOAN_PROGRAM_SALARY ADD SAL_YEAR NUMBER(4);

-- Add SAL_AMOUNT column
ALTER TABLE VEHICLE_LOAN_PROGRAM_SALARY ADD SAL_AMOUNT NUMBER(15,2);

-- Add comments for documentation
COMMENT ON COLUMN VEHICLE_LOAN_PROGRAM_SALARY.SAL_YEAR IS 'Year of the payslip (e.g., 2024, 2025)';
COMMENT ON COLUMN VEHICLE_LOAN_PROGRAM_SALARY.SAL_AMOUNT IS 'Salary amount from the payslip';

-- Note: Existing records will have NULL values for these new columns
-- This is acceptable as they represent historical data before this feature was implemented
