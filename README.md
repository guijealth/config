# config

Clojure config lib

[12F APP - Config](https://12factor.net/config) Store config in the environment.

Automatically validate and convert values based on common data-type [conformers](#conformers).
Freely convert *env vars* into a nested map using Hiccup-like structure definition.

```
com.github.guijealth/config {:git/tag "0.1.0-SNAPSHOT", :git/sha "08d4c8a"}
```

```clojure
(require '[config.core :as cfg])
(require '[config.dotenv :as dotenv])
(require '[config.sysenv :as sysenv])

(def struc
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

(-> {}                                      ; you can start from any config.edn file or empty map
    (cfg/patch (dotenv/parse ".env") struc) ; patch the config with ".env" file, based on struc
    (cfg/patch (sysenv/read-all) struc))    ; patch the config with system env vars, based on struc
;;=> {:api
;;    {:base-url "http://localhost:8080/fake/url",
;;     :auth
;;     "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30"},
;;    :default-query-params {:offset 0, :limit 128, :safe true, :timeout 60000.0},
;;    :some {:nested {:foo "bar", :home "/Users/guille"}}}
```

## Conformers

Conformers both, validate and parse value into indicated type. Below the list of supported conformers
ask for more conformers if needed.

| spec           | description                                        | examples                |
| -------------- | -------------------------------------------------- | ----------------------- |
| `:cfg/string`  | A sequence of Unicode characters                   | `"foo bar"`             |
| `:cfg/integer` | A signed integer                                   | `128`, `-34`            |
| `:cfg/decimal` | Rational number that have a decimal representation | `87.05`                 |
| `:cfg/boolean` | Boolean value                                      | `true`, `false`         |
| `:cfg/url`     | RFC1738 Uniform Resource Locator                   | `http://localhost:8081` |