-- the below should be valid SQL, but double-check please.
-- until we're done, I don't have any requirements to fix the DB schema, so
--   if we need to change this, it'll be okay.
-- lastly: we need *at least* all these fields.


CREATE TABLE IF NOT EXISTS entry (
  id UUID PRIMARY KEY,
  word VARCHAR NOT NULL,
  romanization VARCHAR NOT NULL,
  pronunciation VARCHAR NOT NULL,
  stem VARCHAR NOT NULL,
  type UUID,
  category UUID,
  created TIMESTAMP NOT NULL,
  modified TIMESTAMP
);

CREATE TABLE IF NOT EXISTS definition (
  id UUID PRIMARY KEY,
  entry_id UUID,
  def_order INT NOT NULL,
  text VARCHAR NOT NULL,
  created TIMESTAMP NOT NULL,
  modified TIMESTAMP,
  FOREIGN KEY(entry_id) REFERENCES entry(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS types (
  -- stem/root/affix/etc
  -- should we consider making this a fixed set?
  id UUID PRIMARY KEY,
  name VARCHAR NOT NULL,
  description VARCHAR NOT NULL
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
  id UUID PRIMARY KEY,
  name VARCHAR NOT NULL,
  full_name VARCHAR NOT NULL,
  parent_category UUID,
  description VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS tags (
  -- so people can say stuff like "TODO"
  id UUID PRIMARY KEY,
  name VARCHAR NOT NULL,
  description VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS rel_tag_entry (
  -- yes, I'm pretty sure this is the best way to do it in SQL.
  -- variable arrays != performant, extra tables w/ relations == performant
  row_id UUID PRIMARY KEY,
  tag_id UUID NOT NULL,
  entry_id UUID NOT NULL
);

CREATE TABLE IF NOT EXISTS rel_tag_definition (
  row_id UUID PRIMARY KEY,
  tag_id UUID NOT NULL,
  definition_id UUID NOT NULL
);

CREATE TABLE IF NOT EXISTS examples (
id UUID PRIMARY KEY,
text VARCHAR NOT NULL,
gloss VARCHAR NOT NULL,
pronunciation VARCHAR NOT NULL,
literal_translation VARCHAR NOT NULL,
free_translation VARCHAR NOT NULL,
explanation VARCHAR NOT NULL
);