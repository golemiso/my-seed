package controllers

import org.scalatestplus.play.{BaseOneAppPerSuite, BaseOneAppPerTest, PlaySpec}
import org.scalatest.{AsyncFunSpec, FunSpec}
import play.api.test.FakeRequest
import config.MacWireApplicationFactory
import play.api.test.Helpers._
import play.api.test._

class PlayerControllerSpec extends FunSpec with BaseOneAppPerTest with MacWireApplicationFactory {
  describe("PlayerController") {
    describe("findBy RoleId") {
      it("returns role") {
        route(app, FakeRequest(GET, "/boum")).exists(r => status(r) == NOT_FOUND)
      }
    }
  }
}
