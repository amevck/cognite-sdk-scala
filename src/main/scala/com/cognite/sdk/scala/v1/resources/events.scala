package com.cognite.sdk.scala.v1.resources

import java.time.Instant

import com.cognite.sdk.scala.common._
import com.cognite.sdk.scala.v1._
import com.softwaremill.sttp._
import io.circe.{Decoder, Encoder}
import io.circe.derivation.{deriveDecoder, deriveEncoder}

class Events[F[_]](val requestSession: RequestSession[F])
    extends WithRequestSession[F]
    with PartitionedReadable[Event, F]
    with RetrieveByIds[Event, F]
    with RetrieveByExternalIds[Event, F]
    with Create[Event, EventCreate, F]
    with DeleteByIds[F, Long]
    with DeleteByExternalIds[F]
    with PartitionedFilter[Event, EventsFilter, F]
    with Search[Event, EventsQuery, F]
    with Update[Event, EventUpdate, F] {
  import Events._
  override val baseUri = uri"${requestSession.baseUri}/events"

  override private[sdk] def readWithCursor(
      cursor: Option[String],
      limit: Option[Int],
      partition: Option[Partition]
  ): F[ItemsWithCursor[Event]] =
    Readable.readWithCursor(requestSession, baseUri, cursor, limit, partition)

  /**
  * Retrieve Events based on their IDs
   * @param ids Sequence of IDs for events to retrieve
   * @return Sequence of events corresponding to IDs
   */
  override def retrieveByIds(ids: Seq[Long]): F[Seq[Event]] =
    RetrieveByIds.retrieveByIds(requestSession, baseUri, ids)

  /**
  * Retrieve Events based on their externalIDs
   * @param externalIds Sequence of external IDs for events to retrieve
   * @return Sequence of events corresponding to externalIds
   */
  override def retrieveByExternalIds(externalIds: Seq[String]): F[Seq[Event]] =
    RetrieveByExternalIds.retrieveByExternalIds(requestSession, baseUri, externalIds)

  /**
  * Create Events. See documentation on Items and EventCreate
   * @param items Events to create
   * @return Sequence of events that were created
   */
  override def createItems(items: Items[EventCreate]): F[Seq[Event]] =
    Create.createItems[F, Event, EventCreate](requestSession, baseUri, items)

  /**
  * Update a group of Events. See documentation on EventUpdate
   * @param items Events to update
   * @return Sequence of newly updated Events
   */
  override def update(items: Seq[EventUpdate]): F[Seq[Event]] =
    Update.update[F, Event, EventUpdate](requestSession, baseUri, items)

  /**
  * Delete Events by their IDs
   * @param ids IDs corresponding to the Events to delete
   * @return Unit
   */
  override def deleteByIds(ids: Seq[Long]): F[Unit] =
    DeleteByIds.deleteByIds(requestSession, baseUri, ids)

  /**
  * Delete Events by their external IDs
   * @param externalIds External IDs corresponding to the Events to delete
   * @return Unit
   */
  override def deleteByExternalIds(externalIds: Seq[String]): F[Unit] =
    DeleteByExternalIds.deleteByExternalIds(requestSession, baseUri, externalIds)

  private[sdk] def filterWithCursor(
      filter: EventsFilter,
      cursor: Option[String],
      limit: Option[Int],
      partition: Option[Partition]
  ): F[ItemsWithCursor[Event]] =
    Filter.filterWithCursor(requestSession, baseUri, filter, cursor, limit, partition)

  /**
  * Search for Events matching a specified query
   * @param searchQuery Query to match Events
   * @return Events matching searchQuery
   */
  override def search(searchQuery: EventsQuery): F[Seq[Event]] =
    Search.search(requestSession, baseUri, searchQuery)
}

object Events {
  implicit val instantEncoder: Encoder[Instant] = Encoder.encodeLong.contramap(_.toEpochMilli)
  implicit val instantDecoder: Decoder[Instant] = Decoder.decodeLong.map(Instant.ofEpochMilli)

  implicit val eventDecoder: Decoder[Event] = deriveDecoder[Event]
  implicit val eventsItemsWithCursorDecoder: Decoder[ItemsWithCursor[Event]] =
    deriveDecoder[ItemsWithCursor[Event]]
  implicit val eventsItemsDecoder: Decoder[Items[Event]] =
    deriveDecoder[Items[Event]]
  implicit val createEventEncoder: Encoder[EventCreate] = deriveEncoder[EventCreate]
  implicit val createEventsItemsEncoder: Encoder[Items[EventCreate]] =
    deriveEncoder[Items[EventCreate]]
  implicit val eventUpdateEncoder: Encoder[EventUpdate] =
    deriveEncoder[EventUpdate]
  implicit val updateEventsItemsEncoder: Encoder[Items[EventUpdate]] =
    deriveEncoder[Items[EventUpdate]]
  implicit val eventsFilterEncoder: Encoder[EventsFilter] =
    deriveEncoder[EventsFilter]
  implicit val eventsSearchEncoder: Encoder[EventsSearch] =
    deriveEncoder[EventsSearch]
  implicit val eventsQueryEncoder: Encoder[EventsQuery] =
    deriveEncoder[EventsQuery]
  implicit val eventsFilterRequestEncoder: Encoder[FilterRequest[EventsFilter]] =
    deriveEncoder[FilterRequest[EventsFilter]]
}
