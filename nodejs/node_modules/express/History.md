4.17.1 / 2019-05-25
===================

  * Revert "Improve error message for `null`/`undefined` to `res.status`"

4.17.0 / 2019-05-16
===================

  * Add `express.raw` to parse bodies into `Buffer`
  * Add `express.text` to parse bodies into string
  * Improve error message for non-strings to `res.sendFile`
  * Improve error message for `null`/`undefined` to `res.status`
  * Support multiple hosts in `X-Forwarded-Host`
  * deps: accepts@~1.3.7
  * deps: body-parser@1.19.0
    - Add encoding MIK
    - Add petabyte (`pb`) support
    - Fix parsing array brackets after index
    - deps: bytes@3.1.0
    - deps: http-errors@1.7.2
    - deps: iconv-lite@0.4.24
    - deps: qs@6.7.0
    - deps: raw-body@2.4.0
    - deps: type-is@~1.6.17
  * deps: content-disposition@0.5.3
  * deps: cookie@0.4.0
    - Add `SameSite=None` support
  * deps: finalhandler@~1.1.2
    - Set stricter `Content-Security-Policy` header
    - deps: parseurl@~1.3.3
    - deps: statuses@~1.5.0
  * deps: parseurl@~1.3.3
  * deps: proxy-addr@~2.0.5
    - deps: ipaddr.js@1.9.0
  * deps: qs@6.7.0
    - Fix parsing array brackets after index
  * deps: range-parser@~1.2.1
  * deps: send@0.17.1
    - Set stricter CSP header in redirect & error responses
    - deps: http-errors@~1.7.2
    - deps: mime@1.6.0
    - deps: ms@2.1.1
    - deps: range-parser@~1.2.1
    - deps: statuses@~1.5.0
    - perf: remove redundant `path.normalize` call
  * deps: serve-static@1.14.1
    - Set stricter CSP header in redirect response
    - deps: parseurl@~1.3.3
    - deps: send@0.17.1
  * deps: setprototypeof@1.1.1
  * deps: statuses@~1.5.0
    - Add `103 Early Hints`
  * deps: type-is@~1.6.18
    - deps: mime-types@~2.1.24
    - perf: prevent internal `throw` on invalid type

4.16.4 / 2018-10-10
===================

  * Fix issue where `"Request aborted"` may be logged in `res.sendfile`
  * Fix JSDoc for `Router` constructor
  * deps: body-parser@1.18.3
    - Fix deprecation warnings on Node.js 10+
    - Fix stack trace for strict json parse error
    - deps: depd@~1.1.2
    - deps: http-errors@~1.6.3
    - deps: iconv-lite@0.4.23
    - deps: qs@6.5.2
    - deps: raw-body@2.3.3
    - deps: type-is@~1.6.16
  * deps: proxy-addr@~2.0.4
    - deps: ipaddr.js@1.8.0
  * deps: qs@6.5.2
  * deps: safe-buffer@5.1.2

4.16.3 / 2018-03-12
===================

  * deps: accepts@~1.3.5
    - deps: mime-types@~2.1.18
  * deps: depd@~1.1.2
    - perf: remove argument reassignment
  * deps: encodeurl@~1.0.2
    - Fix encoding `%` as last character
  * deps: finalhandler@1.1.1
    - Fix 404 output for bad / missing pathnames
    - deps: encodeurl@~1.0.2
    - deps: statuses@~1.4.0
  * deps: proxy-addr@~2.0.3
    - deps: ipaddr.js@1.6.0
  * deps: send@0.16.2
    - Fix incorrect end tag in default error & redirects
    - deps: depd@~1.1.2
    - deps: encodeurl@~1.0.2
    - deps: statuses@~1.4.0
  * deps: serve-static@1.13.2
    - Fix incorrect end tag in redirects
    - deps: encodeurl@~1.0.2
    - deps: send@0.16.2
  * deps: statuses@~1.4.0
  * deps: type-is@~1.6.16
    - deps: mime-types@~2.1.18

4.16.2 / 2017-10-09
===================

  * Fix `TypeError` in `res.send` when given `Buffer` and `ETag` header set
  * perf: skip parsing of entire `X-Forwarded-Proto` header

4.16.1 / 2017-09-29
===================

  * deps: send@0.16.1
  * deps: serve-static@1.13.1
    - Fix regression when `root` is incorrectly set to a file
    - deps: send@0.16.1

4.16.0 / 2017-09-28
===================

  * Add `"json escape"` setting for `res.json` and `res.jsonp`
  * Add `express.json` and `express.urlencoded` to parse bodies
  * Add `options` argument to `res.download`
  * Improve error message when autoloading invalid view engine
  * Improve error messages when non-function provided as middleware
  * Skip `Buffer` encoding when not generating ETag for small response
  * Use `safe-buffer` for improved Buffer API
  * deps: accepts@~1.3.4
    - deps: mime-types@~2.1.16
  * deps: content-type@~1.0.4
    - perf: remove argument reassignment
    - perf: skip parameter parsing when no parameters
  * deps: etag@~1.8.1
    - perf: replace regular expression with substring
  * deps: finalhandler@1.1.0
    - Use `res.headersSent` when available
  * deps: parseurl@~1.3.2
    - perf: reduce overhead for full URLs
    - perf: unroll the "fast-path" `RegExp`
  * deps: proxy-addr@~2.0.2
    - Fix trimming leading / trailing OWS in `X-Forwarded-For`
    - deps: forwarded@~0.1.2
    - deps: ipaddr.js@1.5.2
    - perf: reduce overhead when no `X-Forwarded-For` header
  * deps: qs@6.5.1
    - Fix parsing & compacting very deep objects
  * deps: send@0.16.0
    - Add 70 new types for file extensions
    - Add `immutable` option
    - Fix missing `</html>` in default error & redirects
    - Set charset as "UTF-8" for .js and .json
    - Use instance methods on steam to check for listeners
    - deps: mime@1.4.1
    - perf: improve path validation speed
  * deps: serve-static@1.13.0
    - Add 70 new types for file extensions
    - Add `immutable` option
    - Set charset as "UTF-8" for .js and .json
    - deps: send@0.16.0
  * deps: setprototypeof@1.1.0
  * deps: utils-merge@1.0.1
  * deps: vary@~1.1.2
    - perf: improve header token parsing speed
  * perf: re-use options object when generating ETags
  * perf: remove dead `.charset` set in `res.jsonp`

4.15.5 / 2017-09-24
===================

  * deps: debug@2.6.9
  * deps: finalhandler@~1.0.6
    - deps: debug@2.6.9
    - deps: parseurl@~1.3.2
  * deps: fresh@0.5.2
    - Fix handling of modified headers with invalid dates
    - perf: improve ETag match loop
    - perf: improve `If-None-Match` token parsing
  * deps: send@0.15.6
    - Fix handling of modified headers with invalid dates
    - deps: debug@2.6.9
    - deps: etag@~1.8.1
    - deps: fresh@0.5.2
    - perf: improve `If-Match` token parsing
  * deps: serve-static@1.12.6
    - deps: parseurl@~1.3.2
    - deps: send@0.15.6
    - perf: improve slash collapsing

4.15.4 / 2017-08-06
===================

  * deps: debug@2.6.8
  * deps: depd@~1.1.1
    - Remove unnecessary `Buffer` loading
  * deps: finalhandler@~1.0.4
    - deps: debug@2.6.8
  * deps: proxy-addr@~1.1.5
    - Fix array argument being altered
    - deps: ipaddr.js@1.4.0
  * deps: qs@6.5.0
  * deps: send@0.15.4
    - deps: debug@2.6.8
    - deps: depd@~1.1.1
    - deps: http-errors@~1.6.2
  * deps: serve-static@1.12.4
    - deps: send@0.15.4

4.15.3 / 2017-05-16
===================

  * Fix error when `res.set` cannot add charset to `Content-Type`
  * deps: debug@2.6.7
    - Fix `DEBUG_MAX_ARRAY_LENGTH`
    - deps: ms@2.0.0
  * deps: finalhandler@~1.0.3
    - Fix missing `</html>` in HTML document
    - deps: debug@2.6.7
  * deps: proxy-addr@~1.1.4
    - deps: ipaddr.js@1.3.0
  * deps: send@0.15.3
    - deps: debug@2.6.7
    - deps: ms@2.0.0
  * deps: serve-static@1.12.3
    - deps: send@0.15.3
  * deps: type-is@~1.6.15
    - deps: mime-types@~2.1.15
  * deps: vary@~1.1.1
    - perf: hoist regular expression

4.15.2 / 2017-03-06
===================

  * deps: qs@6.4.0
    - Fix regression parsing keys starting with `[`

4.15.1 / 2017-03-05
===================

  * deps: send@0.15.1
    - Fix issue when `Date.parse` does not return `NaN` on invalid date
    - Fix strict violation in broken environments
  * deps: serve-static@1.12.1
    - Fix issue when `Date.parse` does not return `NaN` on invalid date
    - deps: send@0.15.1

4.15.0 / 2017-03-01
===================

  * Add debug message when loading view engine
  * Add `next("router")` to exit from router
  * Fix case where `router.use` skipped requests routes did not
  * Remove usage of `res._headers` private field
    - Improves compatibility with Node.js 8 nightly
  * Skip routing when `req.url` is not set
  * Use `%o` in path debug to tell types apart
  * Use `Object.create` to setup request & response prototypes
  * Use `setprototypeof` module to replace `__proto__` setting
  * Use `statuses` instead of `http` module for status messages
  * deps: debug@2.6.1
    - Allow colors in workers
    - Deprecated `DEBUG_FD` environment variable set to `3` or higher
    - Fix error when running under React Native
    - Use same color for same namespace
    - deps: ms@0.7.2
  * deps: etag@~1.8.0
    - Use SHA1 instead of MD5 for ETag hashing
    - Works with FIPS 140-2 OpenSSL configuration
  * deps: finalhandler@~1.0.0
    - Fix exception when `err` cannot be converted to a string
    - Fully URL-encode the pathname in the 404
    - Only include the pathname in the 404 message
    - Send complete HTML document
    - Set `Content-Security-Policy: default-src 'self'` header
    - deps: debug@2.6.1
  * deps: fresh@0.5.0
    - Fix false detection of `no-cache` request directive
    - Fix incorrect result when `If-None-Match` has both `*` and ETags
    - Fix weak `ETag` matching to match spec
    - perf: delay reading header values until needed
    - perf: enable strict mode
    - perf: hoist regular expressions
    - perf: remove duplicate conditional
    - perf: remove unnecessary boolean coercions
    - perf: skip checking modified time if ETag check failed
    - perf: skip parsing `If-None-Match` when no `ETag` header
    - perf: use `Date.parse` instead of `new Date`
  * deps: qs@6.3.1
    - Fix array parsing from skipping empty values
    - Fix compacting nested arrays
  * deps: send@0.15.0
    - Fix false detection of `no-cache` request directive
    - Fix incorrect result when `If-None-Match` has both `*` and ETags
    - Fix weak `ETag` matching to match spec
    - Remove usage of `res._headers` private field
    - Support `If-Match` and `If-Unmodified-Since` headers
    - Use `res.getHeaderNames()` when available
    - Use `res.headersSent` when available
    - deps: debug@2.6.1
    - deps: etag@~1.8.0
    - deps: fresh@0.5.0
    - deps: http-errors@~1.6.1
  * deps: serve-static@1.12.0
    - Fix false detection of `no-cache` request directive
    - Fix incorrect result when `If-None-Match` has both `*` and ETags
    - Fix weak `ETag` matching to match spec
    - Remove usage of `res._headers` private field
    - Send complete HTML document in redirect response
    - Set default CSP header in redirect response
    - Support `If-Match` and `If-Unmodified-Since` headers
    - Use `res.getHeaderNames()` when available
    - Use `res.headersSent` when available
    - deps: send@0.15.0
  * perf: add fast match path for `*` route
  * perf: improve `req.ips` performance

4.14.1 / 2017-01-28
===================

  * deps: content-disposition@0.5.2
  * deps: finalhandler@0.5.1
    - Fix exception when `err.headers` is not an object
    - deps: statuses@~1.3.1
    - perf: hoist regular expressions
    - perf: remove duplicate validation path
  * deps: proxy-addr@~1.1.3
    - deps: ipaddr.js@1.2.0
  * deps: send@0.14.2
    - deps: http-errors@~1.5.1
    - deps: ms@0.7.2
    - deps: statuses@~1.3.1
  * deps: serve-static@~1.11.2
    - deps: send@0.14.2
  * deps: type-is@~1.6.14
    - deps: mime-types@~2.1.13

4.14.0 / 2016-06-16
===================

  * Add `acceptRanges` option to `res.sendFile`/`res.sendfile`
  * Add `cacheControl` option to `res.sendFile`/`res.sendfile`
  * Add `options` argument to `req.range`
    - Includes the `combine` option
  * Encode URL in `res.location`/`res.redirect` if not already encoded
  * Fix some redirect handling in `res.sendFile`/`res.sendfile`
  * Fix Windows absolute path check using forward slashes
  * Improve error with invalid arguments to `req.get()`
  * Improve performance for `res.json`/`res.jsonp` in most cases
  * Improve `Range` header handling in `res.sendFile`/`res.sendfile`
  * deps: accepts@~1.3.3
    - Fix including type extensions in parameters in `Accept` parsing
    - Fix parsing `Accept` parameters with quoted equals
    - Fix parsing `Accept` parameters with quoted semicolons
    - Many performance improvements
    - deps: mime-types@~2.1.11
    - deps: negotiator@0.6.1
  * deps: content-type@~1.0.2
    - perf: enable strict mode
  * deps: cookie@0.3.1
    - Add `sameSite` option
    - Fix cookie `Max-Age` to never be a floating point number
    - Improve error message when `encode` is not a function
    - Improve error message when `expires` is not a `Date`
    - Throw better error for invalid argument to parse
    - Throw on invalid values provided to `serialize`
    - perf: enable strict mode
    - perf: hoist regular expression
    - perf: use for loop in parse
    - perf: use string concatenation for serialization
  * deps: finalhandler@0.5.0
    - Change invalid or non-numeric status code to 500
    - Overwrite status message to match set status code
    - Prefer `err.statusCode` if `err.status` is invalid
    - Set response headers from `err.headers` object
    - Use `statuses` instead of `http` module for status messages
  * deps: proxy-addr@~1.1.2
    - Fix accepting various invalid netmasks
    - Fix IPv6-mapped IPv4 validation edge cases
    - IPv4 netmasks must be contiguous
    - IPv6 addresses cannot be used as a netmask
    - deps: ipaddr.js@1.1.1
  * deps: qs@6.2.0
    - Add `decoder` option in `parse` function
  * deps: range-parser@~1.2.0
    - Add `combine` option to combine overlapping ranges
    - Fix incorrectly returning -1 when there is at least one valid range
    - perf: remove internal function
  * deps: send@0.14.1
    - Add `acceptRanges` option
    - Add `cacheControl` option
    - Attempt to combine multiple ranges into single range
    - Correctly inherit from `Stream` class
    - Fix `Content-Range` header in 416 responses when using `start`/`end` options
    - Fix `Content-Range` header missing from default 416 responses
    - Fix redirect error when `path` contains raw non-URL characters
    - Fix redirect when `path` starts with multiple forward slashes
    - Ignore non-byte `Range` headers
    - deps: http-errors@~1.5.0
    - deps: range-parser@~1.2.0
    - deps: statuses@~1.3.0
    - perf: remove argument reassignment
  * deps: serve-static@~1.11.1
    - Add `acceptRanges` option
    - Add `cacheControl` option
    - Attempt to combine multiple ranges into single range
    - Fix redirect error when `req.url` contains raw non-URL characters
    - Ignore non-byte `Range` headers
    - Use status code 301 for redirects
    - deps: send@0.14.1
  * deps: type-is@~1.6.13
    - Fix type error when given invalid type to match against
    - deps: mime-types@~2.1.11
  * deps: vary@~1.1.0
    - Only accept valid field names in the `field` argument
  * perf: use strict equality when possible

4.13.4 / 2016-01-21
===================

  * deps: content-disposition@0.5.1
    - perf: enable strict mode
  * deps: cookie@0.1.5
    - Throw on invalid values provided to `serialize`
  * deps: depd@~1.1.0
    - Support web browser loading
    - perf: enable strict mode
  * deps: escape-html@~1.0.3
    - perf: enable strict mode
    - perf: optimize string replacement
    - perf: use faster string coercion
  * deps: finalhandler@0.4.1
    - deps: escape-html@~1.0.3
  * deps: merge-descriptors@1.0.1
    - perf: enable strict mode
  * deps: methods@~1.1.2
    - perf: enable strict mode
  * deps: parseurl@~1.3.1
    - perf: enable strict mode
  * deps: proxy-addr@~1.0.10
    - deps: ipaddr.js@1.0.5
    - perf: enable strict mode
  * deps: range-parser@~1.0.3
    - perf: enable strict mode
  * deps: send@0.13.1
    - deps: depd@~1.1.0
    - deps: destroy@~1.0.4
    - deps: escape-html@~1.0.3
    - deps: range-parser@~1.0.3
  * deps: serve-static@~1.10.2
    - deps: escape-html@~1.0.3
    - deps: parseurl@~1.3.0
    - deps: send@0.13.1

4.13.3 / 2015-08-02
===================

  * Fix infinite loop condition using `mergeParams: true`
  * Fix inner numeric indices incorrectly altering parent `req.params`

4.13.2 / 2015-07-31
===================

  * deps: accepts@~1.2.12
    - deps: mime-types@~2.1.4
  * deps: array-flatten@1.1.1
    - perf: enable strict mode
  * deps: path-to-regexp@0.1.7
    - Fix regression with escaped round brackets and matching groups
  * deps: type-is@~1.6.6
    - deps: mime-types@~2.1.4

4.13.1 / 2015-07-05
===================

  * deps: accepts@~1.2.10
    - deps: mime-types@~2.1.2
  * deps: qs@4.0.0
    - Fix dropping parameters like `hasOwnProperty`
    - Fix various parsing edge cases
  * deps: type-is@~1.6.4
    - deps: mime-types@~2.1.2
    - perf: enable strict mode
    - perf: remove argument reassignment

4.13.0 / 2015-06-20
===================

  * Add settings to debug output
  * Fix `res.format` error when only `default` provided
  * Fix issue where `next('route')` in `app.param` would incorrectly skip values
  * Fix hiding platform issues with `decodeURIComponent`
    - Only `URIError`s are a 400
  * Fix using `*` before params in routes
  * Fix using capture groups before params in routes
  * Simplify `res.cookie` to call `res.append`
  * Use `array-flatten` module for flattening arrays
  * deps: accepts@~1.2.9
    - deps: mime-types@~2.1.1
    - perf: avoid argument reassignment & argument slice
    - perf: avoid negotiator recursive construction
    - perf: enable strict mode
    - perf: remove unnecessary bitwise operator
  * deps: cookie@0.1.3
    - perf: deduce the scope of try-catch deopt
    - perf: remove argument reassignments
  * deps: escape-html@1.0.2
  * deps: etag@~1.7.0
    - Always include entity length in ETags for hash length extensions
    - Generate non-Stats ETags using MD5 only (no longer CRC32)
    - Improve stat performance by removing hashing
    - Improve support for JXcore
    - Remove base64 padding in ETags to shorten
    - Support "fake" stats objects in environments without fs
    - Use MD5 instead of MD4 in weak ETags over 1KB
  * deps: finalhandler@0.4.0
    - Fix a false-positive when unpiping in Node.js 0.8
    - Support `statusCode` property on `Error` objects
    - Use `unpipe` module for unpiping requests
    - deps: escape-html@1.0.2
    - deps: on-finished@~2.3.0
    - perf: enable strict mode
    - perf: remove argument reassignment
  * deps: fresh@0.3.0
    - Add weak `ETag` matching support
  * deps: on-finished@~2.3.0
    - Add defined behavior for HTTP `CONNECT` requests
    - Add defined behavior for HTTP `Upgrade` requests
    - deps: ee-first@1.1.1
  * deps: path-to-regexp@0.1.6
  * deps: send@0.13.0
    - Allow Node.js HTTP server to set `Date` response header
    - Fix incorrectly removing `Content-Location` on 304 response
    - Improve the default redirect response headers
    - Send appropriate headers on default error response
    - Use `http-errors` for standard emitted errors
    - Use `statuses` instead of `http` module for status messages
    - deps: escape-html@1.0.2
    - deps: etag@~1.7.0
    - deps: fresh@0.3.0
    - deps: on-finished@~2.3.0
    - perf: enable strict mode
    - perf: remove unnecessary array allocations
  * deps: serve-static@~1.10.0
    - Add `fallthrough` option
    - Fix reading options from options prototype
    - Improve the default redirect response headers
    - Malformed URLs now `next()` instead of 400
    - deps: escape-html@1.0.2
    - deps: send@0.13.0
    - perf: enable strict mode
    - perf: remove argument reassignment
  * deps: type-is@~1.6.3
    - deps: mime-types@~2.1.1
    - perf: reduce try block size
    - perf: remove bitwise operations
  * perf: enable strict mode
  * perf: isolate `app.render` try block
  * perf: remove argument reassignments in application
  * perf: remove argument reassignments in request prototype
  * perf: remove argument reassignments in response prototype
  * perf: remove argument reassignments in routing
  * perf: remove argument reassignments in `View`
  * perf: skip attempting to decode zero length string
  * perf: use saved reference to `http.STATUS_CODES`

4.12.4 / 2015-05-17
===================

  * deps: accepts@~1.2.7
    - deps: mime-types@~2.0.11
    - deps: negotiator@0.5.3
  * deps: debug@~2.2.0
    - deps: ms@0.7.1
  * deps: depd@~1.0.1
  * deps: etag@~1.6.0
    - Improve support for JXcore
    - Support "fake" stats objects in environments without `fs`
  * deps: finalhandler@0.3.6
    - deps: debug@~2.2.0
    - deps: on-finished@~2.2.1
  * deps: on-finished@~2.2.1
    - Fix `isFinished(req)` when data buffered
  * deps: proxy-addr@~1.0.8
    - deps: ipaddr.js@1.0.1
  * deps: qs@2.4.2
   - Fix allowing parameters like `constructor`
  * deps: send@0.12.3
    - deps: debug@~2.2.0
    - deps: depd@~1.0.1
    - deps: etag@~1.6.0
    - deps: ms@0.7.1
    - deps: on-finished@~2.2.1
  * deps: serve-static@~1.9.3
    - deps: send@0.12.3
  * deps: type-is@~1.6.2
    - deps: mime-types@~2.0.11

4.12.3 / 2015-03-17
===================

  * deps: accepts@~1.2.5
    - deps: mime-types@~2.0.10
  * deps: debug@~2.1.3
    - Fix high intensity foreground color for bold
    - deps: ms@0.7.0
  * deps: finalhandler@0.3.4
    - deps: debug@~2.1.3
  * deps: proxy-addr@~1.0.7
    - deps: ipaddr.js@0.1.9
  * deps: qs@2.4.1
    - Fix error when parameter `hasOwnProperty` is present
  * deps: send@0.12.2
    - Throw errors early for invalid `extensions` or `index` options
    - deps: debug@~2.1.3
  * deps: serve-static@~1.9.2
    - deps: send@0.12.2
  * deps: type-is@~1.6.1
    - deps: mime-types@~2.0.10

4.12.2 / 2015-03-02
===================

  * Fix regression where `"Request aborted"` is logged using `res.sendFile`

4.12.1 / 2015-03-01
===================

  * Fix constructing application with non-configurable prototype properties
  * Fix `ECONNRESET` errors from `res.sendFile` usage
  * Fix `req.host` when using "trust proxy" hops count
  * Fix `req.protocol`/`req.secure` when using "trust proxy" hops count
  * Fix wrong `code` on aborted connections from `res.sendFile`
  * deps: merge-descriptors@1.0.0

4.12.0 / 2015-02-23
===================

  * Fix `"trust proxy"` setting to inherit when app is mounted
  * Generate `ETag`s for all request responses
    - No longer restricted to only responses for `GET` and `HEAD` requests
  * Use `content-type` to parse `Content-Type` headers
  * deps: accepts@~1.2.4
    - Fix preference sorting to be stable for long acceptable lists
    - deps: mime-types@~2.0.9
    - deps: negotiator@0.5.1
  * deps: cookie-signature@1.0.6
  * deps: send@0.12.1
    - Always read the stat size from the file
    - Fix mutating passed-in `options`
    - deps: mime@1.3.4
  * deps: serve-static@~1.9.1
    - deps: send@0.12.1
  * deps: type-is@~1.6.0
    - fix argument reassignment
    - fix false-positives in `hasBody` `Transfer-Encoding` check
    - support wildcard for both type and subtype (`*/*`)
    - deps: mime-types@~2.0.9

4.11.2 / 2015-02-01
===================

  * Fix `res.redirect` double-calling `res.end` for `HEAD` requests
  * deps: accepts@~1.2.3
    - deps: mime-types@~2.0.8
  * deps: proxy-addr@~1.0.6
    - deps: ipaddr.js@0.1.8
  * deps: type-is@~1.5.6
    - deps: mime-types@~2.0.8

4.11.1 / 2015-01-20
===================

  * deps: send@0.11.1
    - Fix root path disclosure
  * deps: serve-static@~1.8.1
    - Fix redirect loop in Node.js 0.11.14
    - Fix root path disclosure
    - deps: send@0.11.1

4.11.0 / 2015-01-13
===================

  * Add `res.append(field, val)` to append headers
  * Deprecate leading `:` in `name` for `app.param(name, fn)`
  * Deprecate `req.param()` -- use `req.params`, `req.body`, or `req.query` instead
  * Deprecate `app.param(fn)`
  * Fix `OPTIONS` responses to include the `HEAD` method properly
  * Fix `res.sendFile` not always detecting aborted connection
  * Match routes iteratively to prevent stack overflows
  * deps: accepts@~1.2.2
    - deps: mime-types@~2.0.7
    - deps: negotiator@0.5.0
  * deps: send@0.11.0
    - deps: debug@~2.1.1
    - deps: etag@~1.5.1
    - deps: ms@0.7.0
    - deps: on-finished@~2.2.0
  * deps: serve-static@~1.8.0
    - deps: send@0.11.0

4.10.8 / 2015-01-13
===================

  * Fix crash from error within `OPTIONS` response handler
  * deps: proxy-addr@~1.0.5
    - deps: ipaddr.js@0.1.6

4.10.7 / 2015-01-04
===================

  * Fix `Allow` header for `OPTIONS` to not contain duplicate methods
  * Fix incorrect "Request aborted" for `res.sendFile` when `HEAD` or 304
  * deps: debug@~2.1.1
  * deps: finalhandler@0.3.3
    - deps: debug@~2.1.1
    - deps: on-finished@~2.2.0
  * deps: methods@~1.1.1
  * deps: on-finished@~2.2.0
  * deps: serve-static@~1.7.2
    - Fix potential open redirect when mounted at root
  * deps: type-is@~1.5.5
    - deps: mime-types@~2.0.7

4.10.6 / 2014-12-12
===================

  * Fix exception in `req.fresh`/`req.stale` without response headers

4.10.5 / 2014-12-10
===================

  * Fix `res.send` double-calling `res.end` for `HEAD` requests
  * deps: accepts@~1.1.4
    - deps: mime-types@~2.0.4
  * deps: type-is@~1.5.4
    - deps: mime-types@~2.0.4

4.10.4 / 2014-11-24
===================

  * Fix `res.sendfile` logging standard write errors

4.10.3 / 2014-11-23
===================

  * Fix `res.sendFile` logging standard write errors
  * deps: etag@~1.5.1
  * deps: proxy-addr@~1.0.4
    - deps: ipaddr.js@0.1.5
  * deps: qs@2.3.3
    - Fix `arrayLimit` behavior

4.10.2 / 2014-11-09
===================

  * Correctly invoke async router callback asynchronously
  * deps: accepts@~1.1.3
    - deps: mime-types@~2.0.3
  * deps: type-is@~1.5.3
    - deps: mime-types@~2.0.3

4.10.1 / 2014-10-28
===================

  * Fix handling of URLs containing `://` in the path
  * deps: qs@2.3.2
    - Fix parsing of mixed objects and values

4.10.0 / 2014-10-23
===================

  * Add support for `app.set('views', array)`
    - Views are looked up in sequence in array of directories
  * Fix `res.send(status)` to mention `res.sendStatus(status)`
  * Fix handling of invalid empty URLs
  * Use `content-disposition` module for `res.attachment`/`res.download`
    - Sends standards-compliant `Content-Disposition` header
    - Full Unicode support
  * Use `path.resolve` in view lookup
  * deps: debug@~2.1.0
    - Implement `DEBUG_FD` env variable support
  * deps: depd@~1.0.0
  * deps: etag@~1.5.0
    - Improve string performance
    - Slightly improve speed for weak ETags over 1KB
  * deps: finalhandler@0.3.2
    - Terminate in progress response only on error
    - Use `on-finished` to determine request status
    - deps: debug@~2.1.0
    - deps: on-finished@~2.1.1
  * deps: on-finished@~2.1.1
    - Fix handling of pipelined requests
  * deps: qs@2.3.0
    - Fix parsing of mixed implicit and explicit arrays
  * deps: send@0.10.1
    - deps: debug@~2.1.0
    - deps: depd@~1.0.0
    - deps: etag@~1.5.0
    - deps: on-finished@~2.1.1
  * deps: serve-static@~1.7.1
    - deps: send@0.10.1

4.9.8 / 2014-10-17
==================

  * Fix `res.redirect` body when redirect status specified
  * deps: accepts@~1.1.2
    - Fix error when media type has invalid parameter
    - deps: negotiator@0.4.9

4.9.7 / 2014-10-10
==================

  * Fix using same param name in array of paths

4.9.6 / 2014-10-08
==================

  * deps: accepts@~1.1.1
    - deps: mime-types@~2.0.2
    - deps: negotiator@0.4.8
  * deps: serve-static@~1.6.4
    - Fix redirect loop when index file serving disabled
  * deps: type-is@~1.5.2
    - deps: mime-types@~2.0.2

4.9.5 / 2014-09-24
==================

  * deps: etag@~1.4.0
  * deps: proxy-addr@~1.0.3
    - Use `forwarded` npm module
  * deps: send@0.9.3
    - deps: etag@~1.4.0
  * deps: serve-static@~1.6.3
    - deps: send@0.9.3

4.9.4 / 2014-09-19
==================

  * deps: qs@2.2.4
    - Fix issue with object keys starting with numbers truncated

4.9.3 / 2014-09-18
==================

  * deps: proxy-addr@~1.0.2
    - Fix a global leak when multiple subnets are trusted
    - deps: ipaddr.js@0.1.3

4.9.2 / 2014-09-17
==================

  * Fix regression for empty string `path` in `app.use`
  * Fix `router.use` to accept array of middleware without path
  * Improve error message for bad `app.use` arguments

4.9.1 / 2014-09-16
==================

  * Fix `app.use` to accept array of middleware without path
  * deps: depd@0.4.5
  * deps: etag@~1.3.1
  * deps: send@0.9.2
    - deps: depd@0.4.5
    - deps: etag@~1.3.1
    - deps: range-parser@~1.0.2
  * deps: serve-static@~1.6.2
    - deps: send@0.9.2

4.9.0 / 2014-09-08
==================

  * Add `res.sendStatus`
  * Invoke callback for sendfile when client aborts
    - Applies to `res.sendFile`, `res.sendfile`, and `res.download`
    - `err` will be populated with request aborted error
  * Support IP address host in `req.subdomains`
  * Use `etag` to generate `ETag` headers
  * deps: accepts@~1.1.0
    - update `mime-types`
  * deps: cookie-signature@1.0.5
  * deps: debug@~2.0.0
  * deps: finalhandler@0.2.0
    - Set `X-Content-Type-Options: nosniff` header
    - deps: debug@~2.0.0
  * deps: fresh@0.2.4
  * deps: media-typer@0.3.0
    - Throw error when parameter format invalid on parse
  * deps: qs@2.2.3
    - Fix issue where first empty value in array is discarded
  * deps: range-parser@~1.0.2
  * deps: send@0.9.1
    - Add `lastModified` option
    - Use `etag` to generate `ETag` header
    - deps: debug@~2.0.0
    - deps: fresh@0.2.4
  * deps: serve-static@~1.6.1
    - Add `lastModified` option
    - deps: send@0.9.1
  * deps: type-is@~1.5.1
    - fix `hasbody` to be true for `content-length: 0`
    - deps: media-typer@0.3.0
    - deps: mime-types@~2.0.1
  * deps: vary@~1.0.0
    - Accept valid `Vary` header string as `field`

4.8.8 / 2014-09-04
==================

  * deps: send@0.8.5
    - Fix a path traversal issue when using `root`
    - Fix malicious path detection for empty string path
  * deps: serve-static@~1.5.4
    - deps: send@0.8.5

4.8.7 / 2014-08-29
==================

  * deps: qs@2.2.2
    - Remove unnecessary cloning

4.8.6 / 2014-08-27
==================

  * deps: qs@2.2.0
    - Array parsing fix
    - Performance improvements

4.8.5 / 2014-08-18
==================

  * deps: send@0.8.3
    - deps: destroy@1.0.3
    - deps: on-finished@2.1.0
  * deps: serve-static@~1.5.3
    - deps: send@0.8.3

4.8.4 / 2014-08-14
==================

  * deps: qs@1.2.2
  * deps: send@0.8.2
    - Work around `fd` leak in Node.js 0.10 for `fs.ReadStream`
  * deps: serve-static@~1.5.2
    - deps: send@0.8.2

4.8.3 / 2014-08-10
==================

  * deps: parseurl@~1.3.0
  * deps: qs@1.2.1
  * deps: serve-static@~1.5.1
    - Fix parsing of weird `req.originalUrl` values
    - deps: parseurl@~1.3.0
    - deps: utils-merge@1.0.0

4.8.2 / 2014-08-07
==================

  * deps: qs@1.2.0
    - Fix parsing array of objects

4.8.1 / 2014-08-06
==================

  * fix incorrect deprecation warnings on `res.download`
  * deps: qs@1.1.0
    - Accept urlencoded square brackets
    - Accept empty values in implicit array notation

4.8.0 / 2014-08-05
==================

  * add `res.sendFile`
    - accepts a file system path instead of a URL
    - requires an absolute path or `root` option specified
  * deprecate `res.sendfile` -- use `res.sendFile` instead
  * support mounted app as any argument to `app.use()`
  * deps: qs@1.0.2
    - Complete rewrite
    - Limits array length to 20
    - Limits object depth to 5
    - Limits parameters to 1,000
  * deps: send@0.8.1
    - Add `extensions` option
  * deps: serve-static@~1.5.0
    - Add `extensions` option
    - deps: send@0.8.1

4.7.4 / 2014-08-04
==================

  * fix `res.sendfile` regression for serving directory index files
  * deps: send@0.7.4
    - Fix incorrect 403 on Windows and Node.js 0.11
    - Fix serving index files without root dir
  * deps: serve-static@~1.4.4
    - deps: send@0.7.4

4.7.3 / 2014-08-04
==================

  * deps: send@0.7.3
    - Fix incorrect 403 on Windows and Node.js 0.11
  * deps: serve-static@~1.4.3
    - Fix incorrect 403 on Windows and Node.js 0.11
    - deps: send@0.7.3

4.7.2 / 2014-07-27
==================

  * deps: depd@0.4.4
    - Work-around v8 generating empty stack traces
  * deps: send@0.7.2
    - deps: depd@0.4.4
  * deps: serve-static@~1.4.2

4.7.1 / 2014-07-26
==================

  * deps: depd@0.4.3
    - Fix exception when global `Error.stackTraceLimit` is too low
  * deps: send@0.7.1
    - deps: depd@0.4.3
  * deps: serve-static@~1.4.1

4.7.0 / 2014-07-25
==================

  * fix `req.protocol` for proxy-direct connections
  * configurable query parser with `app.set('query parser', parser)`
    - `app.set('query parser', 'extended')` parse with "qs" module
    - `app.set('query parser', 'simple')` parse with "querystring" core module
    - `app.set('query parser', false)` disable query string parsing
    - `app.set('query parser', true)` enable simple parsing
  * deprecate `res.json(status, obj)` -- use `res.status(status).json(obj)` instead
  * deprecate `res.jsonp(status, obj)` -- use `res.status(status).jsonp(obj)` instead
  * deprecate `res.send(status, body)` -- use `res.status(status).send(body)` instead
  * deps: debug@1.0.4
  * deps: depd@0.4.2
    - Add `TRACE_DEPRECATION` environment variable
    - Remove non-standard grey color from color output
    - Support `--no-deprecation` argument
    - Support `--trace-deprecation` argument
  * deps: finalhandler@0.1.0
    - Respond after request fully read
    - deps: debug@1.0.4
  * deps: parseurl@~1.2.0
    - Cache URLs based on original value
    - Remove no-longer-needed URL mis-parse work-around
    - Simplify the "fast-path" `RegExp`
  * deps: send@0.7.0
    - Add `dotfiles` option
    - Cap `maxAge` value to 1 year
    - deps: debug@1.0.4
    - deps: depd@0.4.2
  * deps: serve-static@~1.4.0
    - deps: parseurl@~1.2.0
    - deps: send@0.7.0
  * perf: prevent multiple `Buffer` creation in `res.send`

4.6.1 / 2014-07-12
==================

  * fix `subapp.mountpath` regression for `app.use(subapp)`

4.6.0 / 2014-07-11
==================

  * accept multiple callbacks to `app.use()`
  * add explicit "Rosetta Flash JSONP abuse" protection
    - previous versions are not vulnerable; this is just explicit protection
  * catch errors in multiple `req.param(name, fn)` handlers
  * deprecate `res.redirect(url, status)` -- use `res.redirect(status, url)` instead
  * fix `res.send(status, num)` to send `num` as json (not error)
  * remove unnecessary escaping when `res.jsonp` returns JSON response
  * support non-string `path` in `app.use(path, fn)`
    - supports array of paths
    - supports `RegExp`
  * router: fix optimization on router exit
  * router: refactor location of `try` blocks
  * router: speed up standard `app.use(fn)`
  * deps: debug@1.0.3
    - Add support for multiple wildcards in namespaces
  * deps: finalhandler@0.0.3
    - deps: debug@1.0.3
  * deps: methods@1.1.0
    - add `CONNECT`
  * deps: parseurl@~1.1.3
    - faster parsing of href-only URLs
  * deps: path-to-regexp@0.1.3
  * deps: send@0.6.0
    - deps: debug@1.0.3
  * deps: serve-static@~1.3.2
    - deps: parseurl@~1.1.3
    - deps: send@0.6.0
  * perf: fix arguments reassign deopt in some `res` methods

4.5.1 / 2014-07-06
==================

 * fix routing regression when altering `req.method`

4.5.0 / 2014-07-04
==================

 * add deprecation message to non-plural `req.accepts*`
 * add deprecation message to `res.send(body, status)`
 * add deprecation message to `res.vary()`
 * add `headers` option to `res.sendfile`
   - use to set headers on successful file transfer
 * add `mergeParams` option to `Router`
   - merges `req.params` from parent routes
 * add `req.hostname` -- correct name for what `req.host` returns
 * deprecate things with `depd` module
 * deprecate `req.host` -- use `req.hostname` instead
 * fix behavior when handling request without routes
 * fix handling when `route.all` is only route
 * invoke `router.param()` only when route matches
 * restore `req.params` after invoking router
 * use `finalhandler` for final response handling
 * use `media-typer` to alter content-type charset
 * deps: accepts@~1.0.7
 * deps: send@0.5.0
   - Accept string for `maxage` (converted by `ms`)
   - Include link in default redirect response
 * deps: serve-static@~1.3.0
   - Accept string for `maxAge` (converted by `ms`)
   - Add `setHeaders` option
   - Include HTML link in redirect response
   - deps: send@0.5.0
 * deps: type-is@~1.3.2

4.4.5 / 2014-06-26
==================

 * deps: cookie-signature@1.0.4
   - fix for timing attacks

4.4.4 / 2014-06-20
==================

 * fix `res.attachment` Unicode filenames in Safari
 * fix "trim prefix" debug message in `express:router`
 * deps: accepts@~1.0.5
 * deps: buffer-crc32@0.2.3

4.4.3 / 2014-06-11
==================

 * fix persistence of modified `req.params[name]` from `app.param()`
 * deps: accepts@1.0.3
   - deps: negotiator@0.4.6
 * deps: debug@1.0.2
 * deps: send@0.4.3
   - Do not throw uncatchable error on file open race condition
   - Use `escape-html` for HTML escaping
   - deps: debug@1.0.2
   - deps: finished@1.2.2
   - deps: fresh@0.2.2
 * deps: serve-static@1.2.3
   - Do not throw uncatchable error on file open race condition
   - deps: send@0.4.3

4.4.2 / 2014-06-09
==================

 * fix catching errors from top-level handlers
 * use `vary` module for `res.vary`
 * deps: debug@1.0.1
 * deps: proxy-addr@1.0.1
 * deps: send@0.4.2
   - fix "event emitter leak" warnings
   - deps: debug@1.0.1
   - deps: finished@1.2.1
 * deps: serve-static@1.2.2
   - fix "event emitter leak" warnings
   - deps: send@0.4.2
 * deps: type-is@1.2.1

4.4.1 / 2014-06-02
==================

 * deps: methods@1.0.1
 * deps: send@0.4.1
   - Send `max-age` in `Cache-Control` in correct format
 * deps: serve-static@1.2.1
   - use `escape-html` for escaping
   - deps: send@0.4.1

4.4.0 / 2014-05-30
==================

 * custom etag control with `app.set('etag', val)`
   - `app.set('etag', function(body, encoding){ return '"etag"' })` custom etag generation
   - `app.set('etag', 'weak')` weak tag
   - `app.set('etag', 'strong')` strong etag
   - `app.set('etag', false)` turn off
   - `app.set('etag', true)` standard etag
 * mark `res.send` ETag as weak and reduce collisions
 * update accepts to 1.0.2
   - Fix interpretation when header not in request
 * update send to 0.4.0
   - Calculate ETag with md5 for reduced collisions
   - Ignore stream errors after request ends
   - deps: debug@0.8.1
 * update serve-static to 1.2.0
   - Calculate ETag with md5 for reduced collisions
   - Ignore stream errors after request ends
   - deps: send@0.4.0

4.3.2 / 2014-05-28
==================

 * fix handling of errors from `router.param()` callbacks

4.3.1 / 2014-05-23
==================

 * revert "fix behavior of multiple `app.VERB` for the same path"
   - this caused a regression in the order of route execution

4.3.0 / 2014-05-21
==================

 * add `req.baseUrl` to access the path stripped from `req.url` in routes
 * fix behavior of multiple `app.VERB` for the same path
 * fix issue routing requests among sub routers
 * invoke `router.param()` only when necessary instead of every match
 * proper proxy trust with `app.set('trust proxy', trust)`
   - `app.set('trust proxy', 1)` trust first hop
   - `app.set('trust proxy', 'loopback')` trust loopback addresses
   - `app.set('trust proxy', '10.0.0.1')` trust single IP
   - `app.set('trust proxy', '10.0.0.1/16')` trust subnet
   - `app.set('trust proxy', '10.0.0.1, 10.0.0.2')` trust list
   - `app.set('trust proxy', false)` turn off
   - `app.set('trust proxy', true)` trust everything
 * set proper `charset` in `Content-Type` for `res.send`
 * update type-is to 1.2.0
   - support suffix matching

4.2.0 / 2014-05-11
==================

 * deprecate `app.del()` -- use `app.delete()` instead
 * deprecate `res.json(obj, status)` -- use `res.json(status, obj)` instead
   - the edge-case `res.json(status, num)` requires `res.status(status).json(num)`
 * deprecate `res.jsonp(obj, status)` -- use `res.jsonp(status, obj)` instead
   - the edge-case `res.jsonp(status, num)` requires `res.status(status).jsonp(num)`
 * fix `req.next` when inside router instance
 * include `ETag` header in `HEAD` requests
 * keep previous `Content-Type` for `res.jsonp`
 * support PURGE method
   - add `app.purge`
   - add `router.purge`
   - include PURGE in `app.all`
 * update debug to 0.8.0
   - add `enable()` method
   - change from stderr to stdout
 * update methods to 1.0.0
   - add PURGE

4.1.2 / 2014-05-08
==================

 * fix `req.host` for IPv6 literals
 * fix `res.jsonp` error if callback param is object

4.1.1 / 2014-04-27
==================

 * fix package.json to reflect supported node version

4.1.0 / 2014-04-24
==================

 * pass options from `res.sendfile` to `send`
 * preserve casing of headers in `res.header` and `res.set`
 * support unicode file names in `res.attachment` and `res.download`
 * update accepts to 1.0.1
   - deps: negotiator@0.4.0
 * update cookie to 0.1.2
   - Fix for maxAge == 0
   - made compat with expires field
 * update send to 0.3.0
   - Accept API options in options object
   - Coerce option types
   - Control whether to generate etags
   - Default directory access to 403 when index disabled
   - Fix sending files with dots without root set
   - Include file path in etag
   - Make "Can't set headers after they are sent." catchable
   - Send full entity-body for multi range requests
   - Set etags to "weak"
   - Support "If-Range" header
   - Support multiple index paths
   - deps: mime@1.2.11
 * update serve-static to 1.1.0
   - Accept options directly to `send` module
   - Resolve relative paths at middleware setup
   - Use parseurl to parse the URL from request
   - deps: send@0.3.0
 * update type-is to 1.1.0
   - add non-array values support
   - add `multipart` as a shorthand

4.0.0 / 2014-04-09
==================

 * remove:
   - node 0.8 support
   - connect and connect's patches except for charset handling
   - express(1) - moved to [express-generator](https://github.com/expressjs/generator)
   - `express.createServer()` - it has been deprecated for a long time. Use `express()`
   - `app.configure` - use logic in your own app code
   - `app.router` - is removed
   - `req.auth` - use `basic-auth` instead
   - `req.accepted*` - use `req.accepts*()` instead
   - `res.location` - relative URL resolution is removed
   - `res.charset` - include the charset in the content type when using `res.set()`
   - all bundled middleware except `static`
 * change:
   - `app.route` -> `app.mountpath` when mounting an express app in another express app
   - `json spaces` no longer enabled by default in development
   - `req.accepts*` -> `req.accepts*s` - i.e. `req.acceptsEncoding` -> `req.acceptsEncodings`
   - `req.params` is now an object instead of an array
   - `res.locals` is no longer a function. It is a plain js object. Treat it as such.
   - `res.headerSent` -> `res.headersSent` to match node.js ServerResponse object
 * refactor:
   - `req.accepts*` with [accepts](https://github.com/expressjs/accepts)
   - `req.is` with [type-is](https://github.com/expressjs/type-is)
   - [path-to-regexp](https://github.com/component/path-to-regexp)
 * add:
   - `app.router()` - returns the app Router instance
   - `app.route()` - Proxy to the app's `Router#route()` method to create a new route
   - Router & Route - public API

3.21.2 / 2015-07-31
===================

  * deps: connect@2.30.2
    - deps: body-parser@~1.13.3
    - deps: compression@~1.5.2
    - deps: errorhandler@~1.4.2
    - deps: method-override@~2.3.5
    - deps: serve-index@~1.7.2
    - deps: type-is@~1.6.6
    - deps: vhost@~3.0.1
  * deps: vary@~1.0.1
    - Fix setting empty header from empty `field`
    - perf: enable strict mode
    - perf: remove argument reassignments

3.21.1 / 2015-07-05
===================

  * deps: basic-auth@~1.0.3
  * deps: connect@2.30.1
    - deps: body-parser@~1.13.2
    - deps: compression@~1.5.1
    - deps: errorhandler@~1.4.1
    - deps: morgan@~1.6.1
    - deps: pause@0.1.0
    - deps: qs@4.0.0
    - deps: serve-index@~1.7.1
    - deps: type-is@~1.6.4

3.21.0 / 2015-06-18
===================

  * deps: basic-auth@1.0.2
    - perf: enable strict mode
    - perf: hoist regular expression
    - perf: parse with regular expressions
    - perf: remove argument reassignment
  * deps: connect@2.30.0
    - deps: body-parser@~1.13.1
    - deps: bytes@2.1.0
    - deps: compression@~1.5.0
    - deps: cookie@0.1.3
    - deps: cookie-parser@~1.3.5
    - deps: csurf@~1.8.3
    - deps: errorhandler@~1.4.0
    - deps: express-session@~1.11.3
    - deps: finalhandler@0.4.0
    - deps: fresh@0.3.0
    - deps: morgan@~1.6.0
    - deps: serve-favicon@~2.3.0
    - deps: serve-index@~1.7.0
    - deps: serve-static@~1.10.0
    - deps: type-is@~1.6.3
  * deps: cookie@0.1.3
    - perf: deduce the scope of try-catch deopt
    - perf: remove argument reassignments
  * deps: escape-html@1.0.2
  * deps: etag@~1.7.0
    - Always include entity length in ETags for hash length extensions
    - Generate non-Stats ETags using MD5 only (no longer CRC32)
    - Improve stat performance by removing hashing
    - Improve support for JXcore
    - Remove base64 padding in ETags to shorten
    - Support "fake" stats objects in environments without fs
    - Use MD5 instead of MD4 in weak ETags over 1KB
  * deps: fresh@0.3.0
    - Add weak `ETag` matching support
  * deps: mkdirp@0.5.1
    - Work in global strict mode
  * deps: send@0.13.0
    - Allow Node.js HTTP server to set `Date` response header
    - Fix incorrectly removing `Content-Location` on 304 response
    - Improve the default redirect response headers
    - Send appropriate headers on default error response
    - Use `http-errors` for standard emitted errors
    - Use `statuses` instead of `http` module for status messages
    - deps: escape-html@1.0.2
    - deps: etag@~1.7.0
    - deps: fresh@0.3.0
    - deps: on-finished@~2.3.0
    - perf: enable strict mode
    - perf: remove unnecessary array allocations

3.20.3 / 2015-05-17
===================

  * deps: connect@2.29.2
    - deps: body-parser@~1.12.4
    - deps: compression@~1.4.4
    - deps: connect-timeout@~1.6.2
    - deps: debug@~2.2.0
    - deps: depd@~1.0.1
    - deps: errorhandler@~1.3.6
    - deps: finalhandler@0.3.6
    - deps: method-override@~2.3.3
    - deps: morgan@~1.5.3
    - deps: qs@2.4.2
    - deps: response-time@~2.3.1
    - deps: serve-favicon@~2.2.1
    - deps: serve-index@~1.6.4
    - deps: serve-static@~1.9.3
    - deps: type-is@~1.6.2
  * deps: debug@~2.2.0
    - deps: ms@0.7.1
  * deps: depd@~1.0.1
  * deps: proxy-addr@~1.0.8
    - deps: ipaddr.js@1.0.1
  * deps: send@0.12.3
    - deps: debug@~2.2.0
    - deps: depd@~1.0.1
    - deps: etag@~1.6.0
    - deps: ms@0.7.1
    - deps: on-finished@~2.2.1

3.20.2 / 2015-03-16
===================

  * deps: connect@2.29.1
    - deps: body-parser@~1.12.2
    - deps: compression@~1.4.3
    - deps: connect-timeout@~1.6.1
    - deps: debug@~2.1.3
    - deps: errorhandler@~1.3.5
    - deps: express-session@~1.10.4
    - deps: finalhandler@0.3.4
    - deps: method-override@~2.3.2
    - deps: morgan@~1.5.2
    - deps: qs@2.4.1
    - deps: serve-index@~1.6.3
    - deps: serve-static@~1.9.2
    - deps: type-is@~1.6.1
  * deps: debug@~2.1.3
    - Fix high intensity foreground color for bold
    - deps: ms@0.7.0
  * deps: merge-descriptors@1.0.0
  * deps: proxy-addr@~1.0.7
    - deps: ipaddr.js@0.1.9
  * deps: send@0.12.2
    - Throw errors early for invalid `extensions` or `index` options
    - deps: debug@~2.1.3

3.20.1 / 2015-02-28
===================

  * Fix `req.host` when using "trust proxy" hops count
  * Fix `req.protocol`/`req.secure` when using "trust proxy" hops count

3.20.0 / 2015-02-18
===================

  * Fix `"trust proxy"` setting to inherit when app is mounted
  * Generate `ETag`s for all request responses
    - No longer restricted to only responses for `GET` and `HEAD` requests
  * Use `content-type` to parse `Content-Type` headers
  * deps: connect@2.29.0
    - Use `content-type` to parse `Content-Type` headers
    - deps: body-parser@~1.12.0
    - deps: compression@~1.4.1
    - deps: connect-timeout@~1.6.0
    - deps: cookie-parser@~1.3.4
    - deps: cookie-signature@1.0.6
    - deps: csurf@~1.7.0
    - deps: errorhandler@~1.3.4
    - deps: express-session@~1.10.3
    - deps: http-errors@~1.3.1
    - deps: response-time@~2.3.0
    - deps: serve-index@~1.6.2
    - deps: serve-static@~1.9.1
    - deps: type-is@~1.6.0
  * deps: cookie-signature@1.0.6
  * deps: send@0.12.1
    - Always read the stat size from the file
    - Fix mutating passed-in `options`
    - deps: mime@1.3.4

3.19.2 / 2015-02-01
===================

  * deps: connect@2.28.3
    - deps: compression@~1.3.1
    - deps: csurf@~1.6.6
    - deps: errorhandler@~1.3.3
    - deps: express-session@~1.10.2
    - deps: serve-index@~1.6.1
    - deps: type-is@~1.5.6
  * deps: proxy-addr@~1.0.6
    - deps: ipaddr.js@0.1.8

3.19.1 / 2015-01-20
===================

  * deps: connect@2.28.2
    - deps: body-parser@~1.10.2
    - deps: serve-static@~1.8.1
  * deps: send@0.11.1
    - Fix root path disclosure

3.19.0 / 2015-01-09
===================

  * Fix `OPTIONS` responses to include the `HEAD` method property
  * Use `readline` for prompt in `express(1)`
  * deps: commander@2.6.0
  * deps: connect@2.28.1
    - deps: body-parser@~1.10.1
    - deps: compression@~1.3.0
    - deps: connect-timeout@~1.5.0
    - deps: csurf@~1.6.4
    - deps: debug@~2.1.1
    - deps: errorhandler@~1.3.2
    - deps: express-session@~1.10.1
    - deps: finalhandler@0.3.3
    - deps: method-override@~2.3.1
    - deps: morgan@~1.5.1
    - deps: serve-favicon@~2.2.0
    - deps: serve-index@~1.6.0
    - deps: serve-static@~1.8.0
    - deps: type-is@~1.5.5
  * deps: debug@~2.1.1
  * deps: methods@~1.1.1
  * deps: proxy-addr@~1.0.5
    - deps: ipaddr.js@0.1.6
  * deps: send@0.11.0
    - deps: debug@~2.1.1
    - deps: etag@~1.5.1
    - deps: ms@0.7.0
    - deps: on-finished@~2.2.0

3.18.6 / 2014-12-12
===================

  * Fix exception in `req.fresh`/`req.stale` without response headers

3.18.5 / 2014-12-11
===================

  * deps: connect@2.27.6
    - deps: compression@~1.2.2
    - deps: express-session@~1.9.3
    - deps: http-errors@~1.2.8
    - deps: serve-index@~1.5.3
    - deps: type-is@~1.5.4

3.18.4 / 2014-11-23
===================

  * deps: connect@2.27.4
    - deps: body-parser@~1.9.3
    - deps: compression@~1.2.1
    - deps: errorhandler@~1.2.3
    - deps: express-session@~1.9.2
    - deps: qs@2.3.3
    - deps: serve-favicon@~2.1.7
    - deps: serve-static@~1.5.1
    - deps: type-is@~1.5.3
  * deps: etag@~1.5.1
  * deps: proxy-addr@~1.0.4
    - deps: ipaddr.js@0.1.5

3.18.3 / 2014-11-09
===================

  * deps: connect@2.27.3
    - Correctly invoke async callback asynchronously
    - deps: csurf@~1.6.3

3.18.2 / 2014-10-28
===================

  * deps: connect@2.27.2
    - Fix handling of URLs containing `://` in the path
    - deps: body-parser@~1.9.2
    - deps: qs@2.3.2

3.18.1 / 2014-10-22
===================

  * Fix internal `utils.merge` deprecation warnings
  * deps: connect@2.27.1
    - deps: body-parser@~1.9.1
    - deps: express-session@~1.9.1
    - deps: finalhandler@0.3.2
    - deps: morgan@~1.4.1
    - deps: qs@2.3.0
    - deps: serve-static@~1.7.1
  * deps: send@0.10.1
    - deps: on-finished@~2.1.1

3.18.0 / 2014-10-17
===================

  * Use `content-disposition` module for `res.attachment`/`res.download`
    - Sends standards-compliant `Content-Disposition` header
    - Full Unicode support
  * Use `etag` module to generate `ETag` headers
  * deps: connect@2.27.0
    - Use `http-errors` module for creating errors
    - Use `utils-merge` module for merging objects
    - deps: body-parser@~1.9.0
    - deps: compression@~1.2.0
    - deps: connect-timeout@~1.4.0
    - deps: debug@~2.1.0
    - deps: depd@~1.0.0
    - deps: express-session@~1.9.0
    - deps: finalhandler@0.3.1
    - deps: method-override@~2.3.0
    - deps: morgan@~1.4.0
    - deps: response-time@~2.2.0
    - deps: serve-favicon@~2.1.6
    - deps: serve-index@~1.5.0
    - deps: serve-static@~1.7.0
  * deps: debug@~2.1.0
    - Implement `DEBUG_FD` env variable support
  * deps: depd@~1.0.0
  * deps: send@0.10.0
    - deps: debug@~2.1.0
    - deps: depd@~1.0.0
    - deps: etag@~1.5.0

3.17.8 / 2014-10-15
===================

  * deps: connect@2.26.6
    - deps: compression@~1.1.2
    - deps: csurf@~1.6.2
    - deps: errorhandler@~1.2.2

3.17.7 / 2014-10-08
===================

  * deps: connect@2.26.5
    - Fix accepting non-object arguments to `logger`
    - deps: serve-static@~1.6.4

3.17.6 / 2014-10-02
===================

  * deps: connect@2.26.4
    - deps: morgan@~1.3.2
    - deps: type-is@~1.5.2

3.17.5 / 2014-09-24
===================

  * deps: connect@2.26.3
    - deps: body-parser@~1.8.4
    - deps: serve-favicon@~2.1.5
    - deps: serve-static@~1.6.3
  * deps: proxy-addr@~1.0.3
    - Use `forwarded` npm module
  * deps: send@0.9.3
    - deps: etag@~1.4.0

3.17.4 / 2014-09-19
===================

  * deps: connect@2.26.2
    - deps: body-parser@~1.8.3
    - deps: qs@2.2.4

3.17.3 / 2014-09-18
===================

  * deps: proxy-addr@~1.0.2
    - Fix a global leak when multiple subnets are trusted
    - deps: ipaddr.js@0.1.3

3.17.2 / 2014-09-15
===================

  * Use `crc` instead of `buffer-crc32` for speed
  * deps: connect@2.26.1
    - deps: body-parser@~1.8.2
    - deps: depd@0.4.5
    - deps: express-session@~1.8.2
    - deps: morgan@~1.3.1
    - deps: serve-favicon@~2.1.3
    - deps: serve-static@~1.6.2
  * deps: depd@0.4.5
  * deps: send@0.9.2
    - deps: depd@0.4.5
    - deps: etag@~1.3.1
    - deps: range-parser@~1.0.2

3.17.1 / 2014-09-08
===================

  * Fix error in `req.subdomains` on empty host

3.17.0 / 2014-09-08
===================

  * Support `X-Forwarded-Host` in `req.subdomains`
  * Support IP address host in `req.subdomains`
  * deps: connect@2.26.0
    - deps: body-parser@~1.8.1
    - deps: compression@~1.1.0
    - deps: connect-timeout@~1.3.0
    - deps: cookie-parser@~1.3.3
    - deps: cookie-signature@1.0.5
    - deps: csurf@~1.6.1
    - deps: debug@~2.0.0
    - deps: errorhandler@~1.2.0
    - deps: express-session@~1.8.1
    - deps: finalhandler@0.2.0
    - deps: fresh@0.2.4
    - deps: media-typer@0.3.0
    - deps: method-override@~2.2.0
    - deps: morgan@~1.3.0
    - deps: qs@2.2.3
    - deps: serve-favicon@~2.1.3
    - deps: serve-index@~1.2.1
    - deps: serve-static@~1.6.1
    - deps: type-is@~1.5.1
    - deps: vhost@~3.0.0
  * deps: cookie-signature@1.0.5
  * deps: debug@~2.0.0
  * deps: fresh@0.2.4
  * deps: media-typer@0.3.0
    - Throw error when parameter format invalid on parse
  * deps: range-parser@~1.0.2
  * deps: send@0.9.1
    - Add `lastModified` option
    - Use `etag` to generate `ETag` header
    - deps: debug@~2.0.0
    - deps: fresh@0.2.4
  * deps: vary@~1.0.0
    - Accept valid `Vary` header string as `field`

3.16.10 / 2014-09-04
====================

  * deps: connect@2.25.10
    - deps: serve-static@~1.5.4
  * deps: send@0.8.5
    - Fix a path traversal issue when using `root`
    - Fix malicious path detection for empty string path

3.16.9 / 2014-08-29
===================

  * deps: connect@2.25.9
    - deps: body-parser@~1.6.7
    - deps: qs@2.2.2

3.16.8 / 2014-08-27
===================

  * deps: connect@2.25.8
    - deps: body-parser@~1.6.6
    - deps: csurf@~1.4.1
    - deps: qs@2.2.0

3.16.7 / 2014-08-18
===================

  * deps: connect@2.25.7
    - deps: body-parser@~1.6.5
    - deps: express-session@~1.7.6
    - deps: morgan@~1.2.3
    - deps: serve-static@~1.5.3
  * deps: send@0.8.3
    - deps: destroy@1.0.3
    - deps: on-finished@2.1.0

3.16.6 / 2014-08-14
===================

  * deps: connect@2.25.6
    - deps: body-parser@~1.6.4
    - deps: qs@1.2.2
    - deps: serve-static@~1.5.2
  * deps: send@0.8.2
    - Work around `fd` leak in Node.js 0.10 for `fs.ReadStream`

3.16.5 / 2014-08-11
===================

  * deps: connect@2.25.5
    - Fix backwards compatibility in `logger`

3.16.4 / 2014-08-10
===================

  * Fix original URL parsing in `res.location`
  * deps: connect@2.25.4
    - Fix `query` middleware breaking with argument
    - deps: body-parser@~1.6.3
    - deps: compression@~1.0.11
    - deps: connect-timeout@~1.2.2
    - deps: express-session@~1.7.5
    - deps: method-override@~2.1.3
    - deps: on-headers@~1.0.0
    - deps: parseurl@~1.3.0
    - deps: qs@1.2.1
    - deps: response-time@~2.0.1
    - deps: serve-index@~1.1.6
    - deps: serve-static@~1.5.1
  * deps: parseurl@~1.3.0

3.16.3 / 2014-08-07
===================

  * deps: connect@2.25.3
    - deps: multiparty@3.3.2

3.16.2 / 2014-08-07
===================

  * deps: connect@2.25.2
    - deps: body-parser@~1.6.2
    - deps: qs@1.2.0

3.16.1 / 2014-08-06
===================

  * deps: connect@2.25.1
    - deps: body-parser@~1.6.1
    - deps: qs@1.1.0

3.16.0 / 2014-08-05
===================

  * deps: connect@2.25.0
    - deps: body-parser@~1.6.0
    - deps: compression@~1.0.10
    - deps: csurf@~1.4.0
    - deps: express-session@~1.7.4
    - deps: qs@1.0.2
    - deps: serve-static@~1.5.0
  * deps: send@0.8.1
    - Add `extensions` option

3.15.3 / 2014-08-04
===================

  * fix `res.sendfile` regression for serving directory index files
  * deps: connect@2.24.3
    - deps: serve-index@~1.1.5
    - deps: serve-static@~1.4.4
  * deps: send@0.7.4
    - Fix incorrect 403 on Windows and Node.js 0.11
    - Fix serving index files without root dir

3.15.2 / 2014-07-27
===================

  * deps: connect@2.24.2
    - deps: body-parser@~1.5.2
    - deps: depd@0.4.4
    - deps: express-session@~1.7.2
    - deps: morgan@~1.2.2
    - deps: serve-static@~1.4.2
  * deps: depd@0.4.4
    - Work-around v8 generating empty stack traces
  * deps: send@0.7.2
    - deps: depd@0.4.4

3.15.1 / 2014-07-26
===================

  * deps: connect@2.24.1
    - deps: body-parser@~1.5.1
    - deps: depd@0.4.3
    - deps: express-session@~1.7.1
    - deps: morgan@~1.2.1
    - deps: serve-index@~1.1.4
    - deps: serve-static@~1.4.1
  * deps: depd@0.4.3
    - Fix exception when global `Error.stackTraceLimit` is too low
  * deps: send@0.7.1
    - deps: depd@0.4.3

3.15.0 / 2014-07-22
===================

  * Fix `req.protocol` for proxy-direct connections
  * Pass options from `res.sendfile` to `send`
  * deps: connect@2.24.0
    - deps: body-parser@~1.5.0
    - deps: compression@~1.0.9
    - deps: connect-timeout@~1.2.1
    - deps: debug@1.0.4
    - deps: depd@0.4.2
    - deps: express-session@~1.7.0
    - deps: finalhandler@0.1.0
    - deps: method-override@~2.1.2
    - deps: morgan@~1.2.0
    - deps: multiparty@3.3.1
    - deps: parseurl@~1.2.0
    - deps: serve-static@~1.4.0
  * deps: debug@1.0.4
  * deps: depd@0.4.2
    - Add `TRACE_DEPRECATION` environment variable
    - Remove non-standard grey color from color output
    - Support `--no-deprecation` argument
    - Support `--trace-deprecation` argument
  * deps: parseurl@~1.2.0
    - Cache URLs based on original value
    - Remove no-longer-needed URL mis-parse work-around
    - Simplify the "fast-path" `RegExp`
  * deps: send@0.7.0
    - Add `dotfiles` option
    - Cap `maxAge` value to 1 year
    - deps: debug@1.0.4
    - deps: depd@0.4.2

3.14.0 / 2014-07-11
===================

 * add explicit "Rosetta Flash JSONP abuse" protection
   - previous versions are not vulnerable; this is just explicit protection
 * deprecate `res.redirect(url, status)` -- use `res.redirect(status, url)` instead
 * fix `res.send(status, num)` to send `num` as json (not error)
 * remove unnecessary escaping when `res.jsonp` returns JSON response
 * deps: basic-auth@1.0.0
   - support empty password
   - support empty username
 * deps: connect@2.23.0
   - deps: debug@1.0.3
   - deps: express-session@~1.6.4
   - deps: method-override@~2.1.0
   - deps: parseurl@~1.1.3
   - deps: serve-static@~1.3.1
  * deps: debug@1.0.3
    - Add support for multiple wildcards in namespaces
  * deps: methods@1.1.0
    - add `CONNECT`
  * deps: parseurl@~1.1.3
    - faster parsing of href-only URLs

3.13.0 / 2014-07-03
===================

 * add deprecation message to `app.configure`
 * add deprecation message to `req.auth`
 * use `basic-auth` to parse `Authorization` header
 * deps: connect@2.22.0
   - deps: csurf@~1.3.0
   - deps: express-session@~1.6.1
   - deps: multiparty@3.3.0
   - deps: serve-static@~1.3.0
 * deps: send@0.5.0
   - Accept string for `maxage` (converted by `ms`)
   - Include link in default redirect response

3.12.1 / 2014-06-26
===================

 * deps: connect@2.21.1
   - deps: cookie-parser@1.3.2
   - deps: cookie-signature@1.0.4
   - deps: express-session@~1.5.2
   - deps: type-is@~1.3.2
 * deps: cookie-signature@1.0.4
   - fix for timing attacks

3.12.0 / 2014-06-21
===================

 * use `media-typer` to alter content-type charset
 * deps: connect@2.21.0
   - deprecate `connect(middleware)` -- use `app.use(middleware)` instead
   - deprecate `connect.createServer()` -- use `connect()` instead
   - fix `res.setHeader()` patch to work with with get -> append -> set pattern
   - deps: compression@~1.0.8
   - deps: errorhandler@~1.1.1
   - deps: express-session@~1.5.0
   - deps: serve-index@~1.1.3

3.11.0 / 2014-06-19
===================

 * deprecate things with `depd` module
 * deps: buffer-crc32@0.2.3
 * deps: connect@2.20.2
   - deprecate `verify` option to `json` -- use `body-parser` npm module instead
   - deprecate `verify` option to `urlencoded` -- use `body-parser` npm module instead
   - deprecate things with `depd` module
   - use `finalhandler` for final response handling
   - use `media-typer` to parse `content-type` for charset
   - deps: body-parser@1.4.3
   - deps: connect-timeout@1.1.1
   - deps: cookie-parser@1.3.1
   - deps: csurf@1.2.2
   - deps: errorhandler@1.1.0
   - deps: express-session@1.4.0
   - deps: multiparty@3.2.9
   - deps: serve-index@1.1.2
   - deps: type-is@1.3.1
   - deps: vhost@2.0.0

3.10.5 / 2014-06-11
===================

 * deps: connect@2.19.6
   - deps: body-parser@1.3.1
   - deps: compression@1.0.7
   - deps: debug@1.0.2
   - deps: serve-index@1.1.1
   - deps: serve-static@1.2.3
 * deps: debug@1.0.2
 * deps: send@0.4.3
   - Do not throw uncatchable error on file open race condition
   - Use `escape-html` for HTML escaping
   - deps: debug@1.0.2
   - deps: finished@1.2.2
   - deps: fresh@0.2.2

3.10.4 / 2014-06-09
===================

 * deps: connect@2.19.5
   - fix "event emitter leak" warnings
   - deps: csurf@1.2.1
   - deps: debug@1.0.1
   - deps: serve-static@1.2.2
   - deps: type-is@1.2.1
 * deps: debug@1.0.1
 * deps: send@0.4.2
   - fix "event emitter leak" warnings
   - deps: finished@1.2.1
   - deps: debug@1.0.1

3.10.3 / 2014-06-05
===================

 * use `vary` module for `res.vary`
 * deps: connect@2.19.4
   - deps: errorhandler@1.0.2
   - deps: method-override@2.0.2
   - deps: serve-favicon@2.0.1
 * deps: debug@1.0.0

3.10.2 / 2014-06-03
===================

 * deps: connect@2.19.3
   - deps: compression@1.0.6

3.10.1 / 2014-06-03
===================

 * deps: connect@2.19.2
   - deps: compression@1.0.4
 * deps: proxy-addr@1.0.1

3.10.0 / 2014-06-02
===================

 * deps: connect@2.19.1
   - deprecate `methodOverride()` -- use `method-override` npm module instead
   - deps: body-parser@1.3.0
   - deps: method-override@2.0.1
   - deps: multiparty@3.2.8
   - deps: response-time@2.0.0
   - deps: serve-static@1.2.1
 * deps: methods@1.0.1
 * deps: send@0.4.1
   - Send `max-age` in `Cache-Control` in correct format

3.9.0 / 2014-05-30
==================

 * custom etag control with `app.set('etag', val)`
   - `app.set('etag', function(body, encoding){ return '"etag"' })` custom etag generation
   - `app.set('etag', 'weak')` weak tag
   - `app.set('etag', 'strong')` strong etag
   - `app.set('etag', false)` turn off
   - `app.set('etag', true)` standard etag
 * Include ETag in HEAD requests
 * mark `res.send` ETag as weak and reduce collisions
 * update connect to 2.18.0
   - deps: compression@1.0.3
   - deps: serve-index@1.1.0
   - deps: serve-static@1.2.0
 * update send to 0.4.0
   - Calculate ETag with md5 for reduced collisions
   - Ignore stream errors after request ends
   - deps: debug@0.8.1

3.8.1 / 2014-05-27
==================

 * update connect to 2.17.3
   - deps: body-parser@1.2.2
   - deps: express-session@1.2.1
   - deps: method-override@1.0.2

3.8.0 / 2014-05-21
==================

 * keep previous `Content-Type` for `res.jsonp`
 * set proper `charset` in `Content-Type` for `res.send`
 * update connect to 2.17.1
   - fix `res.charset` appending charset when `content-type` has one
   - deps: express-session@1.2.0
   - deps: morgan@1.1.1
   - deps: serve-index@1.0.3

3.7.0 / 2014-05-18
==================

 * proper proxy trust with `app.set('trust proxy', trust)`
   - `app.set('trust proxy', 1)` trust first hop
   - `app.set('trust proxy', 'loopback')` trust loopback addresses
   - `app.set('trust proxy', '10.0.0.1')` trust single IP
   - `app.set('trust proxy', '10.0.0.1/16')` trust subnet
   - `app.set('trust proxy', '10.0.0.1, 10.0.0.2')` trust list
   - `app.set('trust proxy', false)` turn off
   - `app.set('trust proxy', true)` trust everything
 * update connect to 2.16.2
   - deprecate `res.headerSent` -- use `res.headersSent`
   - deprecate `res.on("header")` -- use on-headers module instead
   - fix edge-case in `res.appendHeader` that would append in wrong order
   - json: use body-parser
   - urlencoded: use body-parser
 