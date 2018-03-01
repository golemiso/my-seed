//package controllers
//
//import java.util.UUID
//
//import domain.{ Grouping, GroupingID, GroupingRepository }
//import play.api.mvc._
//import play.api.libs.json.{ Json, OFormat }
//
//import scala.concurrent.ExecutionContext
//
//class GroupingController(mcc: MessagesControllerComponents, repository: GroupingRepository)(implicit ec: ExecutionContext) extends MessagesAbstractController(mcc) {
//
//  def get(id: UUID): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
//    repository.resolveBy(GroupingID(id)).map { grouping =>
//      Ok(Json.toJson(GroupingResource.create(grouping)))
//    }
//  }
//
//  def delete(id: UUID): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
//    repository.deleteBy(GroupingID(id)).map { _ =>
//      NoContent
//    }
//  }
//
//  def getAll: Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
//    repository.resolve.map { groupings =>
//      Ok(Json.toJson(GroupingResource.create(groupings)))
//    }
//  }
//
//  def post(): Action[GroupingResource] = Action.async(parse.json[GroupingResource]) { implicit request: MessagesRequest[GroupingResource] =>
//    val groupingResource = request.body
//    repository.store(Grouping(GroupingID.generate, groupingResource.name)).map { grouping =>
//      Created(Json.toJson(GroupingResource.create(grouping)))
//    }
//  }
//
//  def put(id: UUID): Action[GroupingResource] = Action.async(parse.json[GroupingResource]) { implicit request: MessagesRequest[GroupingResource] =>
//    val groupingResource = request.body
//    repository.store(Grouping(GroupingID(id), groupingResource.name)).map { grouping =>
//      Accepted(Json.toJson(GroupingResource.create(grouping)))
//    }
//  }
//}
//
//case class GroupingResource(id: Option[UUID], name: String)
//object GroupingResource {
//  implicit val format: OFormat[GroupingResource] = Json.format[GroupingResource]
//  def create(grouping: Grouping): GroupingResource = GroupingResource(Some(grouping.id.value), grouping.name)
//  def create(grouping: Seq[Grouping]): Seq[GroupingResource] = grouping.map(create)
//}
