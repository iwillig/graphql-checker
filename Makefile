EXAMPLE_DATABASE = example.db
SQL_SCHEMA = graphql_schema_ddl.sql

.DEFAULT_GOAL := check

$(EXAMPLE_DATABASE): $(SQL_SCHEMA)
	sqlite3 $(EXAMPLE_DATABASE) < $(SQL_SCHEMA)

.PHONY: rebel
rebel:
	clojure -M:rebel

.PHONY: outdated
outdated:
	clojure -M:outdated

.PHONY: lint
lint:
	clojure -M:lint --lint src/ test/ dev/

.PHONY: repl
repl:
	clj -M:tests:nREPL -m nrepl.cmdline

.PHONY: test
test:
	clojure -M:tests

.PHONY: clean
clean:
	-rm junit.xml
	-rm -r target
	-rm $(EXAMPLE_DATABASE)


.PHONY: check
check: outdated lint test
