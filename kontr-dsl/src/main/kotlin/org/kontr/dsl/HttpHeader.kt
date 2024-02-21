package org.kontr.dsl

@DslColour2
class HttpHeader(val key: String, vararg var values: String)

@DslColour2
class HttpHeadersBuilder {
    private val headers = mutableMapOf<String, HttpHeader>()

    private fun build(key: String, vararg values: String): HttpHeader =
        HttpHeader(key, *values).also { headers[key] = it }

    @DslColour2
    fun build() = headers.toMap()

    @DslColour2
    fun Header(vararg keyAndValues: String): HttpHeader =
        if (keyAndValues.isEmpty()) {
            throw RuntimeException("Header requires 'name' and list of 'values'")
        } else {
            build(keyAndValues.first(), *keyAndValues.drop(1).toTypedArray())
        }

    @DslColour2
    fun Accept(vararg values: String) = build("Accept", *values)

    @DslColour2
    fun AcceptCharset(vararg values: String) = build("Accept-Charset", *values)

    @DslColour2
    fun AcceptEncoding(vararg values: String) = build("Accept-Encoding", *values)

    @DslColour2
    fun AcceptLanguage(vararg values: String) = build("Accept-Language", *values)

    @DslColour2
    fun AcceptRanges(vararg values: String) = build("Accept-Ranges", *values)

    @DslColour2
    fun Age(vararg values: String) = build("Age", *values)

    @DslColour2
    fun Allow(vararg values: String) = build("Allow", *values)

    @DslColour2
    fun Authorization(vararg values: String) = build("Authorization", *values)

    @DslColour2
    fun CacheControl(vararg values: String) = build("Cache-Control", *values)

    @DslColour2
    fun Connection(vararg values: String) = build("Connection", *values)

    @DslColour2
    fun ContentEncoding(vararg values: String) = build("Content-Encoding", *values)

    @DslColour2
    fun ContentLanguage(vararg values: String) = build("Content-Language", *values)

    @DslColour2
    fun ContentLength(vararg values: String) = build("Content-Length", *values)

    @DslColour2
    fun ContentLocation(vararg values: String) = build("Content-Location", *values)

    @DslColour2
    fun ContentMD5(vararg values: String) = build("Content-MD5", *values)

    @DslColour2
    fun ContentRange(vararg values: String) = build("Content-Range", *values)

    @DslColour2
    fun ContentType(vararg values: String) = build("Content-Type", *values)

    @DslColour2
    fun Date(vararg values: String) = build("Date", *values)

    @DslColour2
    fun Dav(vararg values: String) = build("Dav", *values)

    @DslColour2
    fun Depth(vararg values: String) = build("Depth", *values)

    @DslColour2
    fun Destination(vararg values: String) = build("Destination", *values)

    @DslColour2
    fun ETag(vararg values: String) = build("ETag", *values)

    @DslColour2
    fun Expect(vararg values: String) = build("Expect", *values)

    @DslColour2
    fun Expires(vararg values: String) = build("Expires", *values)

    @DslColour2
    fun From(vararg values: String) = build("From", *values)

    @DslColour2
    fun Host(vararg values: String) = build("Host", *values)

    @DslColour2
    fun If(vararg values: String) = build("If", *values)

    @DslColour2
    fun IfMatch(vararg values: String) = build("If-Match", *values)

    @DslColour2
    fun IfModifiedSince(vararg values: String) = build("If-Modified-Since", *values)

    @DslColour2
    fun IfNoneMatch(vararg values: String) = build("If-None-Match", *values)

    @DslColour2
    fun IfRange(vararg values: String) = build("If-Range", *values)

    @DslColour2
    fun IfUnmodifiedSince(vararg values: String) = build("If-Unmodified-Since", *values)

    @DslColour2
    fun LastModified(vararg values: String) = build("Last-Modified", *values)

    @DslColour2
    fun Location(vararg values: String) = build("Location", *values)

    @DslColour2
    fun LockToken(vararg values: String) = build("Lock-Token", *values)

    @DslColour2
    fun MaxForwards(vararg values: String) = build("Max-Forwards", *values)

    @DslColour2
    fun Overwrite(vararg values: String) = build("Overwrite", *values)

    @DslColour2
    fun Pragma(vararg values: String) = build("Pragma", *values)

    @DslColour2
    fun ProxyAuthenticate(vararg values: String) = build("Proxy-Authenticate", *values)

    @DslColour2
    fun ProxyAuthorization(vararg values: String) = build("Proxy-Authorization", *values)

    @DslColour2
    fun Range(vararg values: String) = build("Range", *values)

    @DslColour2
    fun Referer(vararg values: String) = build("Referer", *values)

    @DslColour2
    fun RetryAfter(vararg values: String) = build("Retry-After", *values)

    @DslColour2
    fun Server(vararg values: String) = build("Server", *values)

    @DslColour2
    fun StatusURI(vararg values: String) = build("Status-URI", *values)

    @DslColour2
    fun TE(vararg values: String) = build("TE", *values)

    @DslColour2
    fun Timeout(vararg values: String) = build("Timeout", *values)

    @DslColour2
    fun Trailer(vararg values: String) = build("Trailer", *values)

    @DslColour2
    fun TransferEncoding(vararg values: String) = build("Transfer-Encoding", *values)

    @DslColour2
    fun Upgrade(vararg values: String) = build("Upgrade", *values)

    @DslColour2
    fun UserAgent(vararg values: String) = build("User-Agent", *values)

    @DslColour2
    fun Vary(vararg values: String) = build("Vary", *values)

    @DslColour2
    fun Via(vararg values: String) = build("Via", *values)

    @DslColour2
    fun Warning(vararg values: String) = build("Warning", *values)

    @DslColour2
    fun WWWAuthenticate(vararg values: String) = build("WWW-Authenticate", *values)

}
