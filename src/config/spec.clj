(ns config.spec
  (:require
   [clojure.spec.alpha :as s]
   [clojure.zip :as zip]))

(s/def :cfg/string
  (s/conformer #(if (and (string? %) (re-matches #"^[\s\S]+$" %))
                  %
                  ::s/invalid)))

(s/def :cfg/integer
  (s/conformer #(if (and (string? %) (re-matches #"[0]|[-+]?[1-9][0-9]*" %))
                  (parse-long %)
                  ::s/invalid)))

(s/def :cfg/decimal
  (s/conformer #(if (and (string? %) (re-matches #"-?(0|[1-9][0-9]{0,17})(\.[0-9]{1,17})?([eE][+-]?[0-9]{1,9}})?" %))
                  (parse-double %)
                  ::s/invalid)))

(s/def :cfg/boolean
  (s/conformer #(if (and (string? %) (re-matches #"true|false" %))
                  (parse-boolean %)
                  ::s/invalid)))

(s/def :cfg/url
  (s/conformer #(if (and (string? %) (re-matches #"\S*" %))
                  %
                  ::s/invalid)))


(defn spec-zip [m]
  (zip/zipper (fn [x]
                (and (vector? x)
                     (some vector? x)))
              (fn [x]
                (filter vector? x))
              nil
              m))

(defn spec-key [m]
  (when-let [k (first m)]
    (when (keyword k)
      k)))

(defn spec-attrs [m]
  (when-let [attrs (second m)]
    (when (map? attrs)
      attrs)))

(comment

  (s/conform :cfg/boolean "true")
  ;;=> true
  (s/conform :cfg/boolean "false")
  ;;=> false
  (s/conform :cfg/boolean "yes")
  ;;=> :clojure.spec.alpha/invalid

  (s/conform :cfg/integer "128")
  ;;=> 128
  (s/conform :cfg/integer "-10")
  ;;=> -10
  (s/conform :cfg/integer "foo")
  ;;=> :clojure.spec.alpha/invalid

  (s/conform :cfg/decimal "32")
  ;;=> 32.0
  (s/conform :cfg/decimal "87.05")
  ;;=> 87.05
  (s/conform :cfg/decimal "87E")
  ;;=> :clojure.spec.alpha/invalid

  (s/conform :cfg/string "John")
  ;;=> "John"

  (->> (slurp "test/config/struc.edn")
       (read-string)
       (spec-zip)
       (iterate zip/next)
       (take-while (complement zip/end?))
       (map (fn [loc]
              (let [n (zip/node loc)]
                [(spec-key n) 
                 (-> n spec-attrs :of-type)
                 (zip/path loc)]))))
  ;;=> ([nil nil]
  ;;    [:api nil]
  ;;    [:base-url :cfg/url]
  ;;    [:auth :cfg/string]
  ;;    [:default-query-params nil]
  ;;    [:offset :cfg/integer]
  ;;    [:limit :cfg/integer]
  ;;    [:safe :cfg/boolean]
  ;;    [:timeout :cfg/decimal])

  :.)
