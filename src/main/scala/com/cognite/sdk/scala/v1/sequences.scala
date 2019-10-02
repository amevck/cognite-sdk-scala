package com.cognite.sdk.scala.v1

import java.time.Instant

import cats.data.NonEmptyList
import com.cognite.sdk.scala.common.{NonNullableSetter, SearchQuery, Setter, WithExternalId, WithId}

/**
* @constructor Columns of a Sequence
 * @param name Name of the Row
 * @param externalId External ID of the row
 * @param description String description of the row
 * @param valueType Data type of values in the row {String, Integer, Float}
 * @param metadata Metadata associated with this row
 * @param createdTime When this column was created
 * @param lastUpdatedTime Last time this column was updated
 */
final case class SequenceColumn(
    name: Option[String] = None,
    externalId: String,
    description: Option[String] = None,
    // TODO: Turn this into an enum.
    //       See https://github.com/circe/circe-derivation/issues/8
    //       and https://github.com/circe/circe-derivation/pull/91
    valueType: String = "STRING",
    metadata: Option[Map[String, String]] = None,
    createdTime: Instant = Instant.ofEpochMilli(0),
    lastUpdatedTime: Instant = Instant.ofEpochMilli(0)
)

/**
* @constructor Use for creation of a SequenceColumn
 * @param name Name of the column
 * @param externalId External ID of the column
 * @param description Description of the column
 * @param valueType Data type of the column {String, Integer, Float}
 * @param metadata Metadata associated with the column
 */
final case class SequenceColumnCreate(
    name: Option[String] = None,
    externalId: String,
    description: Option[String] = None,
    // TODO: Turn this into an enum.
    //       See https://github.com/circe/circe-derivation/issues/8
    //       and https://github.com/circe/circe-derivation/pull/91
    valueType: String = "STRING",
    metadata: Option[Map[String, String]] = None
)

/**
* @constructor Sequence Resource Type
 * @param id Unique ID for the sequence
 * @param name Name of the sequence
 * @param description String description of the sequence
 * @param assetId Asset associated with this sequence
 * @param externalId External ID of the sequence
 * @param metadata Metadata for this sequence
 * @param columns Nonempty sequence of SequenceColumn that comprise this Sequence
 * @param createdTime Time this sequence was created
 * @param lastUpdatedTime Last time this sequence was updated
 */
final case class Sequence(
    id: Long = 0,
    name: Option[String] = None,
    description: Option[String] = None,
    assetId: Option[Long] = None,
    externalId: Option[String] = None,
    metadata: Option[Map[String, String]] = None,
    columns: NonEmptyList[SequenceColumn],
    createdTime: Instant = Instant.ofEpochMilli(0),
    lastUpdatedTime: Instant = Instant.ofEpochMilli(0)
) extends WithId[Long]
    with WithExternalId

/**
* @constructor Use for Sequence creation
 * @param name Name of the sequence
 * @param description String description of the sequence
 * @param assetId ID of the asset associated with this sequence
 * @param externalId External ID of this sequence
 * @param metadata Metadata associated with this sequence
 * @param columns Nonempty list of SequenceColumnCreate that will comprise the Sequence
 */
final case class SequenceCreate(
    name: Option[String] = None,
    description: Option[String] = None,
    assetId: Option[Long] = None,
    externalId: Option[String] = None,
    metadata: Option[Map[String, String]] = None,
    columns: NonEmptyList[SequenceColumnCreate]
) extends WithExternalId

/**
* @constructor Update an existing sequence
 * @param id Existing ID of the sequence to update
 * @param name Name of the updated sequence
 * @param description Description of the updated sequence
 * @param assetId ID of the asset associated with the updated sequence
 * @param externalId External ID of the updated sequence
 * @param metadata Metadata of the updated sequence
 */
final case class SequenceUpdate(
    id: Long = 0,
    name: Option[Setter[String]] = None,
    description: Option[Setter[String]] = None,
    assetId: Option[Setter[Long]] = None,
    externalId: Option[Setter[String]] = None,
    metadata: Option[NonNullableSetter[Map[String, String]]] = None
) extends WithId[Long]

/**
* @constructor Filter sequences matching certain criteria
 * @param name Filter for this name
 * @param externalIdPrefix  Filter for sequences whose external IDs begin with this prefix
 * @param metadata Filter for this metadata
 * @param assetIds Filter for sequences associated with these assets
 * @param rootAssetIds Filter for sequences associated with these root assets
 * @param createdTime Filter for sequences created within this time range
 * @param lastUpdatedTime Filter for sequences last updated within this time range
 */
final case class SequenceFilter(
    name: Option[String] = None,
    externalIdPrefix: Option[String] = None,
    metadata: Option[Map[String, String]] = None,
    assetIds: Option[Seq[Long]] = None,
    rootAssetIds: Option[Seq[Long]] = None,
    createdTime: Option[TimeRange] = None,
    lastUpdatedTime: Option[TimeRange] = None
)

/**
* @constructor Search for sequences
 * @param name Search for sequences matching this name
 * @param description Search for sequences matching this description
 * @param query String query
 */
final case class SequenceSearch(
    name: Option[String] = None,
    description: Option[String] = None,
    query: Option[String] = None
)

/**
* @constructor Query for sequences matching a filter or search
 * @param filter SequenceFilter to query with
 * @param search SequenceSearch to filter with
 * @param limit Maximum number of elements to return when executing the query
 */
final case class SequenceQuery(
    filter: Option[SequenceFilter] = None,
    search: Option[SequenceSearch] = None,
    limit: Int = 100
) extends SearchQuery[SequenceFilter, SequenceSearch]
