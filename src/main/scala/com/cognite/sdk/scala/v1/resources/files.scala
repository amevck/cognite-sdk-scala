package com.cognite.sdk.scala.v1.resources

import java.time.Instant
import java.io.{BufferedInputStream, FileInputStream}

import cats.syntax.functor._
import com.cognite.sdk.scala.common._
import com.cognite.sdk.scala.v1._
import com.softwaremill.sttp._
import com.softwaremill.sttp.circe._
import io.circe.{Decoder, Encoder}
import io.circe.derivation.{deriveDecoder, deriveEncoder}

class Files[F[_]](val requestSession: RequestSession[F])
    extends WithRequestSession[F]
    with Readable[File, F]
    with RetrieveByIds[File, F]
    with RetrieveByExternalIds[File, F]
    with CreateOne[File, FileCreate, F]
    with DeleteByIds[F, Long]
    with DeleteByExternalIds[F]
    with Filter[File, FilesFilter, F]
    with Search[File, FilesQuery, F]
    with Update[File, FileUpdate, F] {
  import Files._
  override val baseUri = uri"${requestSession.baseUri}/files"

  implicit val errorOrFileDecoder: Decoder[Either[CdpApiError, File]] =
    EitherDecoder.eitherDecoder[CdpApiError, File]

  /**
  * Create a File. See FileCreate documentation
   * @param item File to create
   * @return The created File
   */
  override def createOne(item: FileCreate): F[File] =
    requestSession
      .sendCdf { request =>
        request
          .post(baseUri)
          .body(item)
          .response(asJson[Either[CdpApiError, File]])
          .mapResponse {
            case Left(value) => throw value.error
            case Right(Left(cdpApiError)) =>
              throw cdpApiError.asException(uri"$baseUri/byids")
            case Right(Right(value)) => value
          }
      }

  /**
  * Upload a File with a specific name
   * @param input Input stream specifying File
   * @param name Intended name of the File
   * @return The uploaded File
   */
  def uploadWithName(input: java.io.InputStream, name: String): F[File] = {
    val item = FileCreate(name = name)
    requestSession.flatMap(
      createOne(item),
      (file: File) =>
        file.uploadUrl match {
          case Some(uploadUrl) =>
            val response = requestSession.send { request =>
              request
                .body(input)
                .put(uri"$uploadUrl")
            }
            requestSession.map(
              response,
              (res: Response[String]) =>
                if (res.isSuccess) {
                  file
                } else {
                  throw SdkException(
                    s"File upload of file ${file.name} failed with error code ${res.code.toString()}"
                  )
                }
            )
          case None =>
            throw SdkException(s"File upload of file ${file.name} did not return uploadUrl")
        }
    )
  }

  /**
  * Upload a File
   * @param file File to upload
   * @return The uploaded File
   */
  def upload(file: java.io.File): F[File] = {
    val inputStream = new BufferedInputStream(new FileInputStream(file))
    uploadWithName(inputStream, file.getName())
  }

  override private[sdk] def readWithCursor(
      cursor: Option[String],
      limit: Option[Int],
      partition: Option[Partition]
  ): F[ItemsWithCursor[File]] =
    Readable.readWithCursor(requestSession, baseUri, cursor, limit, None)

  /**
  * Retrieve Files by their IDs
   * @param ids IDs of the Files to retrieve
   * @return Files corresponding to ids
   */
  override def retrieveByIds(ids: Seq[Long]): F[Seq[File]] =
    RetrieveByIds.retrieveByIds(requestSession, baseUri, ids)

  /**
  * Retrieve Files by their external IDs
   * @param externalIds External IDs of the Files to retrieve
   * @return Files corresponding to externalIds
   */
  override def retrieveByExternalIds(externalIds: Seq[String]): F[Seq[File]] =
    RetrieveByExternalIds.retrieveByExternalIds(requestSession, baseUri, externalIds)

  /**
  * Update Files. See the documentation on FileUpdate
   * @param items Events to update
   * @return Updated Files
   */
  override def update(items: Seq[FileUpdate]): F[Seq[File]] =
    Update.update[F, File, FileUpdate](requestSession, baseUri, items)

  /**
  * Delete Files based on their IDs
   * @param ids IDs of the Files to delete
   * @return Unit
   */
  override def deleteByIds(ids: Seq[Long]): F[Unit] =
    DeleteByIds.deleteByIds(requestSession, baseUri, ids)

  /**
  * Delete Files based on their external IDs
   * @param externalIds External IDs of the Files to delete
   * @return Unit
   */
  override def deleteByExternalIds(externalIds: Seq[String]): F[Unit] =
    DeleteByExternalIds.deleteByExternalIds(requestSession, baseUri, externalIds)

  private[sdk] def filterWithCursor(
      filter: FilesFilter,
      cursor: Option[String],
      limit: Option[Int],
      partition: Option[Partition]
  ): F[ItemsWithCursor[File]] =
    Filter.filterWithCursor(requestSession, baseUri, filter, cursor, limit, None)

  /**
  * Search for Files matching a specified query. See the documentation on FilesQuery
   * @param searchQuery Query to match Files
   * @return Files matching the specified query
   */
  override def search(searchQuery: FilesQuery): F[Seq[File]] =
    Search.search(requestSession, baseUri, searchQuery)

  /**
  * Download a file. See the documentation on FileDownload
   * @param item File to download
   * @param out Outstream where the downloaded File will go
   * @return Unit
   */
  def download(item: FileDownload, out: java.io.OutputStream): F[Unit] = {
    val request =
      requestSession
        .sendCdf { request =>
          request
            .post(uri"${baseUri.toString()}/downloadlink")
            .body(Items(Seq(item)))
            .response(asJson[Either[CdpApiError, Items[FileDownloadLink]]])
            .mapResponse {
              case Left(value) => throw value.error
              case Right(Left(cdpApiError)) =>
                throw cdpApiError.asException(uri"${baseUri.toString()}/downloadlink")
              case Right(Right(values)) => values

            }
        }

    requestSession.flatMap(
      request,
      (files: Items[FileDownloadLink]) => {
        val response = requestSession.send { request =>
          request
            .get(
              uri"${files.items
                .map(_.downloadUrl)
                .headOption
                .getOrElse(throw SdkException(s"File download of ${item.toString()} did not return download url"))}"
            )
            .response(asByteArray)
        }
        requestSession.map(
          response,
          (res: Response[Array[Byte]]) =>
            if (res.isSuccess) {
              out.write(res.unsafeBody)
            } else {
              throw SdkException(
                s"File download of file ${item.toString()} failed with error code ${res.code.toString()}"
              )
            }
        )
      }
    )
  }
}

object Files {
  implicit val instantEncoder: Encoder[Instant] = Encoder.encodeLong.contramap(_.toEpochMilli)
  implicit val instantDecoder: Decoder[Instant] = Decoder.decodeLong.map(Instant.ofEpochMilli)

  implicit val fileItemsWithCursorDecoder: Decoder[ItemsWithCursor[File]] =
    deriveDecoder[ItemsWithCursor[File]]
  implicit val fileDecoder: Decoder[File] = deriveDecoder[File]
  implicit val fileItemsDecoder: Decoder[Items[File]] =
    deriveDecoder[Items[File]]
  implicit val createFileEncoder: Encoder[FileCreate] =
    deriveEncoder[FileCreate]
  implicit val createFileItemsEncoder: Encoder[Items[FileCreate]] =
    deriveEncoder[Items[FileCreate]]
  implicit val fileUpdateEncoder: Encoder[FileUpdate] =
    deriveEncoder[FileUpdate]
  implicit val updateFilesItemsEncoder: Encoder[Items[FileUpdate]] =
    deriveEncoder[Items[FileUpdate]]
  implicit val filesFilterEncoder: Encoder[FilesFilter] =
    deriveEncoder[FilesFilter]
  implicit val filesSearchEncoder: Encoder[FilesSearch] =
    deriveEncoder[FilesSearch]
  implicit val filesQueryEncoder: Encoder[FilesQuery] =
    deriveEncoder[FilesQuery]
  implicit val filesFilterRequestEncoder: Encoder[FilterRequest[FilesFilter]] =
    deriveEncoder[FilterRequest[FilesFilter]]
  implicit val fileDownloadLinkIdDecoder: Decoder[FileDownloadLinkId] =
    deriveDecoder[FileDownloadLinkId]
  implicit val fileDownloadLinkExternalIdDecoder: Decoder[FileDownloadLinkExternalId] =
    deriveDecoder[FileDownloadLinkExternalId]
  implicit val fileDownloadIdEncoder: Encoder[FileDownloadId] =
    deriveEncoder[FileDownloadId]
  implicit val fileDownloadExternalIdEncoder: Encoder[FileDownloadExternalId] =
    deriveEncoder[FileDownloadExternalId]
  implicit val fileDownloadEncoder: Encoder[FileDownload] = Encoder.instance {
    case downloadId @ FileDownloadId(_) => fileDownloadIdEncoder(downloadId)
    case downloadExternalId @ FileDownloadExternalId(_) =>
      fileDownloadExternalIdEncoder(downloadExternalId)
  }
  implicit val fileDownloadLinkDecoder: Decoder[FileDownloadLink] =
    fileDownloadLinkIdDecoder.widen.or(fileDownloadLinkExternalIdDecoder.widen)
  implicit val fileDownloadItemsEncoder: Encoder[Items[FileDownload]] =
    deriveEncoder[Items[FileDownload]]
  implicit val fileDownloadItemsDecoder: Decoder[Items[FileDownloadLink]] =
    deriveDecoder[Items[FileDownloadLink]]
  implicit val fileDownloadResponseDecoder: Decoder[Either[CdpApiError, Items[FileDownloadLink]]] =
    EitherDecoder.eitherDecoder[CdpApiError, Items[FileDownloadLink]]
}
