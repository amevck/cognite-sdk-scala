package com.cognite.sdk.scala.v1

import java.time.Instant

import com.cognite.sdk.scala.common.{NonNullableSetter, SearchQuery, Setter, WithExternalId, WithId}

/**
* @constructor Time Series Resource
 * @param name Name of the time series
 * @param isString True if time series data points are string-valued
 * @param metadata Metadata associated with the time series
 * @param unit Physical unit of the time series
 * @param assetId ID of the asset connected to this time series
 * @param isStep True if this time series is a step series
 * @param description Description of the time series
 * @param securityCategories Required security categories to access this time series
 * @param id Server generated ID of this time series
 * @param externalId External ID of this time series
 * @param createdTime When this time series was created
 * @param lastUpdatedTime Last time this time series was updated
 */
final case class TimeSeries(
    name: String,
    isString: Boolean = false,
    metadata: Option[Map[String, String]] = None,
    unit: Option[String] = None,
    assetId: Option[Long] = None,
    isStep: Boolean = false,
    description: Option[String] = None,
    securityCategories: Option[Seq[Long]] = None,
    id: Long = 0,
    externalId: Option[String] = None,
    createdTime: Instant = Instant.ofEpochMilli(0),
    lastUpdatedTime: Instant = Instant.ofEpochMilli(0)
) extends WithId[Long]
    with WithExternalId

/**
* @constructor Use for creation of time series
 * @param externalId External ID of the time series
 * @param name Name of the time series
 * @param legacyName Name for API v0.3, 04, 05, and 0.6 to access the time series
 * @param isString True if time series is string-valued
 * @param metadata Key-value metadata
 * @param unit Physical unit of the time series
 * @param assetId ID of asset connected to this time series
 * @param isStep True if this time series is a step series
 * @param description Description of this time series
 * @param securityCategories Required security categories to access this time series
 */
final case class TimeSeriesCreate(
    externalId: Option[String] = None,
    name: String,
    legacyName: Option[String] = None,
    isString: Boolean = false,
    metadata: Option[Map[String, String]] = None,
    unit: Option[String] = None,
    assetId: Option[Long] = None,
    isStep: Boolean = false,
    description: Option[String] = None,
    securityCategories: Option[Seq[Long]] = None
) extends WithExternalId

/**
* @constructor Use for updating time series
 * @param id Existing ID of the time series
 * @param name New name of the time series
 * @param externalId New external ID
 * @param metadata New metadata
 * @param unit New physical unit
 * @param assetId New asset ID connected to this time series
 * @param description New description
 * @param securityCategories New security categories
 */
final case class TimeSeriesUpdate(
    id: Long = 0,
    name: Option[Setter[String]] = None,
    externalId: Option[Setter[String]] = None,
    metadata: Option[NonNullableSetter[Map[String, String]]] = None,
    unit: Option[Setter[String]] = None,
    assetId: Option[Setter[Long]] = None,
    description: Option[Setter[String]] = None,
    securityCategories: Option[Setter[Seq[Long]]] = None
) extends WithId[Long]

/**
* @constructor Search for time series matching specific filter
 * @param name Match time series with this name
 * @param unit Match time series of this physical unit
 * @param isString Match string-valued time series or non string-valued time series
 * @param isStep Match step series or non step series
 * @param metadata Match time series with this metadata
 * @param assetIds Match time series connected to these assets
 * @param externalIdPrefix Match time series whose external ID begins with this prefix
 * @param createdTime Match time series created in this time range
 * @param lastUpdatedTime Match time series last updated in this time range
 */
final case class TimeSeriesSearchFilter(
    name: Option[String] = None,
    unit: Option[String] = None,
    isString: Option[Boolean] = None,
    isStep: Option[Boolean] = None,
    metadata: Option[Map[String, String]] = None,
    assetIds: Option[Seq[Long]] = None,
    externalIdPrefix: Option[String] = None,
    createdTime: Option[TimeRange] = None,
    lastUpdatedTime: Option[TimeRange] = None
)

/**
* @constructor Filter time series for specific assets
 * @param assetIds Filter time series connected to these assets
 */
final case class TimeSeriesFilter(
    assetIds: Option[Seq[Long]] = None
)

/**
* @constructor Search for time series
 * @param name Name to search for
 * @param description Description to search for
 * @param query String query to search for
 */
final case class TimeSeriesSearch(
    name: Option[String] = None,
    description: Option[String] = None,
    query: Option[String] = None
)

/**
* @constructor Query for time series matching a filter or search
 * @param filter TimeSeriesSearchFilter to query with
 * @param search TimeSeriesSearch to query with
 * @param limit Maximum number of results to return
 */
final case class TimeSeriesQuery(
    filter: Option[TimeSeriesSearchFilter] = None,
    search: Option[TimeSeriesSearch] = None,
    limit: Int = 100
) extends SearchQuery[TimeSeriesSearchFilter, TimeSeriesSearch]
