package com.cognite.sdk.scala.v1

import java.time.Instant

import io.circe.{Decoder, Encoder}
import io.circe.derivation.deriveEncoder

final private[sdk] case class CogniteExternalId(externalId: String)

/**
* @constructor Time range for queries and filters
 * @param min Beginning of the time range
 * @param max End of the time range
 */
final case class TimeRange(min: Instant, max: Instant)

/**
* Decoder used for internal purposes
 */
object TimeRange {
  implicit val instantEncoder: Encoder[Instant] = Encoder.encodeLong.contramap(_.toEpochMilli)
  implicit val instantDecoder: Decoder[Instant] = Decoder.decodeLong.map(Instant.ofEpochMilli)
  implicit val timeRangeEncoder: Encoder[TimeRange] = deriveEncoder
}
