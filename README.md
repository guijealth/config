# config

Clojure config lib

```
com.github.guijealth/config {:git/tag "0.1.0-SNAPSHOT", :git/sha "0c24ac6"}
```

```clojure
  (require '[config.core :as cfg])

  ((fn [struc]
     (-> {}
         (cfg/patch (dotenv/parse ".env") struc)
         (cfg/patch (sysenv/read-all) struc)))
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
```