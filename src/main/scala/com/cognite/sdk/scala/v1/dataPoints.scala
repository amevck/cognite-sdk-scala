package com.cognite.sdk.scala.v1

import com.cognite.sdk.scala.common.{AggregateDataPoint, DataPoint, StringDataPoint}

final private[sdk] case class DataPointsById(
    id: Long,
    datapoints: Seq[DataPoint]
)

final private[sdk] case class DataPointsByExternalId(
    externalId: String,
    datapoints: Seq[DataPoint]
)

final private[sdk] case class DataPointsByIdResponse(
    id: Long,
    externalId: Option[String],
    isString: Boolean,
    datapoints: Seq[DataPoint]
)

final private[sdk] case class DataPointsByExternalIdResponse(
    id: Long,
    externalId: String,
    isString: Boolean,
    datapoints: Seq[DataPoint]
)

final private[sdk] case class StringDataPointsByIdResponse(
    id: Long,
    externalId: Option[String],
    isString: Boolean,
    datapoints: Seq[StringDataPoint]
)

final private[sdk] case class StringDataPointsByExternalIdResponse(
    id: Long,
    externalId: String,
    isString: Boolean,
    datapoints: Seq[StringDataPoint]
)

final private[sdk] case class StringDataPointsById(
    id: Long,
    datapoints: Seq[StringDataPoint]
)

final private[sdk] case class StringDataPointsByExternalId(
    externalId: String,
    datapoints: Seq[StringDataPoint]
)

final private[sdk] case class DeleteRangeById(
    id: Long,
    inclusiveBegin: Long,
    exclusiveEnd: Long
)

final private[sdk] case class DeleteRangeByExternalId(
    externalId: String,
    inclusiveBegin: Long,
    exclusiveEnd: Long
)

final private[sdk] case class QueryRangeById(
    id: Long,
    start: String,
    end: String,
    limit: Option[Int] = None,
    granularity: Option[String] = None,
    aggregates: Option[Seq[String]] = None
)

final private[sdk] case class QueryRangeByExternalId(
    externalId: String,
    start: String,
    end: String,
    limit: Option[Int] = None,
    granularity: Option[String] = None,
    aggregates: Option[Seq[String]] = None
)

final private[sdk] case class QueryAggregatesByIdResponse(
    id: Long,
    externalId: Option[String],
    datapoints: Seq[AggregateDataPoint]
)
