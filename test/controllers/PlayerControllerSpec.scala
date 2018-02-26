package controllers

import org.scalatestplus.play.{BaseOneAppPerSuite, BaseOneAppPerTest, PlaySpec}
import org.scalatest.{AsyncFunSpec, FunSpec}
import play.api.test.FakeRequest
import config.MacWireApplicationFactory
import play.api.test.Helpers._
import play.api.test._

class PlayerControllerSpec extends FunSpec with BaseOneAppPerTest with MacWireApplicationFactory {
  describe("PlayerController") {
    describe("put") {
      it("200") {
        val Some(result) = route(app, FakeRequest(GET, "/players"))
        status(result) === OK
      }
    }
  }
  describe("PlayerController") {
    describe("getAll") {
      it("200") {
        val Some(result) = route(app, FakeRequest(GET, "/players"))
        status(result) === OK
      }
    }
  }
}
