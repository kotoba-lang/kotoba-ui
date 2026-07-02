(ns kotoba-ui.core
  "kotoba-lang default UI/UX design — single require point.

  Product repos that want kotoba-lang's default design (see
  90-docs/adr/2607022800-kotoba-lang-default-uiux-appkit-uikit-interface-fundamentals.md)
  require only `kotoba-ui.core` and get, in one namespace:
  shitsuke's dual-render hiccup contract + liquid-glass-ui's material tokens/
  style/29 components. This namespace owns no logic of its own — every var
  below is a direct alias of a shitsuke or liquid-glass var; see the source
  repo's docstring for behavior. Adding a component/token here means adding
  the alias, nothing else — see docs/adr/0001-kotoba-ui.md.

  Not to be confused with the pre-existing `orgs/kotoba-lang/ui` repo
  (package `kotoba.ui`), which is kami-engine's unrelated WebGPU HUD overlay
  renderer and has no connection to this design system."
  (:require [shitsuke.hiccup :as hiccup]
            [liquid-glass.tokens :as tokens]
            [liquid-glass.style :as style]
            [liquid-glass.components :as glass]))

;; -- hiccup (shitsuke.hiccup) -------------------------------------------------

(def ->html
  "SSR: render kotoba-ui hiccup to an HTML string. Same fn browser reagent
  renders (shitsuke.reagent.core) consumes, per shitsuke's dual-render contract."
  hiccup/->html)

;; -- tokens (liquid-glass.tokens) ---------------------------------------------

(def resolve-tokens tokens/resolve-tokens)
(def resolve-dark-tokens tokens/resolve-dark-tokens)
(def css-variables tokens/css-variables)
(def dark-css-variables tokens/dark-css-variables)
(def spring-linear-easing tokens/spring-linear-easing)

;; -- style (liquid-glass.style) ------------------------------------------------

(def class-name style/class-name)
(def root-css style/root-css)
(def component-rules style/component-rules)
(def component-css style/component-css)
(def inline-style style/inline-style)
(def inline-style-hiccup style/inline-style-hiccup)

;; -- components (liquid-glass.components) -------------------------------------
;; Structural / navigation

(def panel glass/panel)
(def toolbar glass/toolbar)
(def nav-bar glass/nav-bar)
(def tab-bar glass/tab-bar)
(def sheet glass/sheet)
(def alert glass/alert)
(def menu glass/menu)
(def scrim glass/scrim)
(def list-view glass/list-view)
(def list-row glass/list-row)
(def disclosure glass/disclosure)

;; Controls

(def button glass/button)
(def icon-button glass/icon-button)
(def text-field glass/text-field)
(def text-area glass/text-area)
(def search-field glass/search-field)
(def menu-select glass/menu-select)
(def toggle glass/toggle)
(def checkbox glass/checkbox)
(def radio glass/radio)
(def slider glass/slider)
(def stepper glass/stepper)

;; Feedback / content

(def progress-bar glass/progress-bar)
(def progress-circle glass/progress-circle)
(def gauge glass/gauge)
(def badge glass/badge)
(def chip glass/chip)
(def label glass/label)
(def avatar glass/avatar)
(def divider glass/divider)
(def tooltip glass/tooltip)
(def lens-filter-defs glass/lens-filter-defs)
