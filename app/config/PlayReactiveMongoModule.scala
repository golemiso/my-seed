//package config
//
//import play.api._
//import play.api.inject.{ApplicationLifecycle, Binding, BindingKey, Module}
//import play.modules.reactivemongo.NamedDatabaseImpl
//
//final class PlayReactiveMongoModule extends Module {
//  import MongoApi.BindingInfo
//
//  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = apiBindings(MongoApi.parseConfiguration(configuration), configuration)
//
//  private def apiBindings(info: Seq[(String, BindingInfo)], cf: Configuration): Seq[Binding[ReactiveMongoApi]] = info.flatMap {
//    case (name, BindingInfo(strict, db, uri)) =>
//      val provider = new ReactiveMongoProvider(
//        new MongoApi(name, uri, db, strict, cf, _)
//      )
//      val bs = List(ReactiveMongoModule.key(name).to(provider))
//
//      if (name == "default") {
//        bind[ReactiveMongoApi].to(provider) :: bs
//      } else bs
//  }
//}
//
//object ReactiveMongoModule {
//  private[config] def key(name: String): BindingKey[ReactiveMongoApi] =
//    BindingKey(classOf[ReactiveMongoApi]).
//      qualifiedWith(new NamedDatabaseImpl(name))
//
//}
//
///**
//  * Cake pattern components.
//  */
//trait ReactiveMongoComponents {
//  def reactiveMongoApi: ReactiveMongoApi
//}
//
///**
//  * Inject provider for named databases.
//  */
//private[config] final class ReactiveMongoProvider(
//                                                          factory: ApplicationLifecycle => ReactiveMongoApi
//                                                        ) extends Provider[ReactiveMongoApi] {
//  @Inject private var applicationLifecycle: ApplicationLifecycle = _
//  lazy val get: ReactiveMongoApi = factory(applicationLifecycle)
//}
