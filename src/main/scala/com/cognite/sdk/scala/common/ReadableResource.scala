package com.cognite.sdk.scala.common

import cats.effect.Concurrent
import com.cognite.sdk.scala.v1._
import com.softwaremill.sttp._
import com.softwaremill.sttp.circe._
import fs2._
import io.circe.{Decoder, Encoder}
import io.circe.derivation.deriveEncoder

// TODO: Verify that index and numPartitions are valid
final case class Partition(index: Int = 1, numPartitions: Int = 1) {
  override def toString: String = s"${index.toString}/${numPartitions.toString}"
}

trait Readable[R, F[_]] extends WithRequestSession[F] with BaseUri {
  private[sdk] def readWithCursor(
      cursor: Option[String],
      limit: Option[Int],
      partition: Option[Partition]
  ): F[ItemsWithCursor[R]]

  /**
  * Read items of this resource
   * @param limit maximum number of items to read
   * @return
   */
  def read(limit: Option[Int] = None): F[ItemsWithCursor[R]] =
    readWithCursor(None, limit, None)

  private[sdk] def listWithNextCursor(
      cursor: Option[String],
      limit: Option[Int]
  ): Stream[F, R] =
    Readable
      .pullFromCursor(cursor, limit, None, readWithCursor)
      .stream

  /**
  * List elements of this resource
   * @param limit Optional maximum number of elements to return
   * @return Elements of this resource
   */
  def list(limit: Option[Int] = None): Stream[F, R] =
    listWithNextCursor(None, limit)
}

trait PartitionedReadable[R, F[_]] extends Readable[R, F] {
  def listPartitions(numPartitions: Int, limitPerPartition: Option[Int] = None): Seq[Stream[F, R]] =
    1.to(numPartitions).map { i =>
      Readable
        .pullFromCursor(
          None,
          limitPerPartition,
          Some(Partition(i, numPartitions)),
          readWithCursor
        )
        .stream
    }

  def listConcurrently(numPartitions: Int, limitPerPartition: Option[Int] = None)(
      implicit c: Concurrent[F]
  ): Stream[F, R] =
    listPartitions(numPartitions, limitPerPartition).fold(Stream.empty)(_.merge(_))
}

object Readable {
  @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
  private[sdk] def pullFromCursor[F[_], R](
      cursor: Option[String],
      limit: Option[Int],
      partition: Option[Partition],
      get: (Option[String], Option[Int], Option[Partition]) => F[ItemsWithCursor[R]]
  ): Pull[F, R, Unit] =
    if (limit.exists(_ <= 0)) {
      Pull.done
    } else {
      Pull.eval(get(cursor, limit, partition)).flatMap { items =>
        Pull.output(Chunk.seq(items.items)) >>
          items.nextCursor
            .map { s =>
              pullFromCursor(Some(s), limit.map(_ - items.items.size), partition, get)
            }
            .getOrElse(Pull.done)
      }
    }

  private def uriWithCursorAndLimit(baseUri: Uri, cursor: Option[String], limit: Option[Int]) =
    cursor
      .fold(baseUri)(baseUri.param("cursor", _))
      .param("limit", limit.getOrElse(Resource.defaultLimit).toString)

  private[sdk] def readWithCursor[F[_], R](
      requestSession: RequestSession[F],
      baseUri: Uri,
      cursor: Option[String],
      limit: Option[Int],
      partition: Option[Partition]
  )(
      implicit itemsWithCursorDecoder: Decoder[ItemsWithCursor[R]]
  ): F[ItemsWithCursor[R]] = {
    implicit val errorOrItemsDecoder: Decoder[Either[CdpApiError, ItemsWithCursor[R]]] =
      EitherDecoder.eitherDecoder[CdpApiError, ItemsWithCursor[R]]
    val uriWithCursor = uriWithCursorAndLimit(baseUri, cursor, limit)
    val uriWithCursorAndPartition = partition.fold(uriWithCursor) { p =>
      uriWithCursor.param("partition", p.toString)
    }

    requestSession
      .sendCdf { request =>
        request
          .get(uriWithCursorAndPartition)
          .response(asJson[Either[CdpApiError, ItemsWithCursor[R]]])
          .mapResponse {
            case Left(value) => throw value.error
            case Right(Left(cdpApiError)) =>
              throw cdpApiError.asException(uriWithCursorAndPartition)
            case Right(Right(value)) => value
          }
      }
  }
}

trait RetrieveByIds[R, F[_]] extends WithRequestSession[F] with BaseUri {
  /**
  * Retrieve elements of this resource by IDs
   * @param ids IDs of the resources to retrieve
   * @return Elemernts of this resource corresponding to IDs
   */
  def retrieveByIds(ids: Seq[Long]): F[Seq[R]]

  /**
  * Retrieve a single element of this resource by its ID
   * @param id ID of the element to retrieve
   * @return The element matching id, if it exists, or None if it does not
   */
  def retrieveById(id: Long): F[Option[R]] =
    requestSession.map(retrieveByIds(Seq(id)), (r1: Seq[R]) => r1.headOption)
}

object RetrieveByIds {
  implicit val cogniteIdEncoder: Encoder[CogniteId] = deriveEncoder
  implicit val cogniteIdItemsEncoder: Encoder[Items[CogniteId]] = deriveEncoder

  def retrieveByIds[F[_], R](requestSession: RequestSession[F], baseUri: Uri, ids: Seq[Long])(
      implicit itemsDecoder: Decoder[Items[R]]
  ): F[Seq[R]] = {
    implicit val errorOrItemsDecoder: Decoder[Either[CdpApiError, Items[R]]] =
      EitherDecoder.eitherDecoder[CdpApiError, Items[R]]
    requestSession
      .sendCdf { request =>
        request
          .post(uri"$baseUri/byids")
          .body(Items(ids.map(CogniteId)))
          .response(asJson[Either[CdpApiError, Items[R]]])
          .mapResponse {
            case Left(value) => throw value.error
            case Right(Left(cdpApiError)) => throw cdpApiError.asException(uri"$baseUri/byids")
            case Right(Right(value)) => value.items
          }
      }
  }
}

trait RetrieveByExternalIds[R, F[_]] extends WithRequestSession[F] with BaseUri {
  def retrieveByExternalIds(externalIds: Seq[String]): F[Seq[R]]

  /**
  * Retrieve a single item of this resource by its external ID
   * @param externalIds external ID of the item to retrieve
   * @return The element of this resource correspondign to externalIDs if it exists, or none if it does not
   */
  def retrieveByExternalId(externalIds: String): F[Option[R]] =
    requestSession.map(retrieveByExternalIds(Seq(externalIds)), (r1: Seq[R]) => r1.headOption)
}

object RetrieveByExternalIds {
  implicit val cogniteExternalIdEncoder: Encoder[CogniteExternalId] = deriveEncoder
  implicit val cogniteExternalIdItemsEncoder: Encoder[Items[CogniteExternalId]] = deriveEncoder

  def retrieveByExternalIds[F[_], R](
      requestSession: RequestSession[F],
      baseUri: Uri,
      externalIds: Seq[String]
  )(
      implicit itemsDecoder: Decoder[Items[R]]
  ): F[Seq[R]] = {
    implicit val errorOrItemsDecoder: Decoder[Either[CdpApiError, Items[R]]] =
      EitherDecoder.eitherDecoder[CdpApiError, Items[R]]
    requestSession
      .sendCdf { request =>
        request
          .post(uri"$baseUri/byids")
          .body(Items(externalIds.map(CogniteExternalId)))
          .response(asJson[Either[CdpApiError, Items[R]]])
          .mapResponse {
            case Left(value) => throw value.error
            case Right(Left(cdpApiError)) => throw cdpApiError.asException(uri"$baseUri/byids")
            case Right(Right(value)) => value.items
          }
      }
  }
}
