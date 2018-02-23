//package config
//
//import scala.util.{Failure, Success}
//import scala.concurrent.duration._
//import scala.concurrent.{Await, Future}
//import play.api.inject.ApplicationLifecycle
//import play.api.{Configuration, Logger}
//import reactivemongo.api.{DefaultDB, MongoConnection, MongoConnectionOptions, MongoDriver, ReadPreference, ScramSha1Authentication}
//import reactivemongo.api.commands.WriteConcern
//import reactivemongo.core.nodeset.Authenticate
//
//final class MongoApi(
//                       name: String,
//                       parsedUri: MongoConnection.ParsedURI,
//                       dbName: String,
//                       strictMode: Boolean,
//                       configuration: Configuration,
//                       applicationLifecycle: ApplicationLifecycle
//                                   ) {
//  import MongoApi._
//
//  lazy val driver: MongoDriver = new MongoDriver(Some(configuration.underlying), None)
//  lazy val connection: MongoConnection = {
//    val con = driver.connection(parsedUri, strictMode).get
//    registerDriverShutdownHook(con, driver)
//    con
//  }
//
//  def database: Future[DefaultDB] = {
//    import play.api.libs.concurrent.Execution.Implicits.defaultContext
//
//    logger.debug(s"Resolving database '$dbName' ... ($parsedUri)")
//
//    connection.database(dbName)
//  }
//
//  private def registerDriverShutdownHook(connection: MongoConnection, mongoDriver: MongoDriver): Unit = {
//    import scala.concurrent.ExecutionContext.Implicits.global
//
//    applicationLifecycle.addStopHook { () =>
//      logger.info("ReactiveMongoApi stopping...")
//
//      Await.ready(connection.askClose()(10.seconds).map { _ =>
//        logger.info("ReactiveMongoApi connections are stopped")
//      }.andThen {
//        case Failure(reason) =>
//          reason.printStackTrace()
//          mongoDriver.close() // Close anyway
//
//        case _ => mongoDriver.close()
//      }, 12.seconds)
//    }
//  }
//}
//
//private[config] object MongoApi {
//  val DefaultPort = 27017
//  val DefaultHost = "localhost:27017"
//
//  private[config] val logger = Logger(this.getClass)
//
//  private def parseLegacy(configuration: Configuration): MongoConnection.ParsedURI = {
//    val db = configuration.getOptional[String]("mongodb.db").getOrElse(
//      throw configuration.globalError(
//        "Missing configuration key 'mongodb.db'!"
//      )
//    )
//
//    val uris = configuration.getOptional[List[String]]("mongodb.servers").getOrElse(List(DefaultHost))
//
//    val nodes = uris.map { uri =>
//      uri.split(':').toList match {
//        case host :: port :: Nil => host -> {
//          try {
//            val p = port.toInt
//            if (p > 0 && p < 65536) p
//            else throw configuration.globalError(
//              s"Could not parse URI '$uri': invalid port '$port'"
//            )
//          } catch {
//            case _: NumberFormatException => throw configuration.globalError(
//              s"Could not parse URI '$uri': invalid port '$port'"
//            )
//          }
//        }
//        case host :: Nil => host -> DefaultPort
//        case _ => throw configuration.globalError(
//          s"Could not parse host '$uri'"
//        )
//      }
//    }
//
//    var opts = MongoConnectionOptions()
//
//    configuration.getOptional[Int]("mongodb.options.nbChannelsPerNode").
//      foreach { nb => opts = opts.copy(nbChannelsPerNode = nb) }
//
//    configuration.getOptional[String]("mongodb.options.authSource").
//      foreach { src => opts = opts.copy(authSource = Some(src)) }
//
//    configuration.getOptional[Int]("mongodb.options.connectTimeoutMS").
//      foreach { ms => opts = opts.copy(connectTimeoutMS = ms) }
//
//    configuration.getOptional[Boolean]("mongodb.options.tcpNoDelay").
//      foreach { delay => opts = opts.copy(tcpNoDelay = delay) }
//
//    configuration.getOptional[Boolean]("mongodb.options.keepAlive").
//      foreach { keepAlive => opts = opts.copy(keepAlive = keepAlive) }
//
//    configuration.getOptional[Boolean]("mongodb.options.ssl.enabled").
//      foreach { ssl => opts = opts.copy(sslEnabled = ssl) }
//
//    configuration.getOptional[Boolean]("mongodb.options.ssl.allowsInvalidCert").
//      foreach { allows => opts = opts.copy(sslAllowsInvalidCert = allows) }
//
//    configuration.getOptional[String]("mongodb.options.authMode").foreach {
//      case "scram-sha1" =>
//        opts = opts.copy(authMode = ScramSha1Authentication)
//
//      case _ => ()
//    }
//
//    configuration.getOptional[String]("mongodb.options.writeConcern").foreach {
//      case "unacknowledged" =>
//        opts = opts.copy(writeConcern = WriteConcern.Unacknowledged)
//
//      case "acknowledged" =>
//        opts = opts.copy(writeConcern = WriteConcern.Acknowledged)
//
//      case "journaled" =>
//        opts = opts.copy(writeConcern = WriteConcern.Journaled)
//
//      case "default" =>
//        opts = opts.copy(writeConcern = WriteConcern.Default)
//
//      case _ => ()
//    }
//
//    val IntRe = "^([0-9]+)$".r
//
//    configuration.getOptional[String]("mongodb.options.writeConcernW").foreach {
//      case "majority" => opts = opts.copy(writeConcern = opts.writeConcern.
//        copy(w = WriteConcern.Majority))
//
//      case IntRe(str) => opts = opts.copy(writeConcern = opts.writeConcern.
//        copy(w = WriteConcern.WaitForAknowledgments(str.toInt)))
//
//      case tag => opts = opts.copy(writeConcern = opts.writeConcern.
//        copy(w = WriteConcern.TagSet(tag)))
//
//    }
//
//    configuration.getOptional[Boolean]("mongodb.options.writeConcernJ").foreach { jed =>
//      opts = opts.copy(writeConcern = opts.writeConcern.copy(j = jed))
//    }
//
//    configuration.getOptional[Int]("mongodb.options.writeConcernTimeout").foreach { ms =>
//      opts = opts.copy(writeConcern = opts.writeConcern.copy(
//        wtimeout = Some(ms)
//      ))
//    }
//
//    configuration.getOptional[String]("mongodb.options.readPreference").foreach {
//      case "primary" =>
//        opts = opts.copy(readPreference = ReadPreference.primary)
//
//      case "primaryPreferred" =>
//        opts = opts.copy(readPreference = ReadPreference.primaryPreferred)
//
//      case "secondary" =>
//        opts = opts.copy(readPreference = ReadPreference.secondary)
//
//      case "secondaryPreferred" =>
//        opts = opts.copy(readPreference = ReadPreference.secondaryPreferred)
//
//      case "nearest" =>
//        opts = opts.copy(readPreference = ReadPreference.nearest)
//
//      case _ => ()
//    }
//
//    val authenticate: Option[Authenticate] = for {
//      username <- configuration.getOptional[String]("mongodb.credentials.username")
//      password <- configuration.getOptional[String]("mongodb.credentials.password")
//    } yield Authenticate(opts.authSource.getOrElse(db), username, password)
//
//    MongoConnection.ParsedURI(
//      hosts = nodes,
//      options = opts,
//      ignoredOptions = Nil,
//      db = Some(db),
//      authenticate = authenticate
//    )
//  }
//
//  private def parseURI(key: String, uri: String): Option[(MongoConnection.ParsedURI, String)] = MongoConnection.parseURI(uri) match {
//    case Success(parsedURI) => parsedURI.db match {
//      case Some(db) => Some(parsedURI -> db)
//      case _ => {
//        logger.warn(s"Missing database name in '$key': $uri")
//        None
//      }
//    }
//
//    case Failure(e) => {
//      logger.warn(s"Invalid connection URI '$key': $uri", e)
//      None
//    }
//  }
//
//  private[config] case class BindingInfo(
//                                                 strict: Boolean,
//                                                 database: String,
//                                                 uri: MongoConnection.ParsedURI
//                                               )
//
//  private[config] def parseConfiguration(configuration: Configuration): Seq[(String, BindingInfo)] = configuration.getOptional[Configuration]("mongodb") match {
//    case Some(subConf) => {
//      val parsed = Seq.newBuilder[(String, BindingInfo)]
//
//      subConf.getOptional[String]("uri").map("mongodb.uri" -> _).
//        orElse(subConf.getOptional[String]("default.uri").
//          map("mongodb.default.uri" -> _)).flatMap {
//        case (key, uri) => parseURI(key, uri).map {
//          case (u, db) =>
//            val strictKey = s"${key.dropRight(4)}.connection.strictUri"
//            "default" -> BindingInfo(
//              strict = configuration.getOptional[Boolean](strictKey).getOrElse(false),
//              database = db,
//              uri = u
//            )
//        }
//      }.foreach { parsed += _ }
//
//      val other = subConf.entrySet.iterator.collect {
//        case (key, value) if key.endsWith(".uri") && value.unwrapped.isInstanceOf[String] => s"mongodb.$key" -> value.unwrapped.asInstanceOf[String]
//      }
//
//      other.foreach {
//        case (key, input) => parseURI(key, input).foreach {
//          case (u, db) =>
//            val baseKey = key.dropRight(4)
//            val strictKey = s"$baseKey.connection.strictUri"
//            val name = baseKey.drop(8)
//
//            parsed += name -> BindingInfo(
//              strict = configuration.getOptional[Boolean](strictKey).getOrElse(false),
//              database = db,
//              uri = u
//            )
//        }
//      }
//
//      val mongoConfigs = parsed.result()
//
//      if (mongoConfigs.isEmpty) {
//        logger.warn("No configuration in the 'mongodb' section")
//      }
//
//      mongoConfigs
//    }
//
//    case _ => {
//      logger.warn("No 'mongodb' section found in the configuration")
//      Seq.empty
//    }
//  }
//}
