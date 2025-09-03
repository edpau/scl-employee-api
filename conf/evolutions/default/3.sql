# --- !Ups
ALTER TABLE employees
  DROP INDEX email,   -- drop the auto-generated unique constraint name on email
  ADD CONSTRAINT uq_employees_email UNIQUE (email);

# --- !Downs
ALTER TABLE employees
  DROP INDEX uq_employees_email,
  ADD CONSTRAINT email UNIQUE (email);