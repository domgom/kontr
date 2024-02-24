# ROADMAP
This is just a scratch file to move features around until a stable 1.0.0 version

## MUST HAVE
mvn central publishing
web version (render?) to upload collection and generate kontr dsl
non String response bodies (using types for rq/rs bodies), (not filetype)

## NICE TO HAVE
html encoding of uri and query params (http client responsibility?)
openapi to rq{}
rework CLI to make it interactive and usable (like https://github.com/trietsch/spotify-cli)
kotlin-script
add summary? (only if stopOnAssertionError == false?)
add System env var for secrets?
better Env support to have several objects and change from one another with ease
proper github friendly readme and github publish
media types
non String types in headers
a test localhost server instead of typicode
more tests, reuse tests between different http clients
proxy support
follow redirects
async client with coroutines
context for extension functions (not really necessary as we don't extend classes that aren't ours so namespace pollution is negligible)
prism.js for code highlighting in the website

## DONE
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