package org.kontr.dsl

@DslMarker
annotation class KontrDsl

@KontrDsl
class CollectionDsl : Collection()

@KontrDsl
class RequestDsl : Request()

@KontrDsl
class ResponseDsl : Response()

//==========
@DslMarker
annotation class DslColour1 // yellow-green

@DslMarker
annotation class DslColour2 //  green-blue

@DslMarker
annotation class DslColour3 //   purple

@DslMarker
annotation class DslColour4 //  orange

