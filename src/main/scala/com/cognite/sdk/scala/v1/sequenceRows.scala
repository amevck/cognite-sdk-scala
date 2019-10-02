package com.cognite.sdk.scala.v1

import io.circe.Json

/**
* @constructor ID of a column in a sequence
 * @param externalId External ID of the column
 */
final case class SequenceColumnId(externalId: String)

/**
* @constructor Row in a sequence
 * @param rowNumber Index of the row in the sequence
 * @param values Values of the row as json objects
 */
final case class SequenceRow(rowNumber: Long, values: Seq[Json])
private[sdk] final case class SequenceRowsInsertById(id: Long, columns: Seq[String], rows: Seq[SequenceRow])
private[sdk] final case class SequenceRowsInsertByExternalId(
    externalId: String,
    columns: Seq[String],
    rows: Seq[SequenceRow]
)
private[sdk] final case class SequenceRowsDeleteById(id: Long, rows: Seq[Long])
private[sdk] final case class SequenceRowsDeleteByExternalId(externalId: String, rows: Seq[Long])
private[sdk] final case class SequenceRowsQueryById(
    id: Long,
    start: Long,
    end: Long,
    limit: Option[Int],
    columns: Option[Seq[String]]
)
private[sdk] final case class SequenceRowsQueryByExternalId(
    externalId: String,
    start: Long,
    end: Long,
    limit: Option[Int],
    columns: Option[Seq[String]]
)
private[sdk] final case class SequenceRowsResponse(
    columns: Seq[SequenceColumnId], // this can be empty if no data is returned
    rows: Seq[SequenceRow]
)
