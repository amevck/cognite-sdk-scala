package com.cognite.sdk.scala.v1

import java.time.Instant

import com.cognite.sdk.scala.common.{NonNullableSetter, SearchQuery, Setter, WithExternalId, WithId}

/**
* @constructor Events Resource Type
 * @param id ID of the event
 * @param startTime When the event started
 * @param endTime When the event ended
 * @param description String description of the event
 * @param `type` Type of event
 * @param subtype Subtype of this event
 * @param metadata Metadata associated with this event
 * @param assetIds IDs of assets connected with this event
 * @param source Source of this event
 * @param externalId External ID of this event
 * @param createdTime Creation time of this event in CDF
 * @param lastUpdatedTime Last time this event was updated in CDF
 */
final case class Event(
    id: Long = 0,
    startTime: Option[Instant] = None,
    endTime: Option[Instant] = None,
    description: Option[String] = None,
    `type`: Option[String] = None,
    subtype: Option[String] = None,
    metadata: Option[Map[String, String]] = None,
    assetIds: Option[Seq[Long]] = None,
    source: Option[String] = None,
    externalId: Option[String] = None,
    createdTime: Instant = Instant.ofEpochMilli(0),
    lastUpdatedTime: Instant = Instant.ofEpochMilli(0)
) extends WithId[Long]
    with WithExternalId

/**
* @constructor Use to create event
 * @param startTime Start time of the event
 * @param endTime End time of the event
 * @param description String description of the event
 * @param `type` Type of event
 * @param subtype Subtype of the event
 * @param metadata Metadata associated with the event
 * @param assetIds IDs of assets associated with this event
 * @param source Source of the event
 * @param externalId External ID of the event
 */
final case class EventCreate(
    startTime: Option[Instant] = None,
    endTime: Option[Instant] = None,
    description: Option[String] = None,
    `type`: Option[String] = None,
    subtype: Option[String] = None,
    metadata: Option[Map[String, String]] = None,
    assetIds: Option[Seq[Long]] = None,
    source: Option[String] = None,
    externalId: Option[String] = None
) extends WithExternalId

/**
* @constructor Use to update events
 * @param id ID of the event to update
 * @param startTime Start time of the updated event
 * @param endTime End time of the updated event
 * @param description Description of the updated event
 * @param `type` Type of the updated event
 * @param subtype Subtype of the updated event
 * @param metadata Metadata associated with the updated event
 * @param assetIds IDs of assets associated with this updated event
 * @param source Source of the event
 * @param externalId External ID of the event
 */
final case class EventUpdate(
    id: Long = 0,
    startTime: Option[Setter[Instant]] = None,
    endTime: Option[Setter[Instant]] = None,
    description: Option[Setter[String]] = None,
    `type`: Option[Setter[String]] = None,
    subtype: Option[Setter[String]] = None,
    metadata: Option[NonNullableSetter[Map[String, String]]] = None,
    assetIds: Option[NonNullableSetter[Seq[Long]]] = None,
    source: Option[Setter[String]] = None,
    externalId: Option[Setter[String]] = None
) extends WithId[Long]

/**
* @constructor Use to filter events matching certain criteria
 * @param startTime Match events in with start times in this range
 * @param endTime Match events with end times in this range
 * @param metadata Match events with this metadata
 * @param assetIds Match events connected to these asset IDs
 * @param source Match events with this source
 * @param `type` Match events with this type
 * @param subtype Match events with this subtype
 * @param createdTime Match events created within this time range
 * @param lastUpdatedTime Match events last updated within this time range
 * @param externalIdPrefix Match events whose externalID begings with this prefix
 */
final case class EventsFilter(
    startTime: Option[TimeRange] = None,
    endTime: Option[TimeRange] = None,
    metadata: Option[Map[String, String]] = None,
    assetIds: Option[Seq[Long]] = None,
    source: Option[String] = None,
    `type`: Option[String] = None,
    subtype: Option[String] = None,
    createdTime: Option[TimeRange] = None,
    lastUpdatedTime: Option[TimeRange] = None,
    externalIdPrefix: Option[String] = None
)

/**
* @constructor Use to search for events matching a description
 * @param description Description to match events
 */
final case class EventsSearch(
    description: Option[String] = None
)

/**
* @constructor Query for events matching a filter or search
 * @param filter Filter for events. See EventsFilter
 * @param search Search for events. See EventsSearch
 * @param limit Optional maximum number of results to return
 */
final case class EventsQuery(
    filter: Option[EventsFilter] = None,
    search: Option[EventsSearch] = None,
    limit: Int = 100
) extends SearchQuery[EventsFilter, EventsSearch]
