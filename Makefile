.DEFAULT_GOAL := check

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


.PHONY: check
check: outdated lint test
