# clj-sudachi

A Clojure library for idiomatic access to the [Sudachi Japanese morphological analyzer](https://github.com/WorksApplications/Sudachi).

[![Clojars Project](https://img.shields.io/clojars/v/clj-sudachi.svg)](https://clojars.org/borh/clj-sudachi)
[![License](https://img.shields.io/badge/License-EPL%201.0-red.svg)](https://opensource.org/licenses/EPL-1.0)

## Status

This is an early release and most of the API is still subject to change as a result of real-world usage. In particular, some of the feature field names might change in future versions. While only minimal testing on real data has been conducted, future versions will be tested on large text corpora.

## Requirements

You need to first install a Sudachi dictionary from the [SudachiDict project](https://github.com/WorksApplications/SudachiDict) and have it on your path before proceeding.

Dependency information:

```clojure
[borh.clj-sudachi "0.2.0"]
```

This library was developed and tested using Clojure 1.10 and includes Clojure Spec integration.

This project uses [tools.deps.alpha](https://clojure.org/reference/deps_and_cli) for build tooling.

## Usage

From your project:

```clojure
(require '[borh.clj-sudachi.core :as sudachi])

(sudachi/parse "すもももももももものうち")
;; Output:
```

Which corresponds to:

```bash
$ echo 'すもももももももものうち' | jumanpp --force-single-path

```

Alternatively, if you cloned this repository, you can try it out by installing the official [Clojure CLI tools](https://clojure.org/guides/getting_started) and starting the REPL:

```bash
$ clj
```

## Testing

The whole processing pipeline is spec'd with clojure.spec. More tests need to be added.

The project uses [kaocha](https://github.com/lambdaisland/kaocha/) for running tests.
Test with:

```bash
$ bin/kaocha
```

Note that currently the provided script calls out to bash, so a POSIX environment is required.

## Limitations

## Future Work

## License

Copyright © 2019 Bor Hodošček

Distributed under the Apache License, Version 2.0.
