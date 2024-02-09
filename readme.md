# graphql-checker (gqlc)

GraphQL Checker is a tool chain for maintaining large and complex
GraphQL projects.

GraphQL checker parses your schema file and builds a knowledge base
(SQLite) derived from your schema file.

## Development

This tool is written in Clojure. If you use emacs, your normal
`cider-jack-in` should work.

For an editor independent experience, you can start the rebel repl
with

```shell
$ make rebel
clojure -M:rebel
[Rebel readline] Type :repl/help for online help info
user=> (dev)
:reloading (graphql-checker.main graphql-checker.main-test dev user)
:ok
dev=>
```

## Parse-Schema [TODO]

```shell
gqlc parse-schema --help
gqlc parse-schema --help
gqlc parse-schema --file graphql-schema.graphql

gqlc print-schema --graphql-schema graphql-schema.graphql
```


## Fake GraphQL [TODO]

Given a GraphQL

## Install

```shell
make install
```

## Test

```shell
make check
```
