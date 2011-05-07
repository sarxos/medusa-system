
--
-- add desired and quantity fields to the trader (handled 
-- via paper instance)
--

ALTER TABLE trader ADD COLUMN (
	quantity INT NOT NULL DEFAULT 0,
	desired INT NOT NULL DEFAULT 0
);

