package config

import com.softwaremill.macwire._
import com.typesafe.config.{ Config, ConfigFactory }
import controllers.{ AssetsComponents, HomeController, PlayerController }
import domain.PlayerRepository
import infra.mongodb.MongoDBPlayerRepository
import play.api.ApplicationLoader.Context
import play.api.http.FileMimeTypes
import play.api.i18n._
import play.api.mvc._
import play.api.routing.Router
import play.api.{ Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator }
import reactivemongo.api.{ DefaultDB, MongoConnection, MongoDriver }
import router.Routes

import scala.concurrent.{ ExecutionContext, Future }

class MacWireApplicationLoader extends ApplicationLoader {
  def load(context: Context): Application = new MacWireComponents(context).application
}

class MacWireComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with DBComponents
  with AssetsComponents
  with I18nComponents
  with play.filters.HttpFiltersComponents {

  // set up logger
  LoggerConfigurator(context.environment.classLoader).foreach {
    _.configure(context.environment, context.initialConfiguration, Map.empty)
  }

  lazy val messagesActionBuilder: MessagesActionBuilder = wire[MacWireMessagesActionBuilder]
  lazy val messagesControllerComponents: MessagesControllerComponents = wire[MacWireMessagesControllerComponents]

  lazy val playerRepository: PlayerRepository = wire[MongoDBPlayerRepository]

  lazy val homeController: HomeController = wire[HomeController]
  lazy val playerController: PlayerController = wire[PlayerController]

  lazy val router: Router = {
    // add the prefix string in local scope for the Routes constructor
    val prefix: String = "/"
    wire[Routes]
  }
}

case class MacWireMessagesControllerComponents(
  messagesActionBuilder: MessagesActionBuilder,
  actionBuilder: DefaultActionBuilder,
  parsers: PlayBodyParsers,
  messagesApi: MessagesApi,
  langs: Langs,
  fileMimeTypes: FileMimeTypes,
  executionContext: scala.concurrent.ExecutionContext) extends MessagesControllerComponents

class MacWireMessagesActionBuilder(parser: BodyParser[AnyContent], messagesApi: MessagesApi)(implicit ec: ExecutionContext)
  extends MessagesActionBuilderImpl(parser, messagesApi) with MessagesActionBuilder {
  def this(parser: BodyParsers.Default, messagesApi: MessagesApi)(implicit ec: ExecutionContext) = {
    this(parser: BodyParser[AnyContent], messagesApi)
  }
}

trait DBComponents {
  lazy val config: Config = ConfigFactory.load
  lazy val driver: MongoDriver = new MongoDriver(Some(config), None)
  lazy val db: Future[DefaultDB] = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val parsedUri = MongoConnection.parseURI(config.getString("mongodb.uri"))

    for {
      uri <- Future.fromTry(parsedUri)
      con = driver.connection(uri)
      dn <- Future(uri.db.get)
      db <- con.database(dn)
    } yield db
  }
}
