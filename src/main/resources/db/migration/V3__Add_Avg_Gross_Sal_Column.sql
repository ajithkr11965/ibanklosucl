-- Migration script to add AVG_GROSS_SAL column to VEHICLE_LOAN_PROGRAM table
-- This is required to store average gross monthly income from payslips

-- Add AVG_GROSS_SAL column
ALTER TABLE VEHICLE_LOAN_PROGRAM ADD AVG_GROSS_SAL NUMBER(15,2);

-- Add comment for documentation
COMMENT ON COLUMN VEHICLE_LOAN_PROGRAM.AVG_GROSS_SAL IS 'Average gross monthly income calculated from payslip gross salaries';

-- Note: Existing records will have NULL values for this new column
-- This is acceptable as average gross salary is optional and may not be available for existing payslips
