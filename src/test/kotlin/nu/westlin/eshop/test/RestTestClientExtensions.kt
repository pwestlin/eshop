@file:Suppress("unused")

package nu.westlin.eshop.test

import org.springframework.test.web.servlet.client.EntityExchangeResult
import org.springframework.test.web.servlet.client.RestTestClient

val <T : Any> EntityExchangeResult<T>.requiredBody: T
    get() = this.responseBody ?: throw AssertionError("Expected response body to be present, but it was null.")

inline fun <reified T : Any> RestTestClient.ResponseSpec.returnRequiredBody(): T = this.expectBody(T::class.java)
    .returnResult()
    .responseBody ?: throw AssertionError("Expected response body to be present, but it was null.")