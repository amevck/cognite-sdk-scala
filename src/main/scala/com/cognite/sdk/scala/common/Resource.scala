package com.cognite.sdk.scala.common

import com.cognite.sdk.scala.v1.RequestSession
import com.softwaremill.sttp.Uri

private[sdk] object Resource {
  val defaultLimit: Int = 1000
}

private[sdk] trait BaseUri {
  val baseUri: Uri
}

private[sdk] trait WithRequestSession[F[_]] {
  val requestSession: RequestSession[F]
}
