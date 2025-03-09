(ns config.core
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]))

(defn dot-env [f]
  (letfn [(parse-line [line]
            (let [[first second] (str/split line #"=" 2)]
              [(keyword first)
               (str/replace second "\"" "")]))]
    (when (.exists (io/as-file f))
      (->> (slurp f)
           (str/split-lines)
           (map str/trim)
           (remove #(or (empty? %) (str/starts-with? % "#")))
           (map parse-line)
           (remove (comp str/blank? second))
           (into {})))))

(defn sys-env []
  (->> (System/getenv)
       (map (juxt (comp keyword key) val))
       (into {})))

(comment

  (dot-env ".env")

  (sys-env)

  :.)
