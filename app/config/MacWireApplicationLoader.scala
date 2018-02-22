package config

import com.softwaremill.macwire._
import controllers.{AssetsComponents, HomeController, PlayerController}
import domain.PlayerRepository
import infra.MongoPlayerRepository
import play.api.ApplicationLoader.Context
import play.api.http.FileMimeTypes
import play.api.i18n._
import play.api.mvc._
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import router.Routes

import scala.concurrent.ExecutionContext

class MacWireApplicationLoader extends ApplicationLoader {
  def load(context: Context): Application = new MacWireComponents(context).application
}

class MacWireComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with AssetsComponents
  with I18nComponents
  with play.filters.HttpFiltersComponents {

  // set up logger
  LoggerConfigurator(context.environment.classLoader).foreach {
    _.configure(context.environment, context.initialConfiguration, Map.empty)
  }

  lazy val messagesActionBuilder: MessagesActionBuilder = wire[MacWireMessagesActionBuilder]
  lazy val messagesControllerComponents: MessagesControllerComponents = wire[MacWireMessagesControllerComponents]

  lazy val playerRepository: PlayerRepository = wire[MongoPlayerRepository]

  lazy val homeController = wire[HomeController]
  lazy val playerController = wire[PlayerController]

  lazy val router: Router = {
    // add the prefix string in local scope for the Routes constructor
    val prefix: String = "/"
    wire[Routes]
  }
}

case class MacWireMessagesControllerComponents (
                                                           messagesActionBuilder: MessagesActionBuilder,
                                                           actionBuilder: DefaultActionBuilder,
                                                           parsers: PlayBodyParsers,
                                                           messagesApi: MessagesApi,
                                                           langs: Langs,
                                                           fileMimeTypes: FileMimeTypes,
                                                           executionContext: scala.concurrent.ExecutionContext
                                                         ) extends MessagesControllerComponents

class MacWireMessagesActionBuilder(parser: BodyParser[AnyContent], messagesApi: MessagesApi)(implicit ec: ExecutionContext)
  extends MessagesActionBuilderImpl(parser, messagesApi) with MessagesActionBuilder {
  def this(parser: BodyParsers.Default, messagesApi: MessagesApi)(implicit ec: ExecutionContext) = {
    this(parser: BodyParser[AnyContent], messagesApi)
  }
}
