DELIMITER $
CREATE TRIGGER unigueRefereesInArbitration
BEFORE INSERT ON arbitration
FOR EACH ROW
BEGIN

	IF (NEW.referee = NEW.ref1 OR
		NEW.referee = NEW.ref2 OR
		NEW.referee = NEW.fourth OR
		NEW.referee = NEW.observer OR
		NEW.ref1 = NEW.ref2 OR
		NEW.ref1 = NEW.fourth OR
		NEW.ref1 = NEW.observer OR
		NEW.ref2 = NEW.fourth OR
		NEW.ref2 = NEW.observer OR
		NEW.fourth = NEW.observer) THEN

		SIGNAL SQLSTATE VALUE '45000'
		SET MESSAGE_TEXT = 'EACH REFEREE IN ARBITRATION MUST BE UNIQUE'; 
	END IF;

END$
DELIMITER ;