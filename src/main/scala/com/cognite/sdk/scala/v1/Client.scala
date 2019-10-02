package com.cognite.sdk.scala.v1

import BuildInfo.BuildInfo
import cats.Monad
import cats.implicits._
import com.cognite.sdk.scala.common.{Auth, InvalidAuthentication, Login}
import com.cognite.sdk.scala.v1.resources.{
  Assets,
  DataPointsResource,
  Events,
  Files,
  RawDatabases,
  RawRows,
  RawTables,
  SequenceRows,
  SequencesResource,
  ThreeDAssetMappings,
  ThreeDModels,
  ThreeDNodes,
  ThreeDRevisions,
  TimeSeriesResource
}
import com.softwaremill.sttp._

import scala.concurrent.duration._

private[sdk] final case class RequestSession[F[_]: Monad](
    applicationName: String,
    baseUri: Uri,
    sttpBackend: SttpBackend[F, _],
    auth: Auth
) {
  def send[R](r: RequestT[Empty, String, Nothing] => RequestT[Id, R, Nothing]): F[Response[R]] =
    r(
      sttp
        .readTimeout(90.seconds)
    ).send()(sttpBackend, implicitly)

  def sendCdf[R](
      r: RequestT[Empty, String, Nothing] => RequestT[Id, R, Nothing],
      contentType: String = "application/json",
      accept: String = "application/json"
  ): F[R] =
    r(
      sttp
        .followRedirects(false)
        .auth(auth)
        .contentType(contentType)
        .header("accept", accept)
        .header("x-cdp-sdk", s"${BuildInfo.organization}-${BuildInfo.version}")
        .header("x-cdp-app", applicationName)
        .readTimeout(90.seconds)
        .parseResponseIf(_ => true)
    ).send()(sttpBackend, implicitly).map(_.unsafeBody)

  def map[R, R1](r: F[R], f: R => R1): F[R1] = r.map(f)
  def flatMap[R, R1](r: F[R], f: R => F[R1]): F[R1] = r.flatMap(f)
}

/**
 * @constructor Client to access CDF.
 * @param applicationName Application you are accessing
 * @param baseUri Base url to send requests to. Read form COGNITE_BASE_URL environment variable or defaults to
 *                https://api.cognitedata.com if not set
 * @param auth implicit Auth object authenticating use of the project
 * @param sttpBackend Implicit sttp backend, i.e. HttpUrlConnectionBackend()
 */
class GenericClient[F[_]: Monad, _](
    applicationName: String,
    baseUri: String =
      Option(System.getenv("COGNITE_BASE_URL")).getOrElse("https://api.cognitedata.com")
)(
    implicit auth: Auth,
    sttpBackend: SttpBackend[F, _]
) {

  /**
  * Project name
   */
  val project: String = auth.project.getOrElse {
    val sttpBackend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend(
      options = SttpBackendOptions.connectionTimeout(90.seconds)
    )
    val loginStatus = new Login(
      RequestSession(applicationName, uri"$baseUri", sttpBackend, auth)
    ).status()

    if (loginStatus.project.trim.isEmpty) {
      throw InvalidAuthentication()
    } else {
      loginStatus.project
    }
  }

  val requestSession =
    RequestSession(
      applicationName,
      uri"$baseUri/api/v1/projects/$project",
      sttpBackend,
      auth
    )
  val login = new Login[F](requestSession.copy(baseUri = uri"$baseUri"))

  /**
  * Access available assets in this project
   */
  val assets = new Assets[F](requestSession)

  /**
  * Access available events in this project
   */
  val events = new Events[F](requestSession)

  /**
  * Access available events in this project
   */
  val files = new Files[F](requestSession)

  /**
  * Access available time series in this project
   */
  val timeSeries = new TimeSeriesResource[F](requestSession)

  /**
  * Access available data points in this time series
   */
  val dataPoints = new DataPointsResource[F](requestSession)

  /**
  * Access available sequences in this project
   */
  val sequences = new SequencesResource[F](requestSession)

  /**
  * Access available sequence rows in this project
   */
  val sequenceRows = new SequenceRows[F](requestSession)

  /**
  * Access available raw databases in this project
   */
  val rawDatabases = new RawDatabases[F](requestSession)

  /**
  * Access available raw tables for a database
   * @param database Database for which to access the tables
   * @return The tables in database
   */
  def rawTables(database: String): RawTables[F] = new RawTables(requestSession, database)

  /**
  * Access available rows in a specified database and table
   * @param database Database in which to access the rows of a table
   * @param table Table within database for which to access the rows
   * @return Rows within database and table
   */
  def rawRows(database: String, table: String): RawRows[F] =
    new RawRows(requestSession, database, table)

  /**
  * Access the 3D models in this project
   */
  val threeDModels = new ThreeDModels[F](requestSession)

  /**
  * Access 3D revisions for a 3D model
   * @param modelId Model ID for which to access the revisions
   * @return ThreeDRevisions for modelId
   */
  def threeDRevisions(modelId: Long): ThreeDRevisions[F] =
    new ThreeDRevisions(requestSession, modelId)

  /**
  * Access 3D asset mappings for a particular model and revision
   * @param modelId Model for which to access the mappings
   * @param revisionId Revision for which to access the mappings
   * @return Asset mappings of modelId and revisionId
   */
  def threeDAssetMappings(modelId: Long, revisionId: Long): ThreeDAssetMappings[F] =
    new ThreeDAssetMappings(requestSession, modelId, revisionId)

  /**
  * Access the nodes in an asset hierarchy for a particular model and revision
   * @param modelId Model for which to access the nodes
   * @param revisionId Revision for which to access the nodes
   * @return Nodes in the hierarchy corresponding to modelId and revisionId
   */
  def threeDNodes(modelId: Long, revisionId: Long): ThreeDNodes[F] =
    new ThreeDNodes(requestSession, modelId, revisionId)
}

/**
* @constructor Client to access CDF.
 * @param applicationName Application you are accessing
 * @param baseUri Base url to send requests to. Read form COGNITE_BASE_URL environment variable or defaults to
 *                https://api.cognitedata.com if not set
 * @param auth implicit Auth object authenticating use of the project
 * @param sttpBackend Implicit sttp backend, i.e. HttpUrlConnectionBackend()
 */
final case class Client(
    applicationName: String,
    baseUri: String =
      Option(System.getenv("COGNITE_BASE_URL")).getOrElse("https://api.cognitedata.com")
)(
    implicit auth: Auth,
    sttpBackend: SttpBackend[Id, Nothing]
) extends GenericClient[Id, Nothing](applicationName, baseUri)
