(ns kotoba-ui.shell-test
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.string :as str]
            [shitsuke.hiccup :as hiccup]
            [kotoba-ui.shell :as shell]
            [kotoba-ui.core :as ui]))

(defn- count-occurrences [s sub]
  (loop [i 0 n 0]
    (if-let [j (str/index-of s sub i)]
      (recur (inc j) (inc n))
      n)))

(deftest re-export-identity-test
  (testing "kotoba-ui.core shell entries are the same vars, not reimplementations"
    (is (identical? shell/stack ui/stack))
    (is (identical? shell/hero ui/hero))
    (is (identical? shell/page ui/page))
    (is (identical? shell/shell-css ui/shell-css))))

(deftest stack-test
  (testing "default: flex column div, no inline style"
    (let [html (hiccup/->html (shell/stack [:p "a"] [:p "b"]))]
      (is (str/starts-with? html "<div"))
      (is (str/includes? html "kotoba-shell__stack"))
      (is (not (str/includes? html "kotoba-shell__stack--horizontal")))
      (is (not (str/includes? html "style=")))
      (is (str/includes? html "<p>a</p><p>b</p>"))))
  (testing "opts: direction/gap/align map onto modifier class + token vars"
    (let [html (hiccup/->html (shell/stack {:direction :horizontal :gap :6 :align :center}
                                           [:span "x"]))]
      (is (str/includes? html "kotoba-shell__stack--horizontal"))
      (is (str/includes? html "gap:var(--hig-spacing-6);"))
      (is (str/includes? html "align-items:center;")))))

(deftest spacer-test
  (let [html (hiccup/->html (shell/spacer))]
    (is (str/includes? html "kotoba-shell__spacer"))
    (is (str/includes? html "aria-hidden"))))

(deftest section-test
  (testing "semantic <section> with a .hig-title2 heading"
    (let [html (hiccup/->html (shell/section {:title "Docs"} [:p "body"]))]
      (is (str/starts-with? html "<section"))
      (is (str/includes? html "kotoba-shell__section"))
      (is (str/includes? html "kotoba-shell__section-title hig-title2"))
      (is (str/includes? html "<h2"))
      (is (str/includes? html "Docs"))
      (is (str/includes? html "<p>body</p>"))))
  (testing "no title -> no heading; :wide adds the modifier"
    (let [html (hiccup/->html (shell/section {:wide true} [:p "b"]))]
      (is (not (str/includes? html "<h2")))
      (is (str/includes? html "kotoba-shell__section--wide")))))

(deftest hero-test
  (let [html (hiccup/->html (shell/hero {:title "Kotoba"
                                         :tagline "One require."
                                         :actions [(ui/button "Go" {:act :go})]}))]
    (is (str/starts-with? html "<header"))
    (is (str/includes? html "kotoba-shell__hero-title hig-large-title"))
    (is (str/includes? html "<h1"))
    (is (str/includes? html "kotoba-shell__hero-tagline hig-title3"))
    (is (str/includes? html "One require."))
    (is (str/includes? html "kotoba-shell__hero-actions"))
    (is (str/includes? html "liquid-glass__button")))
  (testing "tagline/actions are optional"
    (let [html (hiccup/->html (shell/hero {:title "T"}))]
      (is (not (str/includes? html "hero-tagline")))
      (is (not (str/includes? html "hero-actions"))))))

(deftest grid-test
  (testing "default grid carries no inline style (min lives in the stylesheet)"
    (let [html (hiccup/->html (shell/grid [:div "card"]))]
      (is (str/includes? html "kotoba-shell__grid"))
      (is (not (str/includes? html "style=")))))
  (testing ":min overrides the track minimum per instance"
    (let [html (hiccup/->html (shell/grid {:min "320px"} [:div "card"]))]
      (is (str/includes? html "repeat(auto-fill, minmax(320px, 1fr))")))))

(deftest app-shell-test
  (testing "nav wrapper + <main> content"
    (let [html (hiccup/->html (shell/app-shell {:nav (ui/nav-bar "App")}
                                               [:p "content"]))]
      (is (str/includes? html "kotoba-shell__app"))
      (is (str/includes? html "kotoba-shell__app-nav"))
      (is (str/includes? html "liquid-glass__nav-bar"))
      (is (str/includes? html "<main class=\"kotoba-shell__app-main\"><p>content</p></main>"))
      (is (not (str/includes? html "kotoba-shell__app--with-sidebar")))
      (is (not (str/includes? html "<aside")))))
  (testing ":sidebar adds the two-column modifier + <aside>"
    (let [html (hiccup/->html (shell/app-shell {:sidebar [:nav "links"]} [:p "c"]))]
      (is (str/includes? html "kotoba-shell__app--with-sidebar"))
      (is (str/includes? html "<aside class=\"kotoba-shell__app-sidebar\"")))))

(deftest page-test
  (let [html (hiccup/->html (shell/page {:title "T" :description "D"
                                         :theme {:appearance :dark}}
                                        [:p "b"]))]
    (is (str/starts-with? html "<html lang=\"en\" data-appearance=\"dark\">"))
    (is (str/includes? html "<meta charset=\"utf-8\">"))
    (is (str/includes? html "name=\"viewport\""))
    (is (str/includes? html "<title>T</title>"))
    (is (str/includes? html "name=\"description\""))
    (is (str/includes? html "<style>"))
    (is (str/includes? html "<body><p>b</p></body>")))
  (testing ":auto appearance omits the attribute on <html>; :lang overrides"
    (let [html (hiccup/->html (shell/page {:title "T" :lang "ja"} [:p "b"]))]
      (is (str/starts-with? html "<html lang=\"ja\"><head>")))))

(deftest shell-css-test
  (let [css (shell/shell-css)]
    (testing "everything is inside @layer kotoba.hig"
      (is (str/starts-with? css "@layer kotoba.hig {"))
      (is (= 1 (count-occurrences css "@layer"))))
    (testing "balanced braces"
      (is (= (count-occurrences css "{") (count-occurrences css "}"))))
    (testing "token purity: zero hex color literals (vars/color-mix only)"
      (is (nil? (re-find #"#[0-9a-fA-F]" css)))
      (is (str/includes? css "var(--hig-"))
      (is (str/includes? css "color-mix(in srgb, var(--hig-color-tint) 12%, transparent)")))
    (testing "the production overflow guards are present"
      (is (str/includes? css ".kotoba-shell__app-main { min-width: 0; overflow-wrap: break-word; }"))
      (is (str/includes? css ".kotoba-shell__grid > * { min-width: 0; }")))
    (testing "responsive sidebar collapse"
      (is (str/includes? css "@media (max-width: 768px)")))))
