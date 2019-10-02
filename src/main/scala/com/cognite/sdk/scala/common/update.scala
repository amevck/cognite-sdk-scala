package com.cognite.sdk.scala.common

import com.cognite.sdk.scala.v1._

import com.softwaremill.sttp._
import com.softwaremill.sttp.circe._
import io.circe.{Decoder, Encoder, Json, Printer}
import io.circe.syntax._
import io.circe.derivation.deriveEncoder
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._

final case class UpdateRequest(update: Json, id: Long)

trait Update[R <: WithId[Long], U <: WithId[Long], F[_]]
    extends WithRequestSession[F]
    with BaseUri {
  def update(items: Seq[U]): F[Seq[R]]

  /**
  * Update elements of this resource after reading them
   * @param items Sequence of elements of this resource
   * @param t Function from this resource to its update utility case class
   * @return Sequence of updated elements
   */
  def updateFromRead(items: Seq[R])(
      implicit t: Transformer[R, U]
  ): F[Seq[R]] =
    update(items.map(_.transformInto[U]))

  /**
  * Update an element of this resource
   * @param item Update utility object
   * @return The updated item
   */
  def updateOne(item: U): F[R] =
    requestSession.map(
      update(Seq(item)),
      (r1: Seq[R]) =>
        r1.headOption match {
          case Some(value) => value
          case None => throw SdkException("Unexpected empty response when updating item")
        }
    )

  /**
  * Update an element after reading it
   * @param item Read item
   * @param t Transformer from this resource to its update utility case class
   * @return The updated element of this resource
   */
  def updateOneFromRead(item: R)(
      implicit t: Transformer[R, U]
  ): F[R] =
    requestSession.map(
      updateFromRead(Seq(item)),
      (r1: Seq[R]) =>
        r1.headOption match {
          case Some(value) => value
          case None => throw SdkException("Unexpected empty response when updating item")
        }
    )
}

object Update {
  implicit val updateRequestEncoder: Encoder[UpdateRequest] = deriveEncoder
  implicit val updateRequestItemsEncoder: Encoder[Items[UpdateRequest]] = deriveEncoder
  def update[F[_], R, U <: WithId[Long]: Encoder](
      requestSession: RequestSession[F],
      baseUri: Uri,
      updates: Seq[U]
  )(implicit decodeReadItems: Decoder[Items[R]]): F[Seq[R]] = {
    require(updates.forall(_.id > 0), "Update requires an id to be set")
    implicit val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)
    implicit val errorOrItemsDecoder: Decoder[Either[CdpApiError, Items[R]]] =
      EitherDecoder.eitherDecoder[CdpApiError, Items[R]]
    requestSession
      .sendCdf { request =>
        request
          .post(uri"$baseUri/update")
          .body(Items(updates.map { update =>
            UpdateRequest(update.asJson.mapObject(_.remove("id")), update.id)
          }))
          .response(asJson[Either[CdpApiError, Items[R]]])
          .mapResponse {
            case Left(value) =>
              throw value.error
            case Right(Left(cdpApiError)) => throw cdpApiError.asException(uri"$baseUri/update")
            case Right(Right(value)) => value.items
          }
      }
  }
}
