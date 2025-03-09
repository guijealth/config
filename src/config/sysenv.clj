(ns config.sysenv)

(defn read-all []
  (->> (System/getenv)
       (map (juxt (comp keyword key) val))
       (into {})))

(comment
  
  (read-all)
  
  :.)