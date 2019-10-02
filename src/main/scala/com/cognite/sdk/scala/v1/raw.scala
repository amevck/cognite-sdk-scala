package com.cognite.sdk.scala.v1

import java.time.Instant

import com.cognite.sdk.scala.common.WithId
import io.circe.Json

/**
* @constructor Raw Database
 * @param name Name of the database
 */
final case class RawDatabase(name: String) extends WithId[String] {
  override val id: String = this.name
}

/**
* @constructor Table in a raw database
 * @param name Name of the table
 */
final case class RawTable(name: String) extends WithId[String] {
  override val id: String = this.name
}

/**
* @constructor Row in a Raw table
 * @param key Unique row key
 * @param columns Row data
 * @param lastUpdatedTime Last time this row was updated
 */
final case class RawRow(
    key: String,
    columns: Map[String, Json],
    lastUpdatedTime: Option[Instant] = None
)

/**
* @constructor Unique row key
 * @param key String key
 */
final case class RawRowKey(key: String)
