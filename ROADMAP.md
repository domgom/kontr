# ROADMAP

## MUST HAVE
## NICE TO HAVE
rethink generator / postman generator structure so config options can flow from the frontend easier
openapi to rq{}
add gradle examples project
web package name, filename, env variable name and other options
web spinner on output pane
html encoding of uri and query params (http client responsibility?)
rework CLI to make it interactive and usable (like https://github.com/trietsch/spotify-cli)
add summary? (only if stopOnAssertionError == false?)
add System env var for secrets?
better Env support to have several objects and change from one another with ease
github cli version & publish
media types
non String types in headers
a test localhost server instead of typicode
more tests, reuse tests between different http clients
proxy support
optionally follow redirects
async client with coroutines
if request.body.options.language.json and not content-type, default to application/json? probably unnecessary

## DONE
✅non String response bodies (using types for rq/rs bodies), (not filetype): 
     changed request to return ResponseDsl that is parseable if kotlinx deserialisation is in the classpath, for convenience.  
     Other parsers like Gson or ObjectMapper can be implemented with extension functions ('mapped()'?) similar to the parsed() method.
✅print class and line of caller aliased functions so you can click on it with intellij
✅instead of nested classes, evaluate object with fun names made of path + name or another grouping way
✅investigate possible missing methods in postman collections
✅add postman v2.1 collection accepted format warning
✅LOGO!
✅proper github friendly readme
✅web disclaimer: no cookies, no saved data
✅web not write files in the server
✅web take current version for maven and gradle blocks. Also README.md
✅web filter only *.json files in select button
✅web fix wording and spacing of the left side
✅gha cli / web deployment / mvn central publishing
✅breaking String multiline arguments (weather api put date)
✅web version (render) to upload collection and generate kontr dsl
✅prism.js for code highlighting in the website
✅show DSL colouring syntax on png capture in the documentation
✅handcrafted postman collection with one example of each case
✅rework kontr-examples, it's super messy now 
✅refactored kotlinx deserialisation
✅path & uri variables
✅using default java client 
✅GET/POST/etc functions in addition of RQ 
✅more status checking functions 
✅response body assertions  (Json Unit)
✅Slf4j logger
✅postman collection to rq()
✅group multiple rq{} into rqs{}
✅fixed mvn cli
✅kotlin-cli
✅split postman generator in 2 separate pieces for collection parsing and code generation
✅add maven plugin or CLI interface for postman generator
✅better idiomatic generated classes
✅log function name ~~and arguments~~ instead of http request 
✅repeat/run block until
✅minimal README.md
✅kotlin-script assessed, not much value from kontr's POV
✅context for extension functions (not really necessary as we don't extend classes that aren't ours so namespace pollution is negligible)