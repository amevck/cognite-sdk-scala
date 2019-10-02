package com.cognite.sdk.scala.v1.resources

import java.time.Instant

import com.cognite.sdk.scala.common._
import com.cognite.sdk.scala.v1._
import com.softwaremill.sttp._
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import io.circe.syntax._


class TimeSeriesResource[F[_]](val requestSession: RequestSession[F])
    extends WithRequestSession[F]
    with Readable[TimeSeries, F]
    with RetrieveByIds[TimeSeries, F]
    with RetrieveByExternalIds[TimeSeries, F]
    with Create[TimeSeries, TimeSeriesCreate, F]
    with DeleteByIds[F, Long]
    with DeleteByExternalIds[F]
    with Filter[TimeSeries, TimeSeriesFilter, F]
    with Search[TimeSeries, TimeSeriesQuery, F]
    with Update[TimeSeries, TimeSeriesUpdate, F] {
  import TimeSeriesResource._

  /**
  * Base URL for requests using this resource
   */
  override val baseUri = uri"${requestSession.baseUri}/timeseries"

  override private[sdk] def readWithCursor(
      cursor: Option[String],
      limit: Option[Int],
      partition: Option[Partition]
  ): F[ItemsWithCursor[TimeSeries]] =
    Readable.readWithCursor(requestSession, baseUri, cursor, limit, None)

  /**
  * Retrieve time series by ID
   * @param ids IDs of time series to retrieve
   * @return Time series corresponding to ids
   */
  override def retrieveByIds(ids: Seq[Long]): F[Seq[TimeSeries]] =
    RetrieveByIds.retrieveByIds(requestSession, baseUri, ids)

  /**
  * Retrieve time series by external IDs
   * @param externalIds External IDs of time series to retrieve
   * @return Time series corresponding to externalIds
   */
  override def retrieveByExternalIds(externalIds: Seq[String]): F[Seq[TimeSeries]] =
    RetrieveByExternalIds.retrieveByExternalIds(requestSession, baseUri, externalIds)

  /**
  * Create new time series. See documentation on TimeSeriesCreate
   * @param items Time series to create
   * @return Newly created time series
   */
  override def createItems(items: Items[TimeSeriesCreate]): F[Seq[TimeSeries]] =
    Create.createItems[F, TimeSeries, TimeSeriesCreate](requestSession, baseUri, items)

  /**
  * Update existing time series. See TimeSeriesUpdate
   * @param items Updates to time series
   * @return Updated time series
   */
  override def update(items: Seq[TimeSeriesUpdate]): F[Seq[TimeSeries]] =
    Update.update[F, TimeSeries, TimeSeriesUpdate](requestSession, baseUri, items)

  /**
  * Delete time series specified by their IDs
   * @param ids IDs of time series to delete
   * @return Unit
   */
  override def deleteByIds(ids: Seq[Long]): F[Unit] =
    DeleteByIds.deleteByIds(requestSession, baseUri, ids)

  /**
  * Delete time series specified by their externalIDs
   * @param externalIds External IDs of time series to delete
   * @return Unit
   */
  override def deleteByExternalIds(externalIds: Seq[String]): F[Unit] =
    DeleteByExternalIds.deleteByExternalIds(requestSession, baseUri, externalIds)

  override private[sdk] def filterWithCursor(
      filter: TimeSeriesFilter,
      cursor: Option[String],
      limit: Option[Int],
      partition: Option[Partition]
  ): F[ItemsWithCursor[TimeSeries]] = {
    val uriWithAssetIds = filter.assetIds.fold(baseUri)(
      assetIds => baseUri.param("assetIds", assetIds.asJson.toString())
    )
    Readable.readWithCursor(requestSession, uriWithAssetIds, cursor, limit, None)
  }

  /**
  * Query for specific time series. See documentation on TimeSeriesQuery
   * @param searchQuery Criteria for returned time series
   * @return Time series matching searchQuery
   */
  override def search(searchQuery: TimeSeriesQuery): F[Seq[TimeSeries]] =
    Search.search(requestSession, baseUri, searchQuery)
}

object TimeSeriesResource {
  implicit val instantEncoder: Encoder[Instant] = Encoder.encodeLong.contramap(_.toEpochMilli)
  implicit val instantDecoder: Decoder[Instant] = Decoder.decodeLong.map(Instant.ofEpochMilli)

  implicit val timeSeriesDecoder: Decoder[TimeSeries] = deriveDecoder[TimeSeries]
  implicit val timeSeriesUpdateEncoder: Encoder[TimeSeriesUpdate] = deriveEncoder[TimeSeriesUpdate]
  implicit val timeSeriesItemsWithCursorDecoder: Decoder[ItemsWithCursor[TimeSeries]] =
    deriveDecoder[ItemsWithCursor[TimeSeries]]
  implicit val timeSeriesItemsDecoder: Decoder[Items[TimeSeries]] =
    deriveDecoder[Items[TimeSeries]]
  implicit val createTimeSeriesEncoder: Encoder[TimeSeriesCreate] = deriveEncoder[TimeSeriesCreate]
  implicit val createTimeSeriesItemsEncoder: Encoder[Items[TimeSeriesCreate]] =
    deriveEncoder[Items[TimeSeriesCreate]]
  implicit val timeSeriesFilterEncoder: Encoder[TimeSeriesSearchFilter] =
    deriveEncoder[TimeSeriesSearchFilter]
  implicit val timeSeriesSearchEncoder: Encoder[TimeSeriesSearch] =
    deriveEncoder[TimeSeriesSearch]
  implicit val timeSeriesQueryEncoder: Encoder[TimeSeriesQuery] =
    deriveEncoder[TimeSeriesQuery]
}
