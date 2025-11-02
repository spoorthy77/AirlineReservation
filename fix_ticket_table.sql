-- Add username column to ticket table
ALTER TABLE ticket ADD COLUMN username VARCHAR(100) AFTER pnr;

-- Create index on username for fast filtering
CREATE INDEX idx_ticket_username ON ticket(username);

-- Verify the column was added
SHOW COLUMNS FROM ticket;
