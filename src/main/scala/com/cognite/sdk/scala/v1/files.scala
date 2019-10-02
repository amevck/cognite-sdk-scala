package com.cognite.sdk.scala.v1

import java.time.Instant

import com.cognite.sdk.scala.common.{NonNullableSetter, SearchQuery, Setter, WithExternalId, WithId}

/**
* @constructor Files Resource Type
 * @param id Unique file ID
 * @param name Name of this file
 * @param source Source of this file
 * @param externalId External ID of this file
 * @param mimeType File type
 * @param metadata File metadata
 * @param assetIds Assets in this file
 * @param uploaded Whether this file has been successfully uploaded to CDF
 * @param uploadedTime When this file was uploaded, or None if it has not been uploaded
 * @param createdTime Time of file creation
 * @param lastUpdatedTime Time file was last updated
 * @param uploadUrl Google Cloud Storage resumable upload URL
 */
final case class File(
    id: Long = 0,
    name: String,
    source: Option[String] = None,
    externalId: Option[String] = None,
    mimeType: Option[String] = None,
    metadata: Option[Map[String, String]] = None,
    assetIds: Option[Seq[Long]] = None,
    uploaded: Boolean = false,
    uploadedTime: Option[Instant] = None,
    createdTime: Instant = Instant.ofEpochMilli(0),
    lastUpdatedTime: Instant = Instant.ofEpochMilli(0),
    uploadUrl: Option[String] = None
) extends WithId[Long]
    with WithExternalId

/**
* @constructor Use to create a new file
 * @param name Name of the file
 * @param source Source of the file
 * @param externalId External ID of the file
 * @param mimeType File type
 * @param metadata File metadata
 * @param assetIds Assets connected to this file
 */
final case class FileCreate(
    name: String,
    source: Option[String] = None,
    externalId: Option[String] = None,
    mimeType: Option[String] = None,
    metadata: Option[Map[String, String]] = None,
    assetIds: Option[Seq[Long]] = None
)

/**
* @constructor Update an existing file
 * @param id Existing ID of this file
 * @param externalId External ID of this file
 * @param source Source of this file
 * @param metadata Metadata of this file
 * @param assetIds Assets connected to this file
 */
final case class FileUpdate(
    id: Long = 0,
    externalId: Option[Setter[String]] = None,
    source: Option[Setter[String]] = None,
    metadata: Option[NonNullableSetter[Map[String, String]]] = None,
    assetIds: Option[NonNullableSetter[Seq[Long]]] = None
) extends WithId[Long]

/**
* @constructor Filter files based on certain criteria
 * @param name Match files with this name
 * @param mimeType Match files of this type
 * @param metadata Match files with this metadata
 * @param assetIds Match files connected to these assets
 * @param source Match files with this source
 * @param createdTime Match files created within this time range
 * @param lastUpdatedTime Match files lat updated within this time range
 * @param uploadedTime Match files uploaded within this time range
 * @param externalIdPrefix Match files whose external ID begins with this prefix
 * @param uploaded Match files which either are or are not uploaded
 */
final case class FilesFilter(
    name: Option[String] = None,
    mimeType: Option[String] = None,
    metadata: Option[Map[String, String]] = None,
    assetIds: Option[Seq[Long]] = None,
    source: Option[String] = None,
    createdTime: Option[TimeRange] = None,
    lastUpdatedTime: Option[TimeRange] = None,
    uploadedTime: Option[TimeRange] = None,
    externalIdPrefix: Option[String] = None,
    uploaded: Option[Boolean] = None
)

/**
* @constructor Search for files with a specific name
 * @param name Name of the file to match
 */
final case class FilesSearch(
    name: Option[String]
)

/**
* @constructor Query for files matching a certain filter or search
 * @param filter Filter files. See FilesFilter
 * @param search Search for files. See FilesSearch
 * @param limit Maximum number of files returned by executing this query
 */
final case class FilesQuery(
    filter: Option[FilesFilter] = None,
    search: Option[FilesSearch] = None,
    limit: Int = 100
) extends SearchQuery[FilesFilter, FilesSearch]

/**
* For internal usage
 */
sealed trait FileDownload
/**
 * For internal usage
 */
final case class FileDownloadId(id: Long) extends FileDownload
/**
 * For internal usage
 */
final case class FileDownloadExternalId(externalId: String) extends FileDownload

/**
 * For internal usage
 */
sealed trait FileDownloadLink {
  def downloadUrl: String
}

/**
* For internal usage
 * @param id
 * @param downloadUrl
 */
final case class FileDownloadLinkId(
    id: Long,
    downloadUrl: String
) extends FileDownloadLink

/**
* For internal usage
 * @param externalId
 * @param downloadUrl
 */
final case class FileDownloadLinkExternalId(
    externalId: String,
    downloadUrl: String
) extends FileDownloadLink
