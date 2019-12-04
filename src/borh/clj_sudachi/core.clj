(ns borh.clj-sudachi.core
  (:require [clojure.spec.alpha :as s]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.string :as string]
            [aero.core :as aero])
  (:import [com.worksap.nlp.sudachi
            DictionaryFactory
            Tokenizer$SplitMode
            JapaneseDictionary
            JapaneseTokenizer
            Morpheme Tokenizer Dictionary]
           [java.io FileNotFoundException]
           [java.nio.file LinkOption NoSuchFileException]))

;; Also: https://github.com/hiroaqii/kabosu/blob/master/src/kabosu/core.clj

(s/def ::dictionary-type #{:full :core :small})

(s/fdef dictionary
  :args (s/cat :dictionary-type ::dictionary-type)
  ;; We match against Dictionary to cover all cases.
  :ret #(instance? Dictionary %))

(defn canonicalize-path
  "Returns string of path will all symbolic links resolved."
  [s]
  (try
    (-> s io/file .toPath (.toRealPath (make-array LinkOption 0)) .toString)
    (catch NoSuchFileException _ nil)))

;; TODO Investigate hooks/plugins support
(defn dictionary
  "Given a dictionary type of :full, :core, or :small, returns the loaded dictionary object.
  Typical usage occurs only within the tokenizer function."
  [dictionary-type]
  ;; The config file is in the root of the Sudachi jar:
  (let [dictionary-path (canonicalize-path (:dictionary-path (aero/read-config (io/resource "dictionary-path.edn"))))
        _ (println dictionary-path)
        sudachi-config (slurp (io/input-stream (io/resource "sudachi_fulldict.json")))
        dictionary-filename (case dictionary-type
                              :full (str dictionary-path "/system_full.dic")
                              :core (str dictionary-path "/system_core.dic")
                              :small (str dictionary-path "/system_small.dic"))
        json (string/replace sudachi-config "system_full.dic" dictionary-filename)]
    (try (.create (DictionaryFactory.) json)
         (catch FileNotFoundException _
           (throw (Exception. (str "Failed to open '" dictionary-filename "'.\nPlease install the Sudachi dictionary files by putting them into the SudachiDict directory or provide the correct path using the SUDACHIDICT_PATH environment variable. Download them from: https://github.com/WorksApplications/SudachiDict")))))))

(s/def ::split-keyword-type #{:A :B :C})

(s/fdef split-mode
  :args (s/cat :split-type ::split-keyword-type))

(s/def ::split-type #(instance? Tokenizer$SplitMode %))

(defn split-mode [split-type]
  (case split-type
    :A Tokenizer$SplitMode/A
    :B Tokenizer$SplitMode/B
    :C Tokenizer$SplitMode/C))

(s/def ::tokenizer
  ;; We match against Tokenizer to match all cases.
  #(instance? Tokenizer %))

(s/fdef tokenizer
  :args (s/cat :dictionary-type ::dictionary-type)
  :ret ::tokenizer)

(defn ^JapaneseTokenizer tokenizer
  ([]
   (tokenizer :full))
  ([dictionary-type]
   (.create ^JapaneseDictionary (dictionary dictionary-type))))

;; Defaults are provided, but you may wish to override them with `binding`, or use the constructors provided.
(def ^:dynamic *split-mode* (split-mode :A))
(def ^:dynamic *tokenizer* (tokenizer :full))

(s/def ::morpheme
  (s/keys :req [:morpheme/surface :morpheme/lemma :morpheme/reading :morpheme/pos
                :morpheme/pos-1 :morpheme/pos-2 :morpheme/pos-3 :morpheme/pos-4
                :morpheme/c-type :morpheme/c-form :morpheme/oov?]))

(s/def ::morphemes (s/coll-of ::morpheme))

(s/fdef parse
  :args (s/alt :one (s/cat :text string?)
               :two (s/cat :split-mode ::split-type :text string?)
               :tri (s/cat :tokenizer ::tokenizer :split-mode ::split-type :text string?))
  :ret ::morphemes)

(defn extract-morpheme-info
  [^Morpheme m]
  (let [pos (into [] (.partOfSpeech m))]
    #:morpheme{:surface (.surface m)
               :lemma   (.normalizedForm m)
               :reading (.readingForm m)
               :pos     (str/join "-" (subvec pos 0 4))
               :pos-1   (get pos 0)
               :pos-2   (get pos 1)
               :pos-3   (get pos 2)
               :pos-4   (get pos 3)
               :c-type  (get pos 4)
               :c-form  (get pos 5)
               :oov?    (.isOOV m)}))

(defn parse
  "Returns a vector of morpheme maps for given string `s`.
  Defaults to using the full dictionary and A-type splitting if `tokenizer` and `split-mode` are not provided. `tokenizer` and `spit-mode` must be Sudachi objects created with the `tokenizer` and `split-mode` factories."
  ([s]
   (parse *tokenizer* *split-mode* s))
  ([split-mode s]
   (parse ^JapaneseTokenizer *tokenizer* ^Tokenizer$SplitMode split-mode s))
  ([tokenizer split-mode s]
   (into []
         (map extract-morpheme-info)
         (.tokenize ^JapaneseTokenizer tokenizer ^Tokenizer$SplitMode split-mode s))))

(comment

  ;; Usage

  (use 'criterium.core)
  (quick-bench (parse "解析する❥．"))
  (let [t (tokenizer :full) sm (split-mode :A)] (quick-bench (parse t sm "解析する❥．"))))

