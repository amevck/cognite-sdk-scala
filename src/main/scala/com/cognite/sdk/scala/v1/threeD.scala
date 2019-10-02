package com.cognite.sdk.scala.v1

import java.time.Instant

import com.cognite.sdk.scala.common.{NonNullableSetter, WithId}

/**
* @constructor 3D Models
 * @param name Name of this model
 * @param id ID of this model
 * @param createdTime Time this model was created in CDF
 * @param metadata Metadata associated with this model
 */
final case class ThreeDModel(
    name: String,
    id: Long = 0,
    createdTime: Instant = Instant.ofEpochMilli(0),
    metadata: Option[Map[String, String]] = None
) extends WithId[Long]

/**
* @constructor Use for creation of a 3D model
 * @param name Name of the model
 * @param metadata Metadata associated with this model
 */
final case class ThreeDModelCreate(
    name: String,
    metadata: Option[Map[String, String]] = None
)

/**
* @constructor Use for updating a 3D model
 * @param id ID of the model to update
 * @param name Name of this model
 * @param metadata Metadata associated with this model
 */
final case class ThreeDModelUpdate(
    id: Long = 0,
    name: Option[NonNullableSetter[String]] = None,
    metadata: Option[NonNullableSetter[Map[String, String]]] = None
) extends WithId[Long]

/**
* @constructor Camera
 * @param target Camera target
 * @param position Camera position
 */
final case class Camera(
    target: Option[Array[Double]],
    position: Option[Array[Double]]
)

/**
* @constructor Maximum and minimum coordinates of the model in 3D space
 * @param max Maximum coordinates of the model, as [x,y,z]
 * @param min Minimum coordinates of the model, as [x,y,z]
 */
final case class BoundingBox(
    max: Option[Array[Double]],
    min: Option[Array[Double]]
)

/**
* @constructor Filter for node properties. Only nodes matching these properties will be listed by a query
 * @param properties JSON key-value pairs of category: PropertyCategory
 */
final case class Properties(
    properties: Map[String, PropertyCategory]
)

/**
* @constructor Filter for node properties, nested within a Properties object
 * @param pairs Key-value pairs of property: value. Used as value in category: PropertyCategory key-value pair
 */
final case class PropertyCategory(
    pairs: Map[String, String]
)

/**
* @constructor 3D Revision
 * @param id ID of the revision
 * @param fileId File ID of the revision
 * @param published True if revision is marked as published
 * @param rotation Rotation of the model
 * @param camera Initial camera position and target
 * @param status Status of the revision {Queued, Processing, Done, Failed}
 * @param metadata Application specific metadata
 * @param thumbnailThreedFileId 3D file id of a thumbnail for the revision. List files for this revision to get the ID.
 * @param thumbnailURL URL where revision thumbnail is stored
 * @param assetMappingCount Number of asset mappings associated with this revision
 * @param createdTime Creation time of the resource
 */
final case class ThreeDRevision(
    id: Long = 0,
    fileId: Long,
    published: Boolean = false,
    rotation: Option[Array[Double]] = None,
    camera: Option[Camera] = None,
    status: String = "",
    metadata: Option[Map[String, String]] = None,
    thumbnailThreedFileId: Option[Long] = None,
    thumbnailURL: Option[String] = None,
    assetMappingCount: Long = 0,
    createdTime: Instant = Instant.ofEpochMilli(0)
) extends WithId[Long]

/**
* @constructor Use to create 3D Model Revisions
 * @param published Whether the revision is marked as published
 * @param rotation Model rotation
 * @param metadata Custom application metadata
 * @param camera Initial camera position and target
 * @param fileId ID of file uploaded to Files API. Can not be updated after creation
 */
final case class ThreeDRevisionCreate(
    published: Boolean,
    rotation: Option[Array[Double]] = None,
    metadata: Option[Map[String, String]] = None,
    camera: Option[Camera] = None,
    fileId: Long
)

/**
* @constructor Update a 3D Model Revision
 * @param id Server generated ID
 * @param published Whether the revision is marked published
 * @param rotation Model rotation
 * @param camera Camera position and target
 * @param metadata Model metadata
 */
final case class ThreeDRevisionUpdate(
    id: Long = 0,
    published: Option[NonNullableSetter[Boolean]],
    rotation: Option[NonNullableSetter[Array[Double]]] = None,
    camera: Option[NonNullableSetter[Camera]] = None,
    metadata: Option[NonNullableSetter[Map[String, String]]] = None
) extends WithId[Long]

/**
* @constructor Asset Mapping
 * @param nodeId ID of this node
 * @param assetId ID of the associated asset
 * @param treeIndex Position of this node in 3D hierarchy
 * @param subtreeSize Number of nodes in the subtree of this node (including this node)
 */
final case class ThreeDAssetMapping(
    nodeId: Long,
    assetId: Long,
    treeIndex: Option[Long],
    subtreeSize: Option[Long]
)

/**
* @constructor Create a 3D Asset Mapping
 * @param nodeId ID of the node
 * @param assetId ID of the associated asset
 */
final case class ThreeDAssetMappingCreate(
    nodeId: Long,
    assetId: Long
)

/**
* @constructor Node within a 3D model hierarchy
 * @param id ID of the node
 * @param treeIndex Index of this node within its 3D hierarchy
 * @param parentId ID of this node's parent node
 * @param depth Depth of this node in its tree. Root node has depth 0
 * @param name Name of this node
 * @param subtreeSize Number of descendants of this node
 * @param properties Key-value properties of this node
 * @param boundingBox Maximum and minimum coordinates of the node in 3D space
 */
final case class ThreeDNode(
    id: Long,
    treeIndex: Long,
    parentId: Option[Long],
    depth: Long,
    name: String,
    subtreeSize: Long,
    properties: Option[Properties],
    boundingBox: BoundingBox
)

/**
* @constructor Filter for nodes matching certain criteria
 * @param limit Maximum number of nodes to return when filtering
 * @param depth Depth of subtree to match
 * @param nodeId Node ID to match
 * @param properties Properties to match
 */
final case class ThreeDNodeFilter(
    limit: Int,
    depth: Int,
    nodeId: Long,
    properties: Properties
)
