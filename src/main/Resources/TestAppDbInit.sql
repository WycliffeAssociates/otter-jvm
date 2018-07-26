DROP TABLE if EXISTS "USER PREFERENCES ENTITY";
DROP TABLE if EXISTS "USER LANGUAGES ENTITY";
DROP TABLE if EXISTS "USER ENTITY";
DROP TABLE if EXISTS "LANGUAGE ENTITY";

CREATE TABLE if NOT EXISTS "LANGUAGE ENTITY"
(
  id INTEGER primary key autoincrement,
  slug VARCHAR(8)  not null,
  name VARCHAR(50) not null,
  isGateway INT default 0 not null,
  anglicizedName VARCHAR(50) not null
);

CREATE TABLE if NOT EXISTS "USER ENTITY"
(
  id INTEGER primary key autoincrement,
  audioHash VARCHAR(50) not null,
  audioPath VARCHAR(50) not null
);

CREATE TABLE if NOT EXISTS "USER PREFERENCES ENTITY"
(
  userfk INTEGER PRIMARY KEY references "USER ENTITY" ,
  sourceLanguagefk INTEGER references "LANGUAGE ENTITY",
  targetLanguagefk INTEGER references "LANGUAGE ENTITY"
);

CREATE TABLE if NOT EXISTS "USER LANGUAGES ENTITY"
(
  userfk INTEGER references "USER ENTITY",
  sourceLanguagefk INTEGER references "LANGUAGE ENTITY",
  targetLanguagefk INTEGER references "LANGUAGE ENTITY",
  PRIMARY KEY (userfk, sourceLanguagefk, targetLanguagefk)
);

