# kotoba-ui — design

`kotoba-ui.core` is a pure alias layer (see `src/kotoba_ui/core.cljc`) — this
document is not a second API reference for shitsuke/liquid-glass-ui (that
lives in `liquid-glass-ui/docs/design.md` and `shitsuke/docs/design.md`).
Instead it records the **taxonomy** kotoba-lang's default design docs are
organized around, adopted from Apple's
[App design and UI / Interface fundamentals](https://developer.apple.com/documentation/technologyoverviews/app-design-and-ui)
per `90-docs/adr/2607022800-kotoba-lang-default-uiux-appkit-uikit-interface-fundamentals.md`.

## Interface fundamentals → kotoba-lang mapping

| Interface fundamentals category | kotoba-lang implementation | `kotoba-ui.core` entry points |
|---|---|---|
| Layout | `shitsuke.hiccup` structure + `liquid-glass.components` `panel`/`toolbar`/`nav-bar` | `panel`, `toolbar`, `nav-bar`, `->html` |
| Typography | `shitsuke.tokens` text tokens + `liquid-glass.tokens :ink` | `resolve-tokens`, `css-variables` |
| Color & Materials | `liquid-glass.tokens` (`:surface`/`:elevation`/`:specular`/`:accent`) — the Apple-Liquid-Glass-equivalent material system | `resolve-tokens`, `resolve-dark-tokens`, `root-css` |
| Navigation | `nav-bar`/`tab-bar`/`menu`/`sheet` | `nav-bar`, `tab-bar`, `menu`, `sheet` |
| Controls | `button`/`toggle`/`slider` and the rest of the 29-component catalog | `button`, `toggle`, `slider`, … (see `src/kotoba_ui/core.cljc`) |
| Motion | overlay enter/exit, spring settle, press morph (`liquid-glass-ui/docs/design.md` § "Motion & dynamic effects") | `component-css` (motion rules ship inside it — no separate motion API) |

## Platform bindings: appkit / uikit

`kotoba-ui` is the platform-agnostic entry point. Two sibling repos apply
screen-shape-specific defaults on top of it, mirroring Apple's UIKit
(touch)/AppKit (desktop) split against SwiftUI (declarative,
cross-platform) — `kotoba-ui` plays the SwiftUI-equivalent role here:

- **`appkit`** — desktop/dense-data defaults (`:surface :thick`/`:regular`,
  multi-pane layouts, tables).
- **`uikit`** — touch/mobile/card-first defaults (`:surface :clear`/`:regular`,
  single-column, `sheet`/`alert`-heavy).

Neither repo modifies `shitsuke`/`liquid-glass-ui`/`kotoba-ui` — they only
supply default option maps and layout conventions on top of the same
component catalog. See each repo's own `docs/design.md`.

## Review checklist for new components/tokens

When adding a component or token group anywhere in the
shitsuke → liquid-glass-ui → kotoba-ui → appkit/uikit stack, name which
Interface fundamentals category it belongs to (table above) before writing
code — it should map onto an existing row, or the taxonomy itself needs a
follow-up ADR to extend.
