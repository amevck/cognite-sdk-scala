package com.cognite.sdk.scala

import scala.concurrent.duration._

import cats.Id
import com.softwaremill.sttp.{HttpURLConnectionBackend, SttpBackend}
import com.cognite.sdk.scala.common.{GzipSttpBackend, RetryingBackend}

import scala.concurrent.ExecutionContext

package object v1 {
  implicit def sttpBackend(implicit ec: ExecutionContext): SttpBackend[Id, Nothing] =
    new RetryingBackend[Id, Nothing](
      new GzipSttpBackend[Id, Nothing](
        HttpURLConnectionBackend()
      ),
      initialRetryDelay = 100.millis,
      maxRetryDelay = 200.millis
    )
}
