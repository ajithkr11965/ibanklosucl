-- Migration script to add SAL_GROSS_AMOUNT column to VEHICLE_LOAN_PROGRAM_SALARY table
-- This is required to support gross salary field in addition to net salary

-- Add SAL_GROSS_AMOUNT column
ALTER TABLE VEHICLE_LOAN_PROGRAM_SALARY ADD SAL_GROSS_AMOUNT NUMBER(15,2);

-- Add comment for documentation
COMMENT ON COLUMN VEHICLE_LOAN_PROGRAM_SALARY.SAL_GROSS_AMOUNT IS 'Gross salary amount from the payslip (before deductions)';

-- Note: Existing records will have NULL values for this new column
-- This is acceptable as gross salary is optional and may not be available for all payslips
