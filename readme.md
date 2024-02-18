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

At work we have adopted a workflow where both our backend and frontend
team worked directly with the GraphQL Schema. All our documentation
and types for our API are defined in a single GraphQL Schema
file. Both the backend and the frontend read that file and it’s a
single source of truth for the entire engineering organization.

Using this GraphQL Schema this has introduced a collection of
tradeoffs at our organization. On one hand, everyone understands and
can works directly in the GraphQL Type System. We write our backend
tests also in the GraphQL query language. Sharing this has been very
helpful as we can give the frontend team working GraphQL queries that
we know are tested.

But there have been negative tradeoffs. Our GraphQL Schema file is
huge. If you include detailed documentation inline in the GraphQL
Schema, you lose sight of the GraphQL types themselves.

During our process of building a new GraphQL API, our team adopted the
Relay pattern for designing our GraphQL
Schema. [Relay](https://relay.dev/docs/guides/graphql-server-specification/)
has a couple of useful patterns for Lists of things (Connections),
global unique ids (global ids) and the node API. We want the ability
to enforce or lint our GraphQL schema to make sure all lists are
“connection” edges, and every object should implement the Node
interface.

This tool is an attempt to build a GraphQL Tool chain that parses a
GraphQL schema file and GraphQL operation files (queries and
mutations). This parsed information is then loaded into a DataScript
Database. DataScript is a Clojure implementation of a datalog
database. It is based on datomic, authored by Rich Hickey and team.

This tool chain also uses the language-tools open source library. This
gives us the ability to check for spelling and grammar mistakes in
your GraphqQL files in several languages. Spelling and grammatical
errors are added to this database, along with the original source
character positions.

Then users can run [datalog
queries](https://github.com/tonsky/datascript/blob/master/docs/queries.md)
to ask questions about this GraphQL API. For example, are there any
spelling mistakes in my GraphQL Schema file? Are all lists associated
with a connection object? Is there an object that does not implement
the “Node” interface.

In other words, by using the datalog engine in datascript, we can
explore our GraphQL schema system and track changes to it.

## TODO Usage: Init DB

```shell
gqlc --init-db GraphQL_Schema.graphql
```

This creates a new database file called `GraphQL_Schema_gqlc-db.edn`

## TODO Usage: Update-db

```shell
## Will compare to GraphQL_Schema_ggql-db.edn
gqlc --update-db GraphQL_Schema.graphql
```

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
dev=>(k/run 'graphql-checker.main-test) ;; Run main test
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
