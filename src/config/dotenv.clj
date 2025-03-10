(ns config.dotenv
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io]))

(defn parse-line [line]
  (let [[first second] (str/split line #"=" 2)]
    [(keyword first)
     (str/replace second "\"" "")]))

(defn parse [f]
  (when (.exists (io/as-file f))
    (->> (slurp f)
         (str/split-lines)
         (map str/trim)
         (remove #(or (empty? %) (str/starts-with? % "#")))
         (map parse-line)
         (remove (comp str/blank? second))
         (into {}))))

(comment

  (parse "test/config/data/.env")
  ;;=> {:BASE_URL "http://localhost:8080/fake/url",
  ;;    :AUTH
  ;;    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30",
  ;;    :DQP_OFFSET "0",
  ;;    :DQP_LIMIT "128",
  ;;    :DQP_SAFE_BY_DEFAULT "true",
  ;;    :DQP_TIMEOUT "60000"}

  :.)