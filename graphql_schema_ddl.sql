

create table if not exists graphql_schema (

  id integer primary key asc,
  type text,

  name text,

  tbl_name text,
  rootpage integer,

  ast text

);
