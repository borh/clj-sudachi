(ns clj-jumanpp.core
  (:require [clojure.spec :as s]
            [clojure.string :as str]
            [clojure.data.csv :as csv]
            [me.raynes.conch :as sh]))

(defn integer-conformer [s]
  (if (number? s)
    s
    (try (Integer/parseInt s)
         (catch Exception e :clojure.spec/invalid))))

;; 表層形 読み 見出し語 品詞大分類 品詞大分類_ID 品詞細分類 品詞細分類_ID
;; 活用型 活用型_ID 活用形 活用形_ID 意味情報
(s/def ::surface string?)
(s/def ::reading string?)
(s/def ::surface-base string?)
(s/def ::pos string?)
(s/def ::pos-id (s/conformer integer-conformer))
(s/def ::sub-pos string?)
(s/def ::sub-pos-id (s/conformer integer-conformer))
(s/def ::conj-type string?)
(s/def ::conj-type-id (s/conformer integer-conformer))
(s/def ::conj-form string?)
(s/def ::conj-form-id (s/conformer integer-conformer))

(defn features-conformer
  "Transforms jumanpp's last feature field (意味情報) into a map for easier filtering in processing pipelines. While all features are split on whitespace, some are key-value based (separated with:), and some are single-value. Key-values are simply merged into a map, while single-value fields are keyed according to value and given true as their value."
  [s]
  (cond
    (map? s)
    s

    (string? s)
    (if (not= "NIL" s)
      (apply merge
             (map (fn [sub-s]
                    (let [[k v] (str/split sub-s #":")]
                      {(keyword k) (if v v true)}))
                  (str/split s #" "))))))

(s/def ::features (s/conformer features-conformer)
  #_(s/nilable (s/map-of keyword? (s/or bool? string?))))

(def node-keys
  [::surface
   ::reading
   ::surface-base
   ::pos
   ::pos-id
   ::sub-pos
   ::sub-pos-id
   ::conj-type
   ::conj-type-id
   ::conj-form
   ::conj-form-id
   ::features])

(s/def ::node
  (s/keys :req ~node-keys))

(s/def ::sentence (s/coll-of ::node))

(defn sentence
  "Given string representing output of one sentence of
  jumanpp-processed text, returns a vector of nodes."
  [s]
  (mapv
   (fn [s]
     ;; Due to the ad-hoc text output format of jumanpp, we elect to use a
     ;; CSV parser on \space to deal with quoting on the last
     ;; column. Other possibilities are using instaparse, especially
     ;; if we want to parse jumanpp's n-best format ('@').
     (let [fields (first (csv/read-csv s :separator \space))
           node (s/conform ::node (zipmap node-keys fields))]
       (if (= :clojure.spec/invalid node)
         (clojure.pprint/pprint (s/explain ::node (zipmap node-keys fields)))
         node)))
   (str/split-lines s)))

(s/fdef sentence
  :args (s/cat :s
               (s/and string?
                      (fn [s]
                        (every? (fn [x]
                                  (>= 12 (count (filter (partial = \space) x))))
                                (str/split-lines s)))))
  :ret (s/nilable (s/coll-of ::node)))

(defn parse
  "Given string of Japanese text comprised of one sentence per line,
  uses jumanpp to return a vector of sentences, each containing a
  vector of nodes."
  [s]
  (if (not-empty s)
    (let [parsed-string
          (sh/with-programs
            [jumanpp]
            ;; TODO Currently, we force the output of only the most
            ;; likely path as it simplifies parsing. Relaxing of this
            ;; constraint is predicated on improvement to the
            ;; CSV-based parsing in `sentence`.
            (jumanpp "--force-single-path" {:in s}))]
      (mapv sentence
            (str/split parsed-string #"\nEOS\n")))))

(s/fdef parse
  :args (s/cat :s string?)
  :ret (s/nilable (s/coll-of ::sentence)))

;; TODO Provide a server-client setup to improve
;; throughput. Currently, parse will shell out to jumanpp on every
;; call. By using the server feature of jumanpp, we can communicate
;; via socket. The remaining consideration is how to deal with use in
;; a KNP pipeline.
