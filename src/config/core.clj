(ns config.core
  (:require
   [clojure.spec.alpha :as s]
   [clojure.zip :as zip]
   [config.spec :refer [spec-zip spec-key spec-attrs identity-struc]]
   [config.dotenv :as dotenv]
   [config.sysenv :as sysenv]
   [clojure.string :as str]))

(defn patch
  ([config environment struc]
   (letfn [(patch [loc]
             (let [node (zip/node loc)
                   attrs (spec-attrs node)
                   path (map spec-key (zip/path loc))]
               [(:env attrs)
                (:of-type attrs)
                (->> [(spec-key node)]
                     (concat path)
                     (remove nil?)
                     (into []))]))]
     (->> (spec-zip (or struc (identity-struc environment)))
          (zip/down)
          (iterate zip/next)
          (take-while (complement zip/end?))
          (map patch)
          (reduce (fn [acc [env type path]]
                    (let [raw (get environment (keyword env))
                          spec (or type :cfg/string)
                          conformant (s/conform spec raw)]
                      (cond
                        (nil? env) (update-in acc path #(or % {}))
                        (str/blank? raw) acc
                        (= ::s/invalid conformant) (throw (ex-info "Non conformant env" {:env env, :of-type spec, :path path}))
                        :else (assoc-in acc path conformant))))
                  config))))
  ([config environment]
   (patch config environment nil)))

(comment

  ((fn [struc]
     (-> {}
         (patch (dotenv/parse ".env") struc)
         (patch (sysenv/read-all) struc)))
   [[:api
     [:base-url {:env "BASE_URL", :of-type :cfg/url}]
     [:auth {:env "AUTH", :of-type :cfg/string}]]
    [:default-query-params
     [:offset {:env "DQP_OFFSET", :of-type :cfg/integer}]
     [:limit {:env "DQP_LIMIT", :of-type :cfg/integer}]
     [:safe {:env "DQP_SAFE_BY_DEFAULT", :of-type :cfg/boolean}]
     [:timeout {:env "DQP_TIMEOUT", :of-type :cfg/decimal}]]
    [:some
     [:nested
      [:foo {:env "FOO" :of-type :cfg/string}]
      [:home {:env "HOME" :of-type :cfg/string}]]]])
  ;;=> {:api
  ;;    {:base-url "http://localhost:8080/fake/url",
  ;;     :auth
  ;;     "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30"},
  ;;    :default-query-params {:offset 0, :limit 128, :safe true, :timeout 60000.0},
  ;;    :some {:nested {:foo "bar", :home "/Users/guille"}}}

  :.)
