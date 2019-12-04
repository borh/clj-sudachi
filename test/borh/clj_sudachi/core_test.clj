(ns borh.clj-sudachi.core-test
  (:require [clojure.test :refer [deftest is]]
            [orchestra.spec.test :as st]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [borh.clj-sudachi.core :refer :all])
  (:import [com.worksap.nlp.sudachi
            DictionaryFactory
            Tokenizer$SplitMode
            JapaneseDictionary
            JapaneseTokenizer
            Morpheme Tokenizer]))

(set! *warn-on-reflection* true)
(st/instrument)

(deftest parse-sentence-test
         (is (= (parse "解析する❥．")
                [#:morpheme{:pos-3 "サ変可能",
                            :c-form "*",
                            :pos "名詞-普通名詞-サ変可能-*",
                            :pos-1 "名詞",
                            :lemma "解析",
                            :pos-2 "普通名詞",
                            :pos-4 "*",
                            :oov? false,
                            :c-type "*",
                            :reading "カイセキ",
                            :surface "解析"}
                 #:morpheme{:pos-3 "*",
                            :c-form "連体形-一般",
                            :pos "動詞-非自立可能-*-*",
                            :pos-1 "動詞",
                            :lemma "為る",
                            :pos-2 "非自立可能",
                            :pos-4 "*",
                            :oov? false,
                            :c-type "サ行変格",
                            :reading "スル",
                            :surface "する"}
                 #:morpheme{:pos-3 "サ変可能",
                            :c-form "*",
                            :pos "名詞-普通名詞-サ変可能-*",
                            :pos-1 "名詞",
                            :lemma "❥.",
                            :pos-2 "普通名詞",
                            :pos-4 "*",
                            :oov? true,
                            :c-type "*",
                            :reading "",
                            :surface "❥．"}])))

(deftest parse-vararg-equivalence-test
  (is (= (parse "トーケン")
         (parse (split-mode :A) "トーケン")
         (parse (tokenizer :full) (split-mode :A) "トーケン"))))

(deftest dictionary-test
  (is (let [s "a"]
        (= (parse (tokenizer :full) (split-mode :A) s)
           (parse (tokenizer :core) (split-mode :A) s)
           (parse (tokenizer :small) (split-mode :A) s)))))

(deftest parse-split-test
  (is (= 4 (count (parse (tokenizer :full) (split-mode :A) "国立国語研究所"))))
  (is (= 4 (count (parse "国立国語研究所"))))
  (is (= (let [t (tokenizer :full)]
           (->> [(split-mode :A) (split-mode :B) (split-mode :C)]
                (map #(parse t % "国立国語研究所"))
                (mapv count)))
         [4 3 1])))
