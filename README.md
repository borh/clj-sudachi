# clj-jumanpp

A Clojure library for idiomatic access to the [Japanese Morphological Analyzer JUMAN++](http://nlp.ist.i.kyoto-u.ac.jp/index.php?JUMAN++).

[![Clojars Project](https://img.shields.io/clojars/v/clj-jumanpp.svg)](https://clojars.org/clj-jumanpp)
[![License](https://img.shields.io/badge/License-EPL%201.0-red.svg)](https://opensource.org/licenses/EPL-1.0)

## Status

This is an early release and most of the API is still subject to change as a result of real-world usage. In particular, some of the feature field names might change in future versions. While only minimal testing on real data has been conducted, future versions will be tested on large text corpora.

## Requirements

You need to first install [jumanpp](https://github.com/ku-nlp/jumanpp) and have it on your path before proceeding. Note that this requires ~1GB disk space.

Dependency information:

```clojure
[borh/clj-jumanpp "0.1.0"]
```

Further note that you will need Clojure 1.9 (currently tested with alpha14) to use this library as it makes heavy use of clojure.spec.

This project uses [boot](http://boot-clj.com/) for build tooling.

## Usage

From your project:

```clojure
(require '[clj-jumanpp.core :as jumanpp])

(jumanpp/parse "すもももももももものうち")
;; Output:
[[#:clj-jumanpp.core{:features {:自動獲得 "テキスト"},
                     :conj-form "*",
                     :pos "名詞",
                     :conj-type "*",
                     :conj-form-id 0,
                     :pos-id 6,
                     :sub-pos-id 1,
                     :conj-type-id 0,
                     :surface-base "すもも",
                     :reading "すもも",
                     :sub-pos "普通名詞",
                     :surface "すもも"}
  #:clj-jumanpp.core{:features nil,
                     :conj-form "*",
                     :pos "助詞",
                     :conj-type "*",
                     :conj-form-id 0,
                     :pos-id 9,
                     :sub-pos-id 2,
                     :conj-type-id 0,
                     :surface-base "も",
                     :reading "も",
                     :sub-pos "副助詞",
                     :surface "も"}
  #:clj-jumanpp.core{:features {:代表表記 "股/もも", :カテゴリ "動物-部位"},
                     :conj-form "*",
                     :pos "名詞",
                     :conj-type "*",
                     :conj-form-id 0,
                     :pos-id 6,
                     :sub-pos-id 1,
                     :conj-type-id 0,
                     :surface-base "もも",
                     :reading "もも",
                     :sub-pos "普通名詞",
                     :surface "もも"}
  #:clj-jumanpp.core{:features nil,
                     :conj-form "*",
                     :pos "助詞",
                     :conj-type "*",
                     :conj-form-id 0,
                     :pos-id 9,
                     :sub-pos-id 2,
                     :conj-type-id 0,
                     :surface-base "も",
                     :reading "も",
                     :sub-pos "副助詞",
                     :surface "も"}
  #:clj-jumanpp.core{:features {:代表表記 "股/もも", :カテゴリ "動物-部位"},
                     :conj-form "*",
                     :pos "名詞",
                     :conj-type "*",
                     :conj-form-id 0,
                     :pos-id 6,
                     :sub-pos-id 1,
                     :conj-type-id 0,
                     :surface-base "もも",
                     :reading "もも",
                     :sub-pos "普通名詞",
                     :surface "もも"}
  #:clj-jumanpp.core{:features nil,
                     :conj-form "*",
                     :pos "助詞",
                     :conj-type "*",
                     :conj-form-id 0,
                     :pos-id 9,
                     :sub-pos-id 3,
                     :conj-type-id 0,
                     :surface-base "の",
                     :reading "の",
                     :sub-pos "接続助詞",
                     :surface "の"}
  #:clj-jumanpp.core{:features {:代表表記 "うち/うち"},
                     :conj-form "*",
                     :pos "名詞",
                     :conj-type "*",
                     :conj-form-id 0,
                     :pos-id 6,
                     :sub-pos-id 9,
                     :conj-type-id 0,
                     :surface-base "うち",
                     :reading "うち",
                     :sub-pos "副詞的名詞",
                     :surface "うち"}]]
```

Which corresponds to:

```bash
$ echo 'すもももももももものうち' | jumanpp --force-single-path
すもも すもも すもも 名詞 6 普通名詞 1 * 0 * 0 "自動獲得:テキスト"
も も も 助詞 9 副助詞 2 * 0 * 0 NIL
もも もも もも 名詞 6 普通名詞 1 * 0 * 0 "代表表記:股/もも カテゴリ:動物-部位"
も も も 助詞 9 副助詞 2 * 0 * 0 NIL
もも もも もも 名詞 6 普通名詞 1 * 0 * 0 "代表表記:股/もも カテゴリ:動物-部位"
の の の 助詞 9 接続助詞 3 * 0 * 0 NIL
うち うち うち 名詞 6 副詞的名詞 9 * 0 * 0 "代表表記:うち/うち"
EOS
```

Alternatively, if you cloned this repository, you can try it out by installing [boot](http://boot-clj.com/) and starting the REPL:

```bash
$ boot dev
```

The `dev` command will also start a nrepl server so you can connect with your favourite IDE/editor.

## Testing

The whole processing pipeline is spec'd with clojure.spec. More tests need to be added.

Test with:

```bash
$ boot test
```

## Limitations

-   Currently only exposes the best parse ("--force-single-path").
-   jumanpp is called via shelling out as there are currently no JVM bindings available.
-   Dependency on `clojure.data.csv` for parsing output format.

## Future Work

-   Improve parsing of jumanpp format (flexibility--support n-best; speed--custom parser).
-   Provide server-client parsing mode to increase throughput.
-   Improve and expand generative tests.
-   Integration with KNP.
-   Benchmarking.

## License

Copyright © 2016 Bor Hodošček

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
