(ns kotoba-ui.shell
  "Pure-hiccup HIG layout scaffolds — the page/layout layer this stack was
  missing (consumers hand-wrote 400+ lines of ad-hoc `.layout`/`.hero` CSS
  before it existed).

  Same dual-render contract as the rest of the stack: every component is
  plain hiccup that renders via shitsuke.hiccup/->html (SSR) or reagent
  (browser). Each component takes an optional opts map first, then children.
  All visual values come from `--hig-*` custom properties (see
  kotoba-ui.shell.style) — zero hardcoded colors/sizes — and all shell CSS
  lives inside the `kotoba.hig` cascade layer, so unlayered app CSS always
  wins.

  `page` is the full-document scaffold: it stamps `data-appearance` and
  embeds kotoba-ui.theme/theme-css, so `(->html (page {...} body))` (or
  kotoba-ui.core/->page) is a complete refined page from one call."
  (:require [kotoba-ui.theme :as theme]
            [kotoba-ui.shell.style :as style]))

(def class-name
  "Stable `kotoba-shell__*` class for a component or component--modifier
  (re-exported from kotoba-ui.shell.style)."
  style/class-name)

(def shell-css
  "All kotoba-shell__* structural rules wrapped in `@layer kotoba.hig {...}`
  (re-exported from kotoba-ui.shell.style; already included in
  kotoba-ui.theme/theme-css)."
  style/shell-css)

;; ---------------------------------------------------------------------------
;; small shared helpers

(defn- split-opts
  "[opts children] from a variadic arg list whose first element may be an
  opts map. Hiccup children are never maps, so map? is unambiguous."
  [args]
  (if (map? (first args))
    [(first args) (rest args)]
    [nil args]))

(defn- cls
  "Join a required kotoba-shell class with an optional consumer :class opt
  (same helper shape as liquid-glass.components)."
  [base extra]
  (if (seq extra) (str base " " extra) base))

(defn- css-value
  "Keyword or string opt value -> CSS string (:center -> \"center\")."
  [v]
  (if (keyword? v) (name v) (str v)))

(defn- spacing-var
  "Spacing token keyword -> its `--hig-spacing-*` var reference
  (:6 -> \"var(--hig-spacing-6)\")."
  [k]
  (str "var(--hig-spacing-" (name k) ")"))

;; ---------------------------------------------------------------------------
;; scaffolds

(defn stack
  "Flex column (row with `:direction :horizontal`). opts: :gap (spacing
  token keyword, default :4 via the stylesheet), :align (align-items),
  :justify (justify-content), :class."
  [& args]
  (let [[opts children] (split-opts args)
        {:keys [direction gap align justify class]} opts
        style (merge (when gap     {:gap (spacing-var gap)})
                     (when align   {:align-items (css-value align)})
                     (when justify {:justify-content (css-value justify)}))]
    (into [:div (cond-> {:class (cls (str (class-name :stack)
                                          (when (= direction :horizontal)
                                            (str " " (class-name "stack--horizontal"))))
                                     class)}
                  (seq style) (assoc :style style))]
          children)))

(defn spacer
  "Flex-grow filler — pushes siblings apart inside a stack/toolbar row."
  []
  [:div {:class (class-name :spacer) :aria-hidden true}])

(defn section
  "Semantic content section: hairline top border, content-margin padding,
  readable max-width column centered by default. opts: :title (rendered as a
  `.hig-title2` heading), :wide (true opts out of the readable max-width),
  :id, :class."
  [& args]
  (let [[opts children] (split-opts args)
        {:keys [title wide id class]} opts]
    (into [:section (cond-> {:class (cls (str (class-name :section)
                                              (when wide
                                                (str " " (class-name "section--wide"))))
                                         class)}
                      id (assoc :id id))
           (when title
             [:h2 {:class (str (class-name :section-title) " hig-title2")} title])]
          children)))

(defn hero
  "Page-entrance block: `.hig-large-title` title, `.hig-title3`
  secondary-label tagline, centered action row, generous 4pt-grid padding
  and a subtle radial accent wash (color-mix over the tint token — follows
  the theme accent, no hardcoded gradient). opts: :title, :tagline,
  :actions (seq of hiccup, typically kotoba-ui buttons), :class."
  [opts]
  (let [{:keys [title tagline actions class]} opts]
    [:header {:class (cls (class-name :hero) class)}
     [:h1 {:class (str (class-name :hero-title) " hig-large-title")} title]
     (when tagline
       [:p {:class (str (class-name :hero-tagline) " hig-title3")} tagline])
     (when (seq actions)
       (into [:div {:class (class-name :hero-actions)}] actions))]))

(defn grid
  "Responsive card grid: `repeat(auto-fill, minmax(min, 1fr))` columns.
  opts: :min (CSS length string, default 260px via the stylesheet), :gap
  (spacing token keyword), :class. Children get `min-width: 0` from the
  stylesheet — long content can't blow a track out."
  [& args]
  (let [[opts cards] (split-opts args)
        {min-w :min :keys [gap class]} opts
        style (merge (when min-w
                       {:grid-template-columns
                        (str "repeat(auto-fill, minmax(" min-w ", 1fr))")})
                     (when gap {:gap (spacing-var gap)}))]
    (into [:div (cond-> {:class (cls (class-name :grid) class)}
                  (seq style) (assoc :style style))]
          cards)))

(defn app-shell
  "Whole-app frame. opts: :nav (typically a kotoba-ui nav-bar/toolbar —
  rendered sticky at the top), :sidebar (optional; fixed-width desktop
  column that collapses to single-column under the breakpoint), :class.
  `content` renders in a `<main>` with `min-width: 0` +
  `overflow-wrap: break-word` (the grid-item overflow guard — this exact
  bug shipped in production)."
  [& args]
  (let [[opts content] (split-opts args)
        {:keys [nav sidebar class]} opts]
    [:div {:class (cls (str (class-name :app)
                            (when sidebar (str " " (class-name "app--with-sidebar"))))
                       class)}
     (when nav [:div {:class (class-name :app-nav)} nav])
     [:div {:class (class-name :app-body)}
      (when sidebar [:aside {:class (class-name :app-sidebar)} sidebar])
      (into [:main {:class (class-name :app-main)}] content)]]))

(defn page
  "The full document hiccup: `[:html ...]` with charset/viewport meta,
  title, optional description meta, the theme's complete CSS bundle
  (kotoba-ui.theme/theme-css) inlined, and `data-appearance` stamped when
  the theme forces an appearance. opts: :title, :description, :lang
  (default \"en\"), :theme (a kotoba-ui.theme map), :head (extra head
  hiccup). Render with `(->html (page ...))` — or use kotoba-ui.core/->page,
  which prepends the doctype."
  [opts & body]
  (let [{:keys [title description lang theme head]} opts]
    [:html {:lang (or lang "en")
            :data-appearance (theme/appearance-attr theme)}
     [:head
      [:meta {:charset "utf-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
      [:title (or title "")]
      (when description [:meta {:name "description" :content description}])
      [:style [:hiccup/raw (theme/theme-css theme)]]
      head]
     (into [:body] body)]))
