# kotoba-ui

`kotoba-ui` is the **default UI/UX design single require point** for
kotoba-lang frontends: `kotoba-ui.core` requires
[`shitsuke`](../shitsuke) (structure/dual-render contract) and
[`liquid-glass-ui`](../liquid-glass-ui) (Apple-Liquid-Glass-style material
skin) and re-exports their public API as one namespace. A product repo that
requires only `kotoba-ui.core` gets structure + material + all 29 glass
components in one `require`.

`kotoba-ui.core` owns **no logic of its own** — every var is a direct alias
(`(def button liquid-glass.components/button)`, …) of a shitsuke or
liquid-glass var. It is the thinnest possible composition layer, not a new
design system: see `docs/adr/0001-kotoba-ui.md` and the superproject decision
`90-docs/adr/2607022800-kotoba-lang-default-uiux-appkit-uikit-interface-fundamentals.md`.

## Not to be confused with `orgs/kotoba-lang/ui`

`orgs/kotoba-lang/ui` (package `kotoba.ui`) is a **pre-existing, unrelated**
repo: kami-engine's WebGPU game-canvas HUD overlay renderer (EDN spec → DOM
overlay, browser-only ClojureScript). It predates this repo, has its own
CI-green test suite, and is not touched or reused by `kotoba-ui`. The name
similarity is coincidental — `kotoba-ui` (this repo) is the default *design
system* entry point; `ui` (that repo) is a *game HUD* renderer.

## Usage

```clojure
(require '[kotoba-ui.core :as ui])

(def view
  (ui/panel [(ui/toolbar [(ui/icon-button "☰") (ui/badge "3")])
             (ui/tab-bar [[:visual "Visual"] [:edn "EDN"]] :visual)]
            {:surface :thick :elevation :floating}))

(ui/->html view)          ; SSR
;; once per page/app: (ui/inline-style) — root vars + component CSS
```

## Design

See `docs/design.md` for the Apple *Interface fundamentals* taxonomy this
repo's docs (and, downstream, `appkit`/`uikit`) are organized around, and
`docs/adr/0001-kotoba-ui.md` for the per-repo design record. Component-level
API detail lives in `liquid-glass-ui/docs/design.md` — `kotoba-ui.core` does
not duplicate it, only re-exports it.

## Tests

```bash
clojure -M:test            # published git shitsuke + liquid-glass-ui deps
clojure -M:local:test      # local ../shitsuke + ../liquid-glass-ui overrides
```
