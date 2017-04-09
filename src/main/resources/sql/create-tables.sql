CREATE TABLE IF NOT EXISTS types (
    -- stem/root/affix/etc
    id INT8 PRIMARY KEY,
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
    id INT8 PRIMARY KEY,
    name VARCHAR NOT NULL,
    full_name VARCHAR NOT NULL,
    parent_category INT8,
    description VARCHAR NOT NULL,
    FOREIGN KEY(parent_category) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS entry (
    id INT8 PRIMARY KEY,
    word VARCHAR NOT NULL,
    romanization VARCHAR NOT NULL,
    pronunciation VARCHAR NOT NULL,
    stem VARCHAR NOT NULL,
    type INT8,
    category INT8,
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP,
    FOREIGN KEY(type) REFERENCES types(id) ON DELETE SET NULL,
    FOREIGN KEY(category) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS definition (
    id INT8 PRIMARY KEY,
    entry_id INT8,
    def_order INT NOT NULL,
    text VARCHAR NOT NULL,
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP,
    FOREIGN KEY(entry_id) REFERENCES entry(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tags (
    -- so people can say stuff like "TODO"
    id INT8 PRIMARY KEY,
    name VARCHAR NOT NULL,
    description VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS rel_tag_entry (
    row_id INT8 PRIMARY KEY,
    tag_id INT8 NOT NULL,
    entry_id INT8 NOT NULL,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
    FOREIGN KEY (entry_id) REFERENCES entry(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rel_tag_definition (
    row_id INT8 PRIMARY KEY,
    tag_id INT8 NOT NULL,
    definition_id INT8 NOT NULL,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
    FOREIGN KEY (definition_id) REFERENCES definition(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS examples (
    id INT8 PRIMARY KEY,
    text VARCHAR NOT NULL,
    gloss VARCHAR NOT NULL,
    pronunciation VARCHAR NOT NULL,
    literal_translation VARCHAR NOT NULL,
    free_translation VARCHAR NOT NULL,
    explanation VARCHAR NOT NULL
);
