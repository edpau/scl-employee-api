package contract

import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import utils.ApiError

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ContractController @Inject()(
  cc: ControllerComponents,
  contractService: ContractService
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getAllContracts: Action[AnyContent] = Action.async { implicit request =>
    contractService.getAllContracts().map { contracts =>
      Ok(Json.toJson(contracts))
    }
  }

  def create: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreateContractDto] match {

      case JsSuccess(dto, _) =>
        contractService.createContract(dto).map {
          case Right(response) => Created(Json.toJson(response))
          case Left(error) => error.toResult
        }

      case e: JsError =>
        Future.successful(ApiError.InvalidJson(e).toResult)
    }
  }

}
