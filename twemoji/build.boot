(set-env!
 :resource-paths #{"resources"}
 :dependencies '[[cljsjs/boot-cljsjs "0.7.1" :scope "test"]])

(require '[cljsjs.boot-cljsjs.packaging :refer :all])

(def +lib-version+ "2.3.0")
(def +version+ (str +lib-version+ "-0"))

(task-options!
 pom {:project 'cljsjs/twemoji
      :version +version+
      :description "Twitter Emoji for Everyone"
      :url "http://twitter.github.io/twemoji/"
      :scm {:url "https://github.com/twitter/twemoji"}
      :license {"MIT license" "https://github.com/twitter/twemoji/blob/gh-pages/LICENSE"}})

(defn download-url [version filename]
  (format "http://twemoji.maxcdn.com/2/%s?%s" filename version))

(defn twemoji-files [version]
  {:js {:name "twemoji.js"
        :url (download-url version "twemoji.js")
        :md5 "c04a5730b20e1b6e0173ad205a8a59a3"}
   :js-min {:name "twemoji.min.js"
            :url (download-url version "twemoji.min.js")
            :md5 "15d0721fa247d659e41324e7fd968c14"}})

(defn download-files [version]
  (let [files (twemoji-files version)]
    (apply comp
      (for [{:keys [name url md5]} (vals files)]
        (download :name name :url url :checksum md5)))))

(deftask package []
  (comp (download-files +lib-version+)
        (sift :move {#"twemoji.js" "cljsjs/twemoji/development/twemoji.inc.js"
                     #"twemoji.min.js" "cljsjs/twemoji/production/twemoji.min.inc.js"})
        (deps-cljs :name "cljsjs.twemoji")
        (pom)
        (show :fileset true)
        (jar)))
