-- Migration: Replace 'passed' boolean with 'status' enum
-- Date: 2026-01-17
-- Ticket: GMA-23
-- Description: Change from boolean passed field to status enum (NOT_STARTED, IN_PROGRESS, PASSED)

-- Step 1: Add new status column
ALTER TABLE student_progress 
ADD COLUMN status VARCHAR(20) DEFAULT 'NOT_STARTED';

-- Step 2: Migrate existing data
UPDATE student_progress 
SET status = CASE 
    WHEN passed = true THEN 'PASSED'
    WHEN attempts > 0 THEN 'IN_PROGRESS'
    ELSE 'NOT_STARTED'
END;

-- Step 3: Drop old passed column
ALTER TABLE student_progress 
DROP COLUMN passed;

-- Step 4: Add constraint to ensure valid status values
ALTER TABLE student_progress 
ADD CONSTRAINT check_status CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'PASSED'));
