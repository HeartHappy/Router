package com.hearthappy.router.exception
/**
 * Constructs a new `RuntimeException` with the current stack trace
 * and the specified detail message.
 *
 * @param detailMessage the detail message for this exception.
 */
class NoRouteFoundException (detailMessage: String?) : RuntimeException(detailMessage)