# graphql-checker (gqlc)

> **_NOTE:_** This tool is still under development. Some of the code
> or documentation is a work in progress. PR's welcome

A tool for validating and linting GraphQL Schema and operations.

## Overview

GraphQL Checker is a tool chain for maintaining large and complex
GraphQL projects.

GraphQL checker parses your schema file and builds a knowledge base
(Datalog) derived from your schema file.


[A longer description of the project, optionally with sub-sections like
'Features', 'History', 'Motivation', etc.]

## Motivation


## TODO Usage

```shell
gqlc --init-db GraphQL_Schema.graphql
```
This creates a new database file called `GraphQL_Schema_gqlc-db.edn`

## TODO Usage: Query

Example query that uses the [language-tool](https://dev.languagetool.org/java-api.html) library to find all of the
spelling mistakes in a type description. Return the spelling mistake
and its position.

```shell
gqlc --db GraphQL_Schema_gqlc-db.edn --query-file query-graphql-schema.datalog
```
Where `query-graphql-schema.datalog` is

```clojure
[:find  ?spelling-error ?error-position
 :keys  spelling-error error-position
 :where [?description :gql-type-description/value _]             ;; Find all of the type descriptions in your graphql schema
        [?description :gql-type-description/errors ?error]       ;; Find all of the errors associated with a description
        [?error       :gql-error/type :error.type/spelling]      ;; Filter all of the error to the spelling
        [?error       :gql-error/position ?error-position]       ;; Find the original character position of the spelling error
]
```

```clojure
[{:spelling-error "" :error-position {}] {:spelling-error "" :error-position {}}]
```

## TODO Usage: Configuration

```clojure
{:gqlc/db-file "GraphQL_Schema_gqlc-db.edn"
 :gqlc/gql-schema-file "GraphQL_Schema.graphql"}
```

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

## TODO Parse-Schema

```shell
gqlc --parse-schema --help
gqlc --parse-schema --help
gqlc --parse-schema --file graphql-schema.graphql

gqlc --print-schema --graphql-schema graphql-schema.graphql
```

## TODO Install

```shell
make install
```

## DONE Test

```shell
make check
```
