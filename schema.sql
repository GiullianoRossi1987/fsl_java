-- SQLite3

CREATE TABLE packages(
    cd_pack integer primary key autoincrement not null unique,
    nm_pack text not null unique,
    vl_shell text not null
);

CREATE TABLE extpackages(
    cd_extp integer primary key autoincrement not null unique,
    nm_pack text not null unique,
    vl_link text not null unique,
);

CREATE TABLE winpackages(
    cd_extp integer primary key autoincrement not null unique,
    nm_pack text not null unique,
    vl_link text not null unique,
)