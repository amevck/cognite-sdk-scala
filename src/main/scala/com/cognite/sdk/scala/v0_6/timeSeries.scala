package com.cognite.sdk.scala.v0_6

import com.cognite.sdk.scala.common.{Auth, ReadableResource, Resource, WritableResource}
import com.softwaremill.sttp._
import io.circe.{Decoder, Encoder}

final case class TimeSeries(
    name: String,
    isString: Boolean = false,
    metadata: Option[Map[String, String]] = None,
    unit: Option[String] = None,
    assetId: Option[Long] = None,
    isStep: Boolean = false,
    description: Option[String] = None,
    securityCategories: Option[Seq[Long]] = None,
    id: Option[Long] = None,
    createdTime: Option[Long] = None,
    lastUpdatedTime: Option[Long] = None
)

final case class PostTimeSeries(
    name: String,
    isString: Boolean = false,
    metadata: Option[Map[String, String]] = None,
    unit: Option[String] = None,
    assetId: Option[Long] = None,
    isStep: Boolean = false,
    description: Option[String] = None,
    securityCategories: Option[Seq[Long]] = None
)

class TimeSeriesResource[F[_]](
    implicit val auth: Auth,
    val sttpBackend: SttpBackend[F, _],
    val readDecoder: Decoder[TimeSeries],
    val writeDecoder: Decoder[PostTimeSeries],
    val writeEncoder: Encoder[PostTimeSeries]
) extends Resource
    with ReadableResource[TimeSeries, F]
    with WritableResource[TimeSeries, PostTimeSeries, F] {
  override val baseUri = uri"https://api.cognitedata.com/api/0.6/projects/playground/timeseries"
}
