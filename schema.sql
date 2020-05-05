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
    vl_works integer default 1 not null check(vl_works in (0, 1))
);

CREATE TABLE winpackages(
    cd_extp integer primary key autoincrement not null unique,
    nm_pack text not null unique,
    vl_link text not null unique,
    vl_works integer default 1 not null check(vl_works in (0, 1))
)