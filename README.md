# Bug #2

In `TagsQuery` we generate some state:

```
   :initial-state (fn [_] (generate-tags 10000))})
```

Performance was measured as toggling between `English|Spanish` locale.

Changing number of tags (`10`, `100`, `1,000`, `10,000`) shows significant increase in UI lag when inspect is open, but not when page is loaded with inspect disabled.

* 1k tags

![1k call](screenshots/1k-call.jpg?raw=true)

![1k alloc](screenshots/1k-alloc.jpg?raw=true)

* 10k tags

![10k call](screenshots/10k-call.jpg?raw=true)

![10k alloc](screenshots/10k-alloc.jpg?raw=true)


# Bug #1

Calls `DataView/render`, even after `fulcro-inspect` is disabled
and component should have unmounted.

## Install dependencies

`[fulcrologic/fulcro-inspect "2.0.0-alpha6-PITHYLESS"]`

* Repo: https://github.com/pithyless/fulcro-inspect
* Branch: `avoid-render-when-inspect-is-inactive-logging`

## Compile main client

```
npm install
npx shadow-cljs watch main
```

## Open http://localhost:8020/ with devtools open.

Example session:

### Toggle `English|Spanish` locale

```
21:03:26.820 VISIBLE:  false
21:03:26.821 KeyListener WILL-UPDATE
21:03:26.840 KeyListener DID-UPDATE
21:04:00.934 VISIBLE:  false
21:04:00.935 KeyListener WILL-UPDATE
21:04:00.946 KeyListener DID-UPDATE
```

### Open inspect, `ctrl-F`

```
21:04:41.138 iFrame MOUNT
21:04:41.342 DataViewer/render
21:04:41.374 KeyListener MOUNT
21:04:41.528 DataViewer/render
21:04:41.551 KeyListener MOUNT
```

### Toggle `English|Spanish` locale

```
21:05:30.927 DataViewer/render
21:05:30.928 rendering data viewer 
<div class="fulcro_inspect_ui_data-viewer_DataViewer__container">

21:05:30.929 rendering data viewer 
21:05:30.930 rendering data viewer undefined

...
VISIBLE:  true
21:05:31.068 KeyListener WILL-UPDATE
21:05:31.080 KeyListener DID-UPDATE
21:05:31.208 KeyListener WILL-UPDATE

21:05:31.233 DataViewer/render
21:05:31.234 rendering data viewer 
<div class="fulcro_inspect_ui_data-viewer_DataViewer__container">

21:05:31.250 KeyListener DID-UPDATE
```

### Hide inspect, `ctrl-F`

```
21:07:58.496 VISIBLE:  false
21:07:58.497 KeyListener WILL-UPDATE
21:07:58.499 iFrame UN-MOUNT
21:07:58.500 KeyListener UN-MOUNT
21:07:58.507 KeyListener DID-UPDATE
```

### Keep clicking `English` locale - no change

```
21:08:52.516 VISIBLE:  false
21:08:52.517 KeyListener WILL-UPDATE
21:08:52.521 KeyListener DID-UPDATE

21:08:52.713 VISIBLE:  false
21:08:52.713 KeyListener WILL-UPDATE
21:08:52.717 KeyListener DID-UPDATE``
```

### Toggle between `English|Spanish` locale

BUG! DataViewer appears!

```
21:10:00.384 DataViewer/render
21:10:00.384 rendering data viewer 
<div class="fulcro_inspect_ui_data-viewer_DataViewer__container">

21:10:00.385 rendering data viewer 
21:10:00.385 rendering data viewer undefined

21:10:00.445 DataViewer/render
21:10:00.445 rendering data viewer 
<div class="fulcro_inspect_ui_data-viewer_DataViewer__container">

21:10:00.446 rendering data viewer 
21:10:00.447 rendering data viewer undefined

21:10:00.510 VISIBLE:  false
21:10:00.511 KeyListener WILL-UPDATE
21:10:00.520 KeyListener DID-UPDATE
```


# The Project

The main project source is in `src/main`.

```
.
├── Makefile           ; i18n extract/generate and CI test running
├── i18n               ; directory for i18n build/extract/translate
│   ├── es.po          ; spanish translations
│   └── messages.pot   ; extracted strings (template)
├── karma.conf.js      ; CI Runner config
├── package.json       ; NPM modules
├── project.clj        ; Leiningen project file
├── resources
│   └── public
│       ├── cards.html    ; page for mounting dev cards
│       ├── index.html    ; main app index page
│       └── js
│           └── test
│               └── index.html ; custom test page for running tests in dev mode
├── shadow-cljs.edn    ; Shadow-cljs configuration file. CLJS builds.
└── src
    ├── cards
    │   └── demo
    │       ├── cards.cljs   ; Main for devcards
    │       └── intro.cljs   ; A sample devcards file
    ├── dev
    │   └── user.clj         ; Functions for running web server in development mode
    ├── main
    │   ├── config           ; configuration files for web server
    │   │   ├── defaults.edn
    │   │   ├── dev.edn
    │   │   └── prod.edn
    │   ├── demo
    │   │   ├── api
    │   │   │   ├── mutations.clj          ; server-side implementation of mutations
    │   │   │   ├── mutations.cljs         ; client-side implementation of mutations
    │   │   │   └── read.clj               ; server-side reads
    │   │   ├── client.cljs                ; file that creates the Fulcro client
    │   │   ├── development-preload.cljs   ; code to run in development mode before anything else
    │   │   ├── server.clj                 ; file that creates the web server
    │   │   ├── server_main.clj            ; production server entry point
    │   │   └── ui
    │   │       ├── components.cljc  ; Sample reusable component
    │   │       └── root.cljc        ; Main UI
    │   └── translations
    │       └── es.cljc              ; Generated cljs for es translations (see Makefile)
    └── test
        └── demo
            ├── client_test_main.cljs  ; setup for dev mode tests
            └── sample_spec.cljc       ; a sample spec in fulcro-spec
```

## Setting Up

The shadow-cljs compiler uses all cljsjs and NPM js dependencies through
NPM. If you use a library that is in cljsjs you will also have to add
it to your `package.json`.

You also cannot compile this project until you install the ones it
depends on already:

```
$ npm install
```

or if you prefer `yarn`:

```
$ yarn install
```

Adding NPM Javascript libraries is as simple as adding them to your
`package.json` file and requiring them! See the
[the Shadow-cljs User's Guide](https://shadow-cljs.github.io/docs/UsersGuide.html#_javascript)
for more information.

## Development Mode

Shadow-cljs handles the client-side development build. The file
`src/main/demo/client.cljs` contains the code to start and refresh
the client for hot code reload.

Running all client development builds:

```
$ npx shadow-cljs watch main cards test
...
shadow-cljs - HTTP server for ":main" available at http://localhost:8020
shadow-cljs - HTTP server for ":test" available at http://localhost:8022
shadow-cljs - HTTP server for ":cards" available at http://localhost:8023
...
```

The compiler will detect which builds are affected by a change and will minimize
incremental build time.

NOTE: The server wil start a web server for all three builds (on different ports).
You typically do not need the one for main because you'll be running your
own server, but it is there in case you are only going to be writing
a client-side app that has no server API.

The URLs for working with cards and tests are:

- Cards: [http://localhost:8023/cards.html](http://localhost:8023/cards.html)
- Tests: [http://localhost:8022/index.html](http://localhost:8022/index.html)
- Main: [http://localhost:8020/index.html](http://localhost:8020/index.html) (NO API SERVER)

See the server section below for working on the full-stack app itself.

### Client REPL

The shadow-cljs compiler starts an nREPL. It is configured to start on
port 9000 (in `shadow-cljs.edn`).

In IntelliJ, simply add a *remote* Clojure REPL configuration with
host `localhost` and port `9000`.

If you're using CIDER
see [the Shadow-cljs User's Guide](https://shadow-cljs.github.io/docs/UsersGuide.html#_cider)
for more information.

### The API Server

The shadow-cljs compiler starts a server for serving development files,
but you usually will not use it. Instead you'll start your own server
that can also serve your application's API.

Start a clj REPL in IntelliJ, or from the command line:

```bash
$ lein repl
user=> (go)
...
user=> (restart) ; stop, reload server code, and go again
user=> (tools-ns/refresh) ; retry code reload if hot server reload fails
```

The URL to work on your application is then
[http://localhost:3000](http://localhost:3000).

Hot code reload, preloads, and such are all coded into the javascript,
so serving the files from the alternate server is fine.

### Preloads

There is a preload file that is used on the development build of the
application `demo.development-preload`. You can add code here that
you want to execute before the application initializes in development
mode.

### Fulcro Inspect

The Fulcro inspect will preload on the development build of the main
application and cards. You can activate it by pressing CTRL-F while in
the application. If you need a different keyboard shortcut (e.g. for
Windows) see the docs on github.

## Tests

Tests are in `src/test`

```
src/test
└── demo
    ├── client_test_main.cljs     entry point for dev-mode client tests
    └── sample_spec.cljs          spec runnable by client and server.
```

### Server tests:

Interacting with tests resuts via a browser (also allows test focusing, etc):

From a CLJ REPL:

```
user=> (start-server-tests) ; start a server on port 8888 showing the server tests
```

then navigate to [http://localhost:8888/fulcro-spec-server-tests.html](http://localhost:8888/fulcro-spec-server-tests.html)

If you'd instead like to see them pop up over and over again in a terminal:

```
lein test-refresh
```

### CI Tests

Use the Makefile target `tests`:

```
make test
```

You must have `npm` and Chrome installed. The tests use the `npm`
utility Karma for actually running the tests. This target will run
both client and server tests.

## Dev Cards

The source is in `src/cards`. Remember to add devcard files here, and add
a require the for new card namespace to the `cards.cljs` file.

## I18N

The i18n process is codified into the Makefile as two targets. The first extracts strings from
the source (which must build the js, and run xgettext on it, which you must
have installed, perhaps from brew):

```
make i18n-extract
```

and gives you instructions on generating translations.

The second takes the translations and generates a cljs namespace for
them:

```
make i18n-generate
```

## Standalone Runnable Jar (Production, with advanced optimized client js)

```
lein uberjar
java -jar target/demo.jar
```
