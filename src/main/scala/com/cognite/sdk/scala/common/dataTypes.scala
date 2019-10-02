package com.cognite.sdk.scala.common

import java.time.Instant

import com.softwaremill.sttp.Uri
import io.circe.{Decoder, Encoder, Json, JsonObject}
import io.circe.derivation.deriveDecoder
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._

final case class ItemsWithCursor[A](items: Seq[A], nextCursor: Option[String] = None)
final case class Items[A](items: Seq[A])
private[sdk] final case class SdkException(message: String) extends Throwable(message)
private[sdk] final case class CdpApiErrorPayload(
    code: Int,
    message: String,
    missing: Option[Seq[JsonObject]],
    duplicated: Option[Seq[JsonObject]],
    missingFields: Option[Seq[String]]
)
private[sdk] final case class CdpApiError(error: CdpApiErrorPayload) {
  def asException(url: Uri): CdpApiException =
    this.error
      .into[CdpApiException]
      .withFieldConst(_.url, url)
      .transform
}
private[sdk] object CdpApiError {
  implicit val cdpApiErrorPayloadDecoder: Decoder[CdpApiErrorPayload] = deriveDecoder
  implicit val cdpApiErrorDecoder: Decoder[CdpApiError] = deriveDecoder
}
private[sdk] final case class CdpApiException(
    url: Uri,
    code: Int,
    message: String,
    missing: Option[Seq[JsonObject]],
    duplicated: Option[Seq[JsonObject]],
    missingFields: Option[Seq[String]]
) extends Throwable(s"Request to ${url.toString()} failed with status ${code.toString}: $message")

private[sdk] final case class CogniteId(id: Long)

/**
* Data point returned by queries and retrievals
 * @param timestamp Timestamp of the datapoint
 * @param value Value of the data point
 */
final case class DataPoint(
    timestamp: Instant,
    value: Double
)

private[sdk] final case class AggregateDataPoint(
    timestamp: Instant,
    average: Option[Double],
    max: Option[Double],
    min: Option[Double],
    count: Option[Double],
    sum: Option[Double],
    interpolation: Option[Double],
    stepInterpolation: Option[Double],
    totalVariation: Option[Double],
    continuousVariance: Option[Double],
    discreteVariance: Option[Double]
)

/**
* String-valued data point returned by string queries and retrievals
 * @param timestamp Timestamp of the data point
 * @param value String value of the data point
 */
final case class StringDataPoint(
    timestamp: Instant,
    value: String
)

private[sdk] trait WithId[I] {
  val id: I
}

private[sdk] trait WithExternalId {
  val externalId: Option[String]
}

/**
* Decoder for internal usage
 */
object EitherDecoder {
  def eitherDecoder[A, B](implicit a: Decoder[A], b: Decoder[B]): Decoder[Either[A, B]] = {
    val l: Decoder[Either[A, B]] = a.map(Left.apply)
    val r: Decoder[Either[A, B]] = b.map(Right.apply)
    l.or(r)
  }
}

private[sdk] sealed trait Setter[+T]
private[sdk] sealed trait NonNullableSetter[+T]
private[sdk] final case class Set[+T](set: T) extends Setter[T] with NonNullableSetter[T]
private[sdk] final case class SetNull[+T]() extends Setter[T]

private[sdk] object Setter {
  @SuppressWarnings(Array("org.wartremover.warts.Null", "scalafix:DisableSyntax.null"))
  implicit def optionToSetter[T: Manifest]: Transformer[Option[T], Option[Setter[T]]] =
    new Transformer[Option[T], Option[Setter[T]]] {
      override def transform(src: Option[T]) = src match {
        case null => Some(SetNull()) // scalastyle:ignore null
        case None => None
        case Some(null) => Some(SetNull()) // scalastyle:ignore null
        case Some(value: T) => Some(Set(value))
      }
    }

  @SuppressWarnings(Array("org.wartremover.warts.Null", "scalafix:DisableSyntax.null"))
  implicit def anyToSetter[T]: Transformer[T, Option[Setter[T]]] =
    new Transformer[T, Option[Setter[T]]] {
      override def transform(src: T): Option[Setter[T]] = src match {
        case null => Some(SetNull()) // scalastyle:ignore null
        case value => Some(Set(value))
      }
    }

  implicit def encodeSetter[T](implicit encodeT: Encoder[T]): Encoder[Setter[T]] =
    new Encoder[Setter[T]] {
      final def apply(a: Setter[T]): Json = a match {
        case Set(value) => Json.obj(("set", encodeT.apply(value)))
        case SetNull() => Json.obj(("setNull", Json.True))
      }
    }
}

private[sdk] object NonNullableSetter {
  @SuppressWarnings(
    Array(
      "org.wartremover.warts.Null",
      "org.wartremover.warts.Equals",
      "scalafix:DisableSyntax.null",
      "scalafix:DisableSyntax.!="
    )
  )
  implicit def optionToNonNullableSetter[T: Manifest]
      : Transformer[Option[T], Option[NonNullableSetter[T]]] =
    new Transformer[Option[T], Option[NonNullableSetter[T]]] {
      override def transform(src: Option[T]): Option[NonNullableSetter[T]] = src match {
        case None => None
        case Some(value: T) =>
          require(value != null, "Invalid null value for non-nullable field update") // scalastyle:ignore null
          Some(Set(value))
      }
    }

  implicit def toNonNullableSetter[T: Manifest]: Transformer[T, NonNullableSetter[T]] =
    new Transformer[T, NonNullableSetter[T]] {
      override def transform(value: T): NonNullableSetter[T] = Set(value)
    }

  implicit def toOptionNonNullableSetter[T: Manifest]
      : Transformer[T, Option[NonNullableSetter[T]]] =
    new Transformer[T, Option[NonNullableSetter[T]]] {
      override def transform(value: T): Option[NonNullableSetter[T]] = Some(Set(value))
    }

  implicit def encodeNonNullableSetter[T](
      implicit encodeT: Encoder[T]
  ): Encoder[NonNullableSetter[T]] = new Encoder[NonNullableSetter[T]] {
    final def apply(a: NonNullableSetter[T]): Json = a match {
      case Set(value) => Json.obj(("set", encodeT.apply(value)))
    }
  }
}
