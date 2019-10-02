package com.cognite.sdk.scala.v1

import java.time.Instant

import com.cognite.sdk.scala.common.{NonNullableSetter, SearchQuery, Setter, WithExternalId, WithId}

/**
* @constructor Asset Resource Type
 * @param externalId External ID of this asset
 * @param name Name of this asset
 * @param parentId ID of the parent of this asset
 * @param description String description of this asset
 * @param metadata Metadata of this asset
 * @param source Asset source
 * @param id ID of this asset
 * @param createdTime Time of asset creation in CDF
 * @param lastUpdatedTime Last time asset was updated in CDF
 */
final case class Asset(
    externalId: Option[String] = None,
    name: String,
    parentId: Option[Long] = None,
    description: Option[String] = None,
    metadata: Option[Map[String, String]] = None,
    source: Option[String] = None,
    id: Long = 0,
    createdTime: Instant = Instant.ofEpochMilli(0),
    lastUpdatedTime: Instant = Instant.ofEpochMilli(0)
) extends WithId[Long]
    with WithExternalId

/**
* @constructor Object for creating a new asset
 * @param name Name of this asset
 * @param parentId Optional ID of this asset's parent
 * @param description Optional string description of the asset
 * @param source Optional
 * @param externalId Optional externalID of the asset
 * @param metadata Optional asset metadata
 * @param parentExternalId Optional external ID of parent asset
 */
final case class AssetCreate(
    name: String,
    parentId: Option[Long] = None,
    description: Option[String] = None,
    source: Option[String] = None,
    externalId: Option[String] = None,
    metadata: Option[Map[String, String]] = None,
    parentExternalId: Option[String] = None
) extends WithExternalId

/**
* @constructor Object for updating an existing asset
 * @param id ID of the asset to update
 * @param name Updated asset name
 * @param description Updated asset description
 * @param source Updated asset source
 * @param externalId Updated asset external ID
 * @param metadata Updated asset metadata
 */
final case class AssetUpdate(
    id: Long,
    name: Option[NonNullableSetter[String]] = None,
    description: Option[Setter[String]] = None,
    source: Option[Setter[String]] = None,
    externalId: Option[Setter[String]] = None,
    metadata: Option[NonNullableSetter[Map[String, String]]] = None
) extends WithId[Long]

/**
* @constructor Object for filtering assets based on certain criteria
 * @param name Names to match
 * @param parentIds parentIds to match
 * @param metadata Metadata to match
 * @param source Sources to match
 * @param createdTime Match assets created within a TimeRange
 * @param lastUpdatedTime Match assets last updated within a TimeRange
 * @param root Match assets under a root asset
 * @param externalIdPrefix Match assets with a certain prefix in their external IDs
 */
final case class AssetsFilter(
    name: Option[String] = None,
    parentIds: Option[Seq[Long]] = None,
    metadata: Option[Map[String, String]] = None,
    source: Option[String] = None,
    createdTime: Option[TimeRange] = None,
    lastUpdatedTime: Option[TimeRange] = None,
    root: Option[Boolean] = None,
    externalIdPrefix: Option[String] = None
)

/**
* @constructor Search for assets with a name and description
 * @param name Search for assets with this name
 * @param description Search for assets with this description
 */
final case class AssetsSearch(
    name: Option[String] = None,
    description: Option[String] = None
)

/**
* @constructor Query for assets matching a filter or search
 * @param filter Filter to match assets
 * @param search Search to match assets
 * @param limit Maximum number of elements to return. Defaults to 100
 */
final case class AssetsQuery(
    filter: Option[AssetsFilter] = None,
    search: Option[AssetsSearch] = None,
    limit: Int = 100
) extends SearchQuery[AssetsFilter, AssetsSearch]
