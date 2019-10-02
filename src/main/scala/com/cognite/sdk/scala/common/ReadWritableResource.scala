package com.cognite.sdk.scala.common

import com.cognite.sdk.scala.v1._
import com.softwaremill.sttp._
import com.softwaremill.sttp.circe._
import io.circe.derivation.deriveEncoder
import io.circe.{Decoder, Encoder}
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._

/**
* Trait common to resource that can be deleted by their IDs. For internal usage.
 * @tparam F Function over resource type
 * @tparam PrimitiveId ID of the resource
 */
trait DeleteByIds[F[_], PrimitiveId] {
  def deleteByIds(ids: Seq[PrimitiveId]): F[Unit]

  /**
  * Delete a resource by its ID
   * @param id ID of the resource to delete
   * @return Unit
   */
  def deleteById(id: PrimitiveId): F[Unit] = deleteByIds(Seq(id))
}

/**
* For internal usage. Use the delete method specific to a resource type
 */
object DeleteByIds {
  implicit val cogniteIdEncoder: Encoder[CogniteId] = deriveEncoder
  implicit val cogniteIdItemsEncoder: Encoder[Items[CogniteId]] = deriveEncoder

  def deleteByIds[F[_]](
      requestSession: RequestSession[F],
      baseUri: Uri,
      ids: Seq[Long]
  ): F[Unit] = {
    implicit val errorOrUnitDecoder: Decoder[Either[CdpApiError, Unit]] =
      EitherDecoder.eitherDecoder[CdpApiError, Unit]
    // TODO: group deletes by max deletion request size
    //       or assert that length of `ids` is less than max deletion request size
    requestSession
      .sendCdf { request =>
        request
          .post(uri"$baseUri/delete")
          .body(Items(ids.map(CogniteId)))
          .response(asJson[Either[CdpApiError, Unit]])
          .mapResponse {
            case Left(value) => throw value.error
            case Right(Left(cdpApiError)) => throw cdpApiError.asException(uri"$baseUri/delete")
            case Right(Right(_)) => ()
          }
      }
  }
}

/**
* Trait common to resources that can be deleted by their external IDs. For internal usage
 * @tparam F Function over resource type
 */
trait DeleteByExternalIds[F[_]] {
  /**
  * Delete resources by their external IDs
   * @param externalIds External IDs of the resources to delete
   * @return Unit
   */
  def deleteByExternalIds(externalIds: Seq[String]): F[Unit]

  /**
  * Delete a resource by its external ID
   * @param externalId External ID of the resource to delete
   * @return Unit
   */
  def deleteByExternalId(externalId: String): F[Unit] = deleteByExternalIds(Seq(externalId))
}

/**
* For internal usage. Use the delete methods specific to a resource type.
 */
object DeleteByExternalIds {
  implicit val cogniteExternalIdEncoder: Encoder[CogniteExternalId] = deriveEncoder
  implicit val cogniteExternalIdItemsEncoder: Encoder[Items[CogniteExternalId]] = deriveEncoder
  implicit val errorOrUnitDecoder: Decoder[Either[CdpApiError, Unit]] =
    EitherDecoder.eitherDecoder[CdpApiError, Unit]

  def deleteByExternalIds[F[_]](
      requestSession: RequestSession[F],
      baseUri: Uri,
      externalIds: Seq[String]
  ): F[Unit] =
    // TODO: group deletes by max deletion request size
    //       or assert that length of `ids` is less than max deletion request size
    requestSession
      .sendCdf { request =>
        request
          .post(uri"$baseUri/delete")
          .body(Items(externalIds.map(CogniteExternalId)))
          .response(asJson[Either[CdpApiError, Unit]])
          .mapResponse {
            case Left(value) => throw value.error
            case Right(Left(cdpApiError)) => throw cdpApiError.asException(uri"$baseUri/delete")
            case Right(Right(_)) => ()
          }
      }
}

/**
* Trait common to resources that can be created.
 * @tparam R
 * @tparam W
 * @tparam F
 */
trait Create[R, W, F[_]] extends WithRequestSession[F] with CreateOne[R, W, F] with BaseUri {
  private[sdk] def createItems(items: Items[W]): F[Seq[R]]

  /**
  * Create elements of this resource
   * @param items Elements to craete
   * @return Sequence of created elements
   */
  def create(items: Seq[W]): F[Seq[R]] =
    createItems(Items(items))

  /**
  * Create items of this resource
   * @param items Items to create
   * @param t Transformer from this resource type to its create utility case class
   * @return Created items
   */
  def createFromRead(items: Seq[R])(
      implicit t: Transformer[R, W]
  ): F[Seq[R]] =
    createItems(Items(items.map(_.transformInto[W])))

  /**
  * Create a single element of this resource
   * @param item Element to create
   * @return Created element
   */
  def createOne(item: W): F[R] =
    requestSession.map(
      create(Seq(item)),
      (r1: Seq[R]) =>
        r1.headOption match {
          case Some(value) => value
          case None => throw SdkException("Unexpected empty response when creating item")
        }
    )
}

/**
* For internal usage. Use the create method of a specific resource.
 */
object Create {
  def createItems[F[_], R, W](requestSession: RequestSession[F], baseUri: Uri, items: Items[W])(
      implicit readDecoder: Decoder[ItemsWithCursor[R]],
      itemsEncoder: Encoder[Items[W]]
  ): F[Seq[R]] = {
    implicit val errorOrItemsWithCursorDecoder: Decoder[Either[CdpApiError, ItemsWithCursor[R]]] =
      EitherDecoder.eitherDecoder[CdpApiError, ItemsWithCursor[R]]
    requestSession
      .sendCdf { request =>
        request
          .post(baseUri)
          .body(items)
          .response(asJson[Either[CdpApiError, ItemsWithCursor[R]]])
          .mapResponse {
            case Left(value) =>
              throw value.error
            case Right(Left(cdpApiError)) => throw cdpApiError.asException(baseUri)
            case Right(Right(value)) => value.items
          }
      }
  }
}

/**
* Trait common to resources that can be created. For internal usage. Use the create methods of a specific resource
 * @tparam R
 * @tparam W
 * @tparam F
 */
trait CreateOne[R, W, F[_]] extends WithRequestSession[F] with BaseUri {
  def createOne(item: W): F[R]

  /**
  * Create an item of this resource
   * @param item Resource item to create
   * @param t Transformer from this resource to its create utility case class
   * @return Created item
   */
  def createOneFromRead(item: R)(
      implicit t: Transformer[R, W]
  ): F[R] = createOne(item.transformInto[W])

}

/**
* For internal usage. Use the create methods specific to a resource.
 */
object CreateOne {
  def createOne[F[_], R, W](requestSession: RequestSession[F], baseUri: Uri, item: W)(
      implicit readDecoder: Decoder[R],
      itemsEncoder: Encoder[W]
  ): F[R] = {
    implicit val errorOrItemsWithCursorDecoder: Decoder[Either[CdpApiError, R]] =
      EitherDecoder.eitherDecoder[CdpApiError, R]
    requestSession
      .sendCdf { request =>
        request
          .post(baseUri)
          .body(item)
          .response(asJson[Either[CdpApiError, R]])
          .mapResponse {
            case Left(value) =>
              throw value.error
            case Right(Left(cdpApiError)) => throw cdpApiError.asException(baseUri)
            case Right(Right(value)) => value
          }
      }
  }
}
