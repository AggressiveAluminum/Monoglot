-- the below should be valid SQL, but double-check please.
-- until we're done, I don't have any requirements to fix the DB schema, so
--   if we need to change this, it'll be okay.
-- lastly: we need *at least* all these fields.


CREATE TABLE IF NOT EXISTS entry (
  id INT PRIMARY KEY
  word VARCHAR NOT NULL,
  romanization VARCHAR,
  pronunciation VARCHAR,
  stem VARCHAR,
  type INT,
  category INT,
  created TIMESTAMP NOT NULL,
  modified TIMESTAMP
);

CREATE TABLE IF NOT EXISTS definition (
  id INT PRIMARY KEY,
  entry_id INT NOT NULL,
  previous_definition INT,
  next_definition INT,
  text TEXT,
  created TIMESTAMP NOT NULL,
  modified TIMESTAMP
);

CREATE TABLE IF NOT EXISTS types (
  -- stem/root/affix/etc
  -- should we consider making this a fixed set?
  id INT PRIMARY KEY,
  name VARCHAR NOT NULL,
  description VARCHAR
);

CREATE TABLE IF NOT EXISTS categories (
  -- part-of-speech.
  -- for noun classes/verb conjugation paradigms/etc, have the subcategory
  --   with a "pointer" to its parent
  -- it may be worth it to keep this as a tree in-memory?
  --  as strings for the UI: (examples)
  --    `noun` name: noun
  --    `noun:G1` or `noun:neuter`; name: G1 or neuter, NOT the UI string
  --    `noun:G1:from greek`; name: "from greek"
  id INT PRIMARY KEY,
  name VARCHAR NOT NULL,
  parent_category INT,
  description VARCHAR
);

CREATE TABLE IF NOT EXISTS tags (
  -- so people can say stuff like "TODO"
  id INT PRIMARY KEY,
  name VARCHAR NOT NULL,
  description VARCHAR
);

CREATE TABLE IF NOT EXISTS rel_tag_entry (
  -- yes, I'm pretty sure this is the best way to do it in SQL.
  -- variable arrays != performant, extra tables w/ relations == performant
  row_id INT PRIMARY KEY,
  tag_id INT,
  entry_id INT
);

CREATE TABLE IF NOT EXISTS rel_tag_definition (
  row_id INT PRIMARY KEY,
  tag_id INT,
  definition_id INT
);