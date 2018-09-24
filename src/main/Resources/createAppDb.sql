CREATE TABLE IF NOT EXISTS language_entity (
  id            INTEGER PRIMARY KEY AUTOINCREMENT,
  slug          VARCHAR(8)  NOT NULL UNIQUE,
  name          VARCHAR(50) NOT NULL,
  gateway       INTEGER DEFAULT 0 NOT NULL,
  anglicized    VARCHAR(50) NOT NULL,
  direction     VARCHAR(3) NOT NULL
);

CREATE TABLE IF NOT EXISTS dublin_core_entity (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    conformsTo  VARCHAR(30) NOT NULL,
    creator     VARCHAR(30) NOT NULL,
    description VARCHAR(150) NOT NULL,
    format      VARCHAR(20) NOT NULL,
    identifier  VARCHAR(10) NOT NULL,
    issued      VARCHAR(30) NOT NULL,
    language_fk INTEGER NOT NULL,
    modified    VARCHAR(30) NOT NULL,
    publisher   VARCHAR(50) NOT NULL,
    subject     VARCHAR(50) NOT NULL,
    type        VARCHAR(50) NOT NULL,
    title       VARCHAR(50) NOT NULL,
    version     INTEGER NOT NULL,
    path        VARCHAR(80) NOT NULL,
    FOREIGN KEY (language_fk) REFERENCES language_entity(id)
);

CREATE TABLE IF NOT EXISTS rc_link_entity (
    rc1_fk      INTEGER NOT NULL,
    rc2_fk      INTEGER NOT NULL,
    PRIMARY KEY (rc1_fk, rc2_fk),
    FOREIGN KEY (rc1_fk) REFERENCES dublin_core_entity(id) ON DELETE CASCADE,
    FOREIGN KEY (rc2_fk) REFERENCES dublin_core_entity(id) ON DELETE CASCADE,
    CONSTRAINT directionless CHECK (rc1_fk < rc2_fk)
);

CREATE TABLE IF NOT EXISTS collection_entity (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    parent_fk     INTEGER,
    source_fk     INTEGER,
    label         VARCHAR(50) NOT NULL,
    title         VARCHAR(50) NOT NULL,
    slug          VARCHAR(20) NOT NULL,
    sort          INTEGER NOT NULL,
    rc_fk         INTEGER NOT NULL,
    FOREIGN KEY (parent_fk) REFERENCES collection_entity(id),
    FOREIGN KEY (source_fk) REFERENCES collection_entity(id),
    FOREIGN KEY (rc_fk) REFERENCES dublin_core_entity(id)
);

CREATE TABLE IF NOT EXISTS content_entity (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    collection_fk    INTEGER NOT NULL,
    label            VARCHAR(50) NOT NULL,
    selected_take_fk INTEGER,
    start            INTEGER NOT NULL,
    sort             INTEGER NOT NULL,
    FOREIGN KEY (collection_fk) REFERENCES collection_entity(id) ON DELETE CASCADE,
    FOREIGN KEY (selected_take_fk) REFERENCES take_entity(id)
);

CREATE TABLE IF NOT EXISTS content_derivative (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    content_fk       INTEGER NOT NULL,
    source_fk        INTEGER NOT NULL,
    FOREIGN KEY (content_fk) REFERENCES content_entity(id),
    FOREIGN KEY (source_fk) REFERENCES content_entity(id)
);

CREATE TABLE IF NOT EXISTS take_entity (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    content_fk       INTEGER NOT NULL,
    filename         VARCHAR(50) NOT NULL,
    path             VARCHAR(80) NOT NULL,
    number           INTEGER NOT NULL,
    timestamp        VARCHAR(30) NOT NULL,
    unheard          INTEGER DEFAULT 0 NOT NULL,
    FOREIGN KEY (content_fk) REFERENCES content_entity(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS marker_entity (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    take_fk          INTEGER NOT NULL,
    number           INTEGER NOT NULL,
    position         INTEGER NOT NULL,
    label            VARCHAR(50) NOT NULL,
    FOREIGN KEY (take_fk) REFERENCES take_entity(id)
);

CREATE TABLE IF NOT EXISTS resource_link (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    resource_content_fk INTEGER NOT NULL,
    content_fk          INTEGER,
    collection_fk       INTEGER,
    FOREIGN KEY (resource_content_fk) REFERENCES content_entity(id),
    FOREIGN KEY (content_fk) REFERENCES content_entity(id),
    FOREIGN KEY (collection_fk) REFERENCES collection_entity(id),
    CONSTRAINT ensure_at_least_one_not_null CHECK ((collection_fk is NOT NULL) or (content_fk is NOT NULL)),
    CONSTRAINT prevent_both_not_null CHECK ((collection_fk is NULL) or (content_fk is NULL)),
    UNIQUE (resource_content_fk, content_fk, collection_fk)
);

CREATE TABLE IF NOT EXISTS audio_plugin_entity (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    name                VARCHAR(50) NOT NULL,
    version             VARCHAR(20) NOT NULL,
    bin                 VARCHAR(80) NOT NULL,
    args                VARCHAR(50) NOT NULL,
    record              INTEGER DEFAULT 0 NOT NULL,
    edit                INTEGER DEFAULT 0 NOT NULL
);